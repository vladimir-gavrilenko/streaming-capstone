#!/usr/bin/env bash
set -eu
CONFIG_FILE="${KAFKA_HOME}/config/server.properties"
sed -i "s/broker\.id=0/broker\.id=${BROKER_ID}/g" "${CONFIG_FILE}"
sed -i "s/zookeeper\.connect=localhost:2181/zookeeper\.connect=${ZOOKEEPER}/g" "${CONFIG_FILE}"
"${KAFKA_HOME}/bin/kafka-server-start.sh" "${CONFIG_FILE}"
