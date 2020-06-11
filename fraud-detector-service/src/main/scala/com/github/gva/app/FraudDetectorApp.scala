package com.github.gva.app

import com.github.gva.core.{Event, FraudDetector}
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery}
import org.apache.spark.sql.types.{StringType, StructType}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

object FraudDetectorApp {
  def main(args: Array[String]): Unit = {
    val config = FraudDetectorConfig.parse(args) match {
      case Some(c) => c
      case None => throw new RuntimeException("Failed to parse passed arguments...")
    }
    val spark = buildSparkSession(config)
    val fraudDetector = new FraudDetector
    val eventsSchema = ScalaReflection.schemaFor[Event].dataType.asInstanceOf[StructType]
    val events = readEventsStream(spark, config, eventsSchema)
    val bots = fraudDetector.detectBots(
      events,
      windowDuration = config.botThresholdIntervalSeconds,
      slideDuration = config.botThresholdIntervalSlideSeconds,
      eventsPerWindowThreshold = config.botThresholdNumOfEventsPerInterval
    )
    val activeBotsQuery = writeActiveBotsQuery(bots, config)
    val botsHistoryQuery = writeBotsHistoryQuery(bots, config)
    activeBotsQuery.awaitTermination()
    botsHistoryQuery.awaitTermination()
  }

  private def buildSparkSession(config: FraudDetectorConfig) = {
    val spark = SparkSession
      .builder()
      .config("spark.redis.host", config.redisHost)
      .config("spark.redis.port", config.redisPort)
      .config("spark.cassandra.connection.host", config.cassandraHost)
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    spark
  }

  private def readEventsStream(
    spark: SparkSession,
    config: FraudDetectorConfig,
    eventsSchema: StructType
  ): DataFrame = {
    spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", config.kafkaBootstrapServers)
      .option("subscribe", config.kafkaTopic)
      .load()
      .select(col("value").cast(StringType))
      .withColumn("parsed", from_json(col("value"), eventsSchema))
      .select("parsed.*")
  }

  private def writeActiveBotsQuery(bots: DataFrame, config: FraudDetectorConfig): StreamingQuery = {
    bots
      .writeStream
      .option("checkpointLocation", config.redisCheckpoint)
      .outputMode(OutputMode.Complete)
      .foreachBatch { (batch: DataFrame, _) =>
        batch.show(truncate = false)
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
  }

  def writeBotsHistoryQuery(bots: DataFrame, config: FraudDetectorConfig): StreamingQuery = {
    bots
      .select("ip")
      .withColumn("marked_at", current_timestamp())
      .writeStream
      .option("checkpointLocation", config.cassandraCheckpoint)
      .outputMode(OutputMode.Update)
      .format("org.apache.spark.sql.cassandra")
      .option("keyspace", config.cassandraKeyspace)
      .option("table", config.cassandraTable)
      .start()
  }
}
