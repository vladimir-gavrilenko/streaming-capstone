#!/usr/bin/env bash
set -eu

while ! cqlsh -e 'describe cluster;' $CASSANDRA_SEEDS; do
  echo "Waiting for the seed node..."
  sleep 5
done

SLAVE_HOSTS=0
while [[ $SLAVE_HOSTS -eq 0 ]]; do
  echo "Waiting for the cluster to be ready..."
  sleep 5
  ROWS=$(cqlsh -e 'select count(*) from system.peers;' $CASSANDRA_SEEDS | grep rows)
  PREFIX="("
  SUFFIX=" rows)"
  SLAVE_HOSTS="${ROWS#$PREFIX}"
  SLAVE_HOSTS="${SLAVE_HOSTS%$SUFFIX}"
  echo "Rows: $ROWS; Slave hosts: $SLAVE_HOSTS"
done

cqlsh --file /app/init.cql $CASSANDRA_SEEDS
