#!/usr/bin/env bash
set -eu
SPARK_MASTER_HOST="$1"
echo "Waiting spark-master to launch master web UI..."
while ! nc -z "${SPARK_MASTER_HOST}" 8080; do
  sleep 1
  echo "waiting..."
done
echo "spark-master is available"
"${SPARK_HOME}"/bin/spark-class org.apache.spark.deploy.worker.Worker spark://"${SPARK_MASTER_HOST}":7077
