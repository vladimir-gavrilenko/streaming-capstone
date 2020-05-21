package com.github.gva.app

case class FraudDetectorConfig(
  kafkaSourceBootstrapServers: String = "kafka:9092",
  kafkaSourceTopic: String = "events"
)

object FraudDetectorConfig {
  private val parser = new scopt.OptionParser[FraudDetectorConfig]("fraud-detector") {
    opt[Seq[String]]("kafka-bootstrap-servers")
      .required()
      .valueName("host1:port1,host2:port2,...")
      .action((xs, c) => c.copy(kafkaSourceBootstrapServers = xs.mkString(",")))

    opt[String]("kafka-topic")
      .required()
      .action((x, c) => c.copy(kafkaSourceTopic = x))
  }

  def parse(args: Array[String]): Option[FraudDetectorConfig] = {
    parser.parse(args, FraudDetectorConfig())
  }
}
