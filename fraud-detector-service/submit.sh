#!/usr/bin/env bash
set -eu

while [ ! -f "${CASSANDRA_INIT_STATE_MARKER}" ] && [ ! -f "${KAFKA_INIT_STATE_MARKER}" ]; do
  echo "Waiting for Cassandra and Kafka to be initialized..."
  sleep 5
done

"${SPARK_HOME}/bin/spark-submit" \
  --master "spark://${SPARK}" \
  --class com.github.gva.app.FraudDetectorApp \
  --driver-memory 512M \
  --executor-memory 512M \
  --num-executors 1 \
  --packages \
  "org.apache.spark:spark-sql-kafka-0-10_2.11:${SPARK_VERSION},com.datastax.spark:spark-cassandra-connector_2.11:2.5.0" \
  ./fraud-detector-service.jar \
  --kafka-bootstrap-servers "${KAFKA}" \
  --kafka-topic events \
  --redis "${REDIS}" \
  --redis-prefix bots \
  --cassandra "${CASSANDRA}" \
  --cassandra-table fraud.bots \
  --redis-checkpoint "${ACTIVE_BOTS_CHECKPOIN_LOCATION}" \
  --cassandra-checkpoint "${HISTORY_CHECKPOINT_LOCATION}" \
  --bot-ttl-seconds 20 \
  --bot-threshold-interval-slide-seconds 1 \
  --bot-threshold-interval-seconds 10 \
  --bot-threshold-num-events-per-interval 20 \

