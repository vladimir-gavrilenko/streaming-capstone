FROM openjdk:8-alpine

ENV SPARK_VERSION 2.4.5
ENV SPARK_HOME /usr/lib/spark

RUN apk add --no-cache bash && \
    apk --no-cache add procps && \
    apk --update add coreutils
RUN cd /tmp && \
    wget https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-hadoop2.7.tgz && \
    tar zxf spark-${SPARK_VERSION}-bin-hadoop2.7.tgz && \
    rm -rf spark-${SPARK_VERSION}-bin-hadoop2.7.tgz && \
    mv spark-${SPARK_VERSION}-bin-hadoop2.7 /usr/lib/spark

EXPOSE 4040
EXPOSE 6066
EXPOSE 7077
EXPOSE 8080
EXPOSE 8081
EXPOSE 8881

WORKDIR ${SPARK_HOME}
CMD ["bin/spark-class", "org.apache.spark.deploy.master.Master"]
