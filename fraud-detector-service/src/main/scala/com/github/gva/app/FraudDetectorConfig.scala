package com.github.gva.app

case class FraudDetectorConfig(
  kafkaBootstrapServers: String = "kafka:9092",
  kafkaTopic: String = "events",
  redisHost: String = "redis",
  redisPort: String = "6379",
  redisPrefix: String = "bots",
  cassandra: String = "cassandra",
  cassandraTable: String = "fraud.bots",
  botTtlSeconds: Int = 10,
  botThresholdIntervalSlideSeconds: Int = 1,
  botThresholdIntervalSeconds: Int = 10,
  botThresholdNumOfEventsPerInterval: Int = 20
)

object FraudDetectorConfig {
  private val parser = new scopt.OptionParser[FraudDetectorConfig]("fraud-detector") {
    opt[Seq[String]]("kafka-bootstrap-servers")
      .required()
      .valueName("host1:port1,host2:port2,...")
      .action((xs, c) => c.copy(kafkaBootstrapServers = xs.mkString(",")))

    opt[String]("kafka-topic")
      .required()
      .action((x, c) => c.copy(kafkaTopic = x))

    opt[String]("redis")
      .required()
      .action { case (x: String, c: FraudDetectorConfig) =>
        val Array(host, port) = x.split(":")
        c.copy(redisHost = host, redisPort = port)
      }

    opt[String]("redis-prefix")
      .required()
      .action((x, c) => c.copy(redisPrefix = x))

    opt[String]("cassandra")
      .required()
      .action((x, c) => c.copy(cassandra = x))

    opt[String]("cassandra-table")
      .required()
      .action((x, c) => c.copy(cassandraTable = x))

    opt[Int]("bot-ttl-seconds")
      .action((x, c) => c.copy(botTtlSeconds = x))

    opt[Int]("bot-threshold-interval-slide-seconds")
      .action((x, c) => c.copy(botThresholdIntervalSlideSeconds = x))

    opt[Int]("bot-threshold-interval-seconds")
      .action((x, c) => c.copy(botThresholdIntervalSeconds = x))

    opt[Int]("bot-threshold-num-events-per-interval")
      .action((x, c) => c.copy(botThresholdNumOfEventsPerInterval = x))
  }

  def parse(args: Array[String]): Option[FraudDetectorConfig] = {
    parser.parse(args, FraudDetectorConfig())
  }
}
