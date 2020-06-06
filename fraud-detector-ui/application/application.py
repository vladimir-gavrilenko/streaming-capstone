import json
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


@app.route('/events', methods=['POST'])
def create_events():
    event_time = datetime.now().timestamp()
    ips: List[str] = request.form['ips'].split(',')
    producer: KafkaProducer = Clients.get('kafka-producer')
    for ip in ips:
        event = json.dumps({
            'ip': ip,
            'action': 'click',
            'epochSeconds': int(event_time)
        })
        producer.send('events', key=ip.encode('utf-8'), value=event.encode('utf-8'))
    producer.flush(timeout=5)
    return '', 201


@app.route('/bots')
def get_bots():
    redis_client: Redis = Clients.get('redis')
    prefix = 'bots:'
    bots = [
        bot.decode()[len(prefix):]
        for bot in redis_client.scan_iter(f'{prefix}*')
    ]
    return jsonify(bots=bots)


@app.route('/total')
def get_total():
    cassandra_cluster: Cluster = Clients.get('cassandra')
    with cassandra_cluster.connect() as session:
        total = session.execute('select count(*) from fraud.bots').one()[0]
        print(total)
    return jsonify(total=total)


@app.route('/status')
def get_status():
    consumer: KafkaConsumer = Clients.get('kafka-consumer')
    kafka_topics = consumer.topics()
    kafka_status = 'events' in kafka_topics
    redis_client: Redis = Clients.get('redis')
    redis_info = redis_client.info()
    redis_status = bool(redis_info)
    cassandra_client: Cluster = Clients.get('cassandra')
    cassandra_session = cassandra_client.connect()
    cassandra_status = not cassandra_session.is_shutdown
    return jsonify(
        kafka_status=kafka_status,
        redis_status=redis_status,
        cassandra_status=cassandra_status
    )
