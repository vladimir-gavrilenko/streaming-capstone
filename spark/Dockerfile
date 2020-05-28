FROM openjdk:8

RUN apt-get update && apt-get install wait-for-it

ENV SPARK_VERSION 2.4.5
ENV SPARK_HOME /usr/lib/spark

RUN cd /tmp && \
    wget -nv https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-hadoop2.7.tgz && \
    tar zxf spark-${SPARK_VERSION}-bin-hadoop2.7.tgz && \
    rm -rf spark-${SPARK_VERSION}-bin-hadoop2.7.tgz && \
    mv spark-${SPARK_VERSION}-bin-hadoop2.7 ${SPARK_HOME}

COPY ./*.sh ${SPARK_HOME}/

EXPOSE 4040
EXPOSE 6066
EXPOSE 7077
EXPOSE 8080
EXPOSE 8081
EXPOSE 8881

WORKDIR ${SPARK_HOME}
CMD ["./start-master.sh"]
