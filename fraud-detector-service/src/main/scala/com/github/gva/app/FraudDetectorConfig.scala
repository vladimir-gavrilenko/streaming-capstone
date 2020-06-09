package com.github.gva.app

case class FraudDetectorConfig(
  kafkaBootstrapServers: String = "kafka:9092",
  kafkaTopic: String = "events",
  redisHost: String = "redis",
  redisPort: String = "6379",
  redisPrefix: String = "bots",
  redisCheckpoint: String = "file:///tmp/redis",
  cassandraHost: String = "cassandra",
  cassandraPort: String = "9042",
  cassandraKeyspace: String = "fraud",
  cassandraTable: String = "bots",
  cassandraCheckpoint: String = "file:///tmp/cassandra",
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

    opt[String]("redis-checkpoint")
      .required()
      .action((x, c) => c.copy(redisCheckpoint = x))

    opt[String]("cassandra")
      .required()
      .action { case (x: String, c: FraudDetectorConfig) =>
        val Array(host, port) = x.split(":")
        c.copy(cassandraHost = host, cassandraPort = port)
      }

    opt[String]("cassandra-table")
      .required()
      .action { case (x: String, c: FraudDetectorConfig) =>
        val Array(keyspace, table) = x.split("\\.")
        c.copy(cassandraKeyspace = keyspace, cassandraTable = table)
      }

    opt[String]("cassandra-checkpoint")
      .required()
      .action((x, c) => c.copy(cassandraCheckpoint = x))

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
