import os

from cassandra.cluster import Cluster
from kafka import KafkaProducer, KafkaConsumer
from redis import Redis


class Clients:
    clients = {}

    @classmethod
    def get(cls, client_name):
        client = cls.clients.get(client_name, None)
        if client:
            return client
        if client_name == 'redis':
            client = cls._create_redis()
        elif client_name == 'kafka-producer':
            client = cls._create_kafka_producer()
        elif client_name == 'kafka-consumer':
            client = cls._create_kafka_consumer()
        elif client_name == 'cassandra':
            client = cls._create_cassandra()
        else:
            raise RuntimeError(f'Unknown client name: {client_name}')
        cls.clients[client_name] = client
        return client

    @staticmethod
    def _create_redis():
        host, port = os.environ['REDIS'].split(':')
        return Redis(host=host, port=int(port), db=0)

    @staticmethod
    def _create_kafka_producer():
        bootstrap_servers = os.environ['KAFKA']
        return KafkaProducer(
            bootstrap_servers=bootstrap_servers,
            acks=1,
            batch_size=500,
            linger_ms=500
        )

    @staticmethod
    def _create_kafka_consumer():
        bootstrap_servers = os.environ['KAFKA']
        return KafkaConsumer(bootstrap_servers=bootstrap_servers)

    @staticmethod
    def _create_cassandra():
        host = os.environ['CASSANDRA'].split(':')[0]
        cluster = Cluster([host])
        return cluster
