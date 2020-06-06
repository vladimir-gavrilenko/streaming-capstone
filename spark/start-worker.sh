#!/usr/bin/env bash
set -eu
wait-for-it "$SPARK_MASTER" -t 60
"${SPARK_HOME}/bin/spark-class" org.apache.spark.deploy.worker.Worker "spark://${SPARK_MASTER}"
