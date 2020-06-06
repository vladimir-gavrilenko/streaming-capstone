FROM spark:latest
COPY target/scala-2.11/fraud-detector-service.jar /app/
COPY submit.sh /app/
WORKDIR /app
CMD ["./submit.sh"]