#!/usr/bin/env bash
set -eu

while [ ! -f /state/cassandra ] && [ ! -f /state/kafka ] ; do
    echo "Waiting for Cassandra and Kafka to be initialized..."
    sleep 5
done

"${SPARK_HOME}/bin/spark-submit" \
  --master "spark://${SPARK}" \
  --class com.github.gva.app.FraudDetectorApp \
  --driver-memory 512M \
  --executor-memory 512M \
  --num-executors 1 \
  --packages "org.apache.spark:spark-sql-kafka-0-10_2.11:${SPARK_VERSION}" \
  ./fraud-detector-service.jar \
  --kafka-bootstrap-servers "${KAFKA}" \
  --kafka-topic events \
  --redis "${REDIS}" \
  --redis-prefix bots \
  --cassandra ${CASSANDRA} \
  --cassandra-table fraud.bots
