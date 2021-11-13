from flask import Flask, jsonify
from os.path import exists
from init_db import init_db
import json, random

app = Flask(__name__)

# Question Bank (Synchronous with q&a.json)
question_bank = [], [], []  # chin, eng, math
cat_key_mapper = dict(zip(("chin", "eng", "math"), range(len(question_bank))))
FILE_PATH = "q&a.json"  # Data Location


# Load questions from json to question_bank (to be called whenever json is updated)
def load_questions():
    for cat in question_bank:
        cat.clear()
    with open(FILE_PATH, 'r') as file:
        for question in json.load(file):
            cat = question["cat"]
            question_bank[cat_key_mapper.get(cat, 0)].append(question)


def get_all_questions(cat=None):
    if cat is None:
        return list(zip(*question_bank))[0]
    return question_bank[cat_key_mapper.get(cat, 0)]


def feed_random_questions(cat=None, prev_id=None):
    questions = get_all_questions(cat)
    while True:
        rand_q = random.choice(questions)
        while rand_q["qid"] == prev_id and len(questions) > 1:
            rand_q = random.choice(questions)
        yield rand_q


@app.route('/')
@app.route('/home')
def home():
    # TODO: Change this to a user manual
    with open(FILE_PATH, 'r') as file:
        dat = json.load(file)
        return jsonify(dat)


@app.route('/q')  # this yields any random question
@app.route('/q/<cat>')  # cat can be one of {"chin", "eng", "math"}
def q(cat=None):
    return jsonify(next(feed_random_questions(cat), {'error': 'no question available.'}))


# TODO: Endpoints for Adding/Removing questions


if __name__ == '__main__':
    if exists(FILE_PATH) is False:
        init_db(FILE_PATH)
    load_questions()
    app.config['JSON_AS_ASCII'] = False
    app.run(debug=True)
