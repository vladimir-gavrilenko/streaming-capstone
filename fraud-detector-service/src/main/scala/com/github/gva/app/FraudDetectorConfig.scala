package com.github.gva.app

case class FraudDetectorConfig(
  kafkaBootstrapServers: String = "kafka:9092",
  kafkaTopic: String = "events",
  redisHost: String = "redis",
  redisPort: String = "6379",
  redisPrefix: String = "bots",
  cassandra: String = "cassandra",
  cassandraTable: String = "fraud.bots",
  botTtlSeconds: Int = 10
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

    opt[Int]("ttl")
      .action((x, c) => c.copy(botTtlSeconds = x))
  }

  def parse(args: Array[String]): Option[FraudDetectorConfig] = {
    parser.parse(args, FraudDetectorConfig())
  }
}
