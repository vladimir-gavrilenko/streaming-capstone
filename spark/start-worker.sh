#!/usr/bin/env bash
set -eu
SPARK_MASTER_HOST="$1"
wait-for-it "$SPARK_MASTER_HOST:7077" -t 60
"${SPARK_HOME}"/bin/spark-class org.apache.spark.deploy.worker.Worker spark://"${SPARK_MASTER_HOST}":7077
