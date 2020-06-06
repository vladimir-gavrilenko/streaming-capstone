#!/usr/bin/env bash
pip install -r requirements.txt
export FLASK_APP=application/application.py

while [ ! -f "${CASSANDRA_INIT_STATE_MARKER}" ] && [ ! -f "${KAFKA_INIT_STATE_MARKER}" ]; do
  echo "Waiting for Cassandra and Kafka to be initialized..."
  sleep 5
done

flask run --host=0.0.0.0
