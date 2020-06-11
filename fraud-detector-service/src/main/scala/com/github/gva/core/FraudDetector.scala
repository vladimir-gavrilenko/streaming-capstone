package com.github.gva.core

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

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
      .withWatermark("timestamp", s"$slideDuration seconds")
      .groupBy(
        window(
          timeColumn = col("timestamp"),
          windowDuration = s"$windowDuration seconds",
          slideDuration = s"$slideDuration seconds",
          startTime = "0 seconds"),
        col("ip")
      )
      .count()
      .filter(col("count") > eventsPerWindowThreshold)
  }
}
