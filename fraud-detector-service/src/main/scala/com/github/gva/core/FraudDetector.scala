package com.github.gva.core

import org.apache.spark.sql.functions._

import org.apache.spark.sql.DataFrame

/**
 * Bot detection algorithm:
 *  - More than 20 requests per 10 seconds for single IP.
 *  - IP should be whitelisted in 10 minutes if bot detection condition is not matched.
 *    It means IP should be deleted from Redis once host stops suspicious activity.
 */
class FraudDetector {
  def detectBots(
    events: DataFrame,
    windowDuration: Int = 10,
    slideDuration: Int = 1,
    eventsPerWindowThreshold: Int = 20
  ): DataFrame = {
    events
      .withColumn("timestamp", to_timestamp(col("epochSeconds")))
      .withWatermark("timestamp", s"$windowDuration seconds")
      .groupBy(
        col("ip"),
        window(col("timestamp"), s"$windowDuration seconds", s"$slideDuration seconds")
      )
      .count()
      .filter(col("count") > eventsPerWindowThreshold)
  }
}
