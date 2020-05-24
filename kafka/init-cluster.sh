#!/usr/bin/env bash
set -eu
KAFKA_SEED="${1}:9092"
wait-for-it "${KAFKA_SEED}" -t 60
"${KAFKA_HOME}/bin/kafka-topics.sh" \
  --create \
  --bootstrap-server "${KAFKA_SEED}" \
  --replication-factor 2 \
  --partitions 6 \
  --topic events
sleep 5
echo "topics:"
"${KAFKA_HOME}/bin/kafka-topics.sh" \
  --bootstrap-server "${KAFKA_SEED}" \
  --list
echo "details for 'events':"
"${KAFKA_HOME}/bin/kafka-topics.sh" \
  --bootstrap-server "${KAFKA_SEED}" \
  --describe events