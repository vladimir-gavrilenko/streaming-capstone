FROM python:3.7
COPY . /app/

EXPOSE 5000

ENV REDIS redis:6379
ENV CASSANDRA cassandra-seed:9042

WORKDIR /app

CMD ["./run.sh"]

