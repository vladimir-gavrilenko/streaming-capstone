#!/usr/bin/env bash
set -eu

wait-for-it "${CASSANDRA}" -t 60
wait-for-it "${KAFKA}" -t 60
wait-for-it "${REDIS}" -t 60
wait-for-it "${SPARK}" -t 60

while ! ls /state/cassandra && ! ls /state/kafka; do
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
  --kafka-topic events
