package com.github.gva.core

import java.util.UUID.randomUUID

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.execution.streaming.MemoryStream
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.functions._
import org.scalatest.FunSuite

class FraudDetectorSuite extends FunSuite with DataFrameSuiteBase {
  test("regular ip addresses are not marked as bots") {
    val events = (1 to 100)
      .flatMap(s => Seq(
        Event(ip = "1.2.3.4", action = "click", epochSeconds = s),
        Event(ip = "5.6.7.8", action = "click", epochSeconds = s + 1)
      ))
    val bots = process(events)
    assert(bots.isEmpty)
  }

  test("suspicious ip addresses are marked as bots") {
    val events = (1 to 100)
      .flatMap(s => Seq(
        Event(ip = "1.2.3.4", action = "click", epochSeconds = s),
        Event(ip = "1.2.3.4", action = "click", epochSeconds = s + 1),
        Event(ip = "1.2.3.4", action = "click", epochSeconds = s + 2),
        Event(ip = "5.6.7.8", action = "click", epochSeconds = s)
      ))
    val bots = process(events)
    assert(!bots.filter(col("ip") === "1.2.3.4").isEmpty)
    assert(bots.filter(col("ip") === "5.6.7.8").isEmpty)
  }

  test("suspicious ip addresses can be removed from the bot list after some time") {
    val oneBot = (1 to 100)
      .flatMap(s => Seq(
        Event(ip = "1.2.3.4", action = "click", epochSeconds = s),
        Event(ip = "1.2.3.4", action = "click", epochSeconds = s + 1),
        Event(ip = "1.2.3.4", action = "click", epochSeconds = s + 2),
        Event(ip = "5.6.7.8", action = "click", epochSeconds = s)
      ))
    val noBots = (101 to 200)
      .flatMap(s => Seq(
        Event(ip = "1.2.3.4", action = "click", epochSeconds = s),
        Event(ip = "5.6.7.8", action = "click", epochSeconds = s)
      ))
    val bots = process(oneBot ++ noBots)
    val botStoppedTime = to_timestamp(lit(101))
    assert(!bots.filter(col("window.start") < botStoppedTime).isEmpty)
    assert(bots.filter(col("window.start") > botStoppedTime).isEmpty)
  }

  def process(eventsData: Seq[Event]): DataFrame = {
    import spark.implicits._
    val queryName = s"sink_${randomUUID()}".replace("-", "_")
    val fraudDetector = new FraudDetector
    val eventsSource = MemoryStream[Event]
    val events = eventsSource.toDF()
    val bots = fraudDetector.detectBots(events)
    val outputQuery = bots
      .writeStream
      .format("memory")
      .queryName(queryName)
      .outputMode(OutputMode.Append)
      .start()
    eventsSource.addData(eventsData)
    outputQuery.processAllAvailable()
    spark.table(queryName)
  }
}
