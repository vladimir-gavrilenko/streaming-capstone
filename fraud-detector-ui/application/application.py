import random

from flask import Flask, render_template, request, jsonify

app = Flask(__name__)


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/events', methods=['POST'])
def event():
    ips = request.form['ips'].split(',')
    for ip in ips:
        print(ip)
    return '', 201


@app.route('/bots')
def bots():
    ips = [
        '1.2.3.4', '5.6.7.8', '9.10.11.12', '13.14.15.16',
        '17.18.19.20', '21.22.23.24', '25.26.27.28',
    ]
    all_bots = random.sample(ips, random.randint(0, len(ips) - 1))
    return jsonify(bots=all_bots)
