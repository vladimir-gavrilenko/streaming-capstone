#!/usr/bin/env bash
set -eu

wait-for-it "${CASSANDRA}" -t 60
wait-for-it "${KAFKA}" -t 60
wait-for-it "${REDIS}" -t 60
wait-for-it "${SPARK}" -t 60

"${SPARK_HOME}/bin/spark-submit" \
  --master "spark://${SPARK}" \
  --class com.github.gva.app.FraudDetectorApp \
  --packages "org.apache.spark:spark-sql-kafka-0-10_2.11:${SPARK_VERSION}" \
  ./fraud-detector-service.jar \
  --kafka-bootstrap-servers "${KAFKA}" \
  --kafka-topic events
