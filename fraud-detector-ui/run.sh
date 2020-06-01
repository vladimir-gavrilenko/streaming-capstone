#!/usr/bin/env bash
pip install -r requirements.txt
export FLASK_APP=application/application.py

while ! ls /state/cassandra && ! ls /state/kafka; do
    echo "Waiting for Cassandra and Kafka to be initialized..."
    sleep 5
done

flask run --host=0.0.0.0
