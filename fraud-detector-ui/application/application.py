import random
from datetime import datetime
from typing import List

from cassandra.cluster import Cluster
from flask import Flask, render_template, request, jsonify
from kafka import KafkaProducer, KafkaConsumer
from redis import Redis

from application.clients import Clients

app = Flask(__name__)


@app.route('/')
def get_index():
    return render_template('index.html')


@app.route('/status')
def get_status():
    consumer: KafkaConsumer = Clients.get('kafka-consumer')
    kafka_topics = consumer.topics()
    print(f'topics: {kafka_topics}')
    kafka_status = 'events' in kafka_topics
    redis_client: Redis = Clients.get('redis')
    redis_info = redis_client.info()
    print(f'redis_info: {redis_info}')
    redis_status = bool(redis_info)
    return jsonify(kafka_status=kafka_status, redis_status=redis_status)


@app.route('/events', methods=['POST'])
def create_events():
    ips: List[str] = request.form['ips'].split(',')
    producer: KafkaProducer = Clients.get('kafka-producer')
    now = datetime.now().strftime('%Y-%m-%dT%H:%M:%S')
    for ip in ips:
        producer.send('events', key=ip.encode('utf-8'), value=now.encode('utf-8'))
    return '', 201


@app.route('/bots')
def get_bots():
    redis_client: Redis = Clients.get('redis')
    bots = [bot for bot in redis_client.scan_iter('bots:*')]
    return jsonify(bots=bots)


@app.route('/total')
def get_total():
    cassandra_cluster: Cluster = Clients.get('cassandra')
    session = cassandra_cluster.connect('system')
    res = session.execute('SELECT * FROM schema_keyspaces;')
    print(res)
    total = random.randint(0, 10)
    return jsonify(total=total)
