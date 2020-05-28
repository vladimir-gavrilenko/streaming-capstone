#!/usr/bin/env bash
pip install -r requirements.txt
export FLASK_APP=application/application.py
flask run --host=0.0.0.0
