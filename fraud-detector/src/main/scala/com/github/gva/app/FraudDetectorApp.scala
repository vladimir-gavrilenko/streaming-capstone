package com.github.gva.app

import com.github.gva.core.FraudDetector
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode

object FraudDetectorApp {
  def main(args: Array[String]): Unit = {
    val config = FraudDetectorConfig.parse(args) match {
      case Some(c) => c
      case None => throw new RuntimeException
    }
    val spark = SparkSession.builder().getOrCreate()
    val botDetector = new FraudDetector

    val events = spark
      .readStream
      .format("kafka")
      .option("bootstrap.servers", config.kafkaSourceBootstrapServers)
      .load()

    val botsQuery = botDetector.detectBots(events)
      .writeStream
      .outputMode(OutputMode.Append())
      .format("console")
      .start()

    botsQuery.awaitTermination()
  }
}
