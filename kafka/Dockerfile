FROM openjdk:8

RUN apt-get update && apt-get install wait-for-it

ENV KAFKA_VERSION 2.5.0
ENV KAFKA_SCALA_VERSION 2.12
ENV KAFKA_HOME /usr/lib/kafka
ENV BROKER_ID 0
ENV ZOOKEEPER localhost:2181

RUN cd /tmp && \
    wget -nv https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_${KAFKA_SCALA_VERSION}-${KAFKA_VERSION}.tgz && \
    tar zxf kafka_${KAFKA_SCALA_VERSION}-${KAFKA_VERSION}.tgz && \
    rm -rf kafka_${KAFKA_SCALA_VERSION}-${KAFKA_VERSION}.tgz && \
    mv kafka_${KAFKA_SCALA_VERSION}-${KAFKA_VERSION} ${KAFKA_HOME}

COPY ./*.sh ${KAFKA_HOME}/

EXPOSE 9092

WORKDIR ${KAFKA_HOME}
CMD ["./start.sh"]
