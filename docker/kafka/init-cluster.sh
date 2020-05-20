#!/usr/bin/env bash
set -eu
echo "Waiting kafka-seed to launch..."
while ! "${KAFKA_HOME}/bin/kafka-topics.sh" --bootstrap-server kafka-seed:9092 --list; do
  sleep 1
  echo "waiting..."
done
echo "kafka-seed is available!"

"${KAFKA_HOME}/bin/kafka-topics.sh" \
  --create \
  --bootstrap-server kafka-seed:9092 \
  --replication-factor 2 \
  --partitions 6 \
  --topic events

sleep 5
echo "topics:"
"${KAFKA_HOME}/bin/kafka-topics.sh" --bootstrap-server kafka-seed:9092 --list
