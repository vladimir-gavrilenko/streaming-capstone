package com.github.gva.app

import com.github.gva.core.{Event, FraudDetector}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.from_json
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.{StringType, StructType}

object FraudDetectorApp {
  def main(args: Array[String]): Unit = {
    val config = FraudDetectorConfig.parse(args) match {
      case Some(c) => c
      case None => throw new RuntimeException("Failed to parse passed arguments...")
    }
    val spark = SparkSession
      .builder()
      .config("spark.redis.host", config.redisHost)
      .config("spark.redis.port", config.redisPort)
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    val botDetector = new FraudDetector

    val kafkaStream = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", config.kafkaBootstrapServers)
      .option("subscribe", config.kafkaTopic)
      .option("kafkaConsumer.pollTimeoutMs", 2000)
      .option("fetchOffset.retryIntervalMs", 200)
      .load()

    val eventsSchema = ScalaReflection.schemaFor[Event].dataType.asInstanceOf[StructType]
    val events: DataFrame = kafkaStream
      .select(col("value").cast(StringType))
      .withColumn("parsed", from_json(col("value"), eventsSchema))
      .select("parsed.*")

    val botsQuery = botDetector.detectBots(events)

    val redisStream = botsQuery
      .writeStream
      .outputMode(OutputMode.Update) // AnalysisException: Complete output mode not supported when there are no streaming aggregations on streaming DataFrames/Datasets;
      .foreachBatch { (batch: DataFrame, _) =>
        batch
          .write
          .mode(SaveMode.Overwrite)
          .format("org.apache.spark.sql.redis")
          .option("ttl", config.botTtlSeconds)
          .option("table", config.redisPrefix)
          .option("key.column", "ip")
          .save()
      }
      .start()

    redisStream.awaitTermination()
  }
}
