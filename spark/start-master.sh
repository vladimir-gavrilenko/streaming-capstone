#!/usr/bin/env bash
set -eu
"${SPARK_HOME}/bin/spark-class" org.apache.spark.deploy.master.Master -h "$(hostname)"
