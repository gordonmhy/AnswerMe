import json
import random
from os.path import exists, join, realpath, dirname

from flask import Flask, jsonify, request, Response, render_template
from flask_cors import CORS, cross_origin

from init_db import init_db

app = Flask(__name__)
cors = CORS(app)

# FILE_PATH = join(realpath(dirname(__file__)), "static/data", "q&a.json")  # Data Location
FILE_PATH = "static/data/q&a.json"
CATEGORIES = ("chin", "eng", "math")

# Question Bank (Synchronous with q&a.json)
question_bank = [], [], []  # chin, eng, math
cat_key_mapper = dict(zip(CATEGORIES, range(len(question_bank))))


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
        result = []
        for i in range(3):
            result += question_bank[i]
        return result
    return question_bank[cat_key_mapper.get(cat, 0)]


def max_qid():
    return max([int(item["qid"]) for cat in question_bank for item in cat])


def feed_random_questions(cat=None, prev_id=None):
    questions = get_all_questions(cat)
    while True:
        rand_q = random.choice(questions)
        while rand_q["qid"] == prev_id and len(questions) > 1:
            rand_q = random.choice(questions)
        yield rand_q


def get_question(qid, source):
    return next(iter([item for item in source if item["qid"] == str(qid)]), None)


def add_question(cat, q, a, note):
    if cat not in CATEGORIES:
        return False
    new_question = {
        "cat": cat, "q": q, "a": a, "note": note, "qid": str(max_qid() + 1),
    }
    new_dat = list(get_all_questions()) + [new_question]
    with open(FILE_PATH, 'w') as file:
        file.write(json.dumps(new_dat, ensure_ascii=False, indent=4))
    load_questions()
    return new_question


def del_question(qid):
    all_questions = list(get_all_questions())
    to_delete = get_question(qid, all_questions)
    if to_delete is None:
        return False
    all_questions.remove(to_delete)
    with open(FILE_PATH, 'w') as file:
        file.write(json.dumps(all_questions, ensure_ascii=False, indent=4))
    load_questions()
    return to_delete["qid"]


#######################
#  RESTFUL endpoints  #
#######################

@app.route('/all')
@app.route('/all/<cat>')
@cross_origin(origin='*')
def rest_all_questions(cat=None):
    return jsonify(get_all_questions(cat)), 200


@app.route('/ques')  # this yields any random question
@app.route('/ques/<cat>')  # cat can be one of {"chin", "eng", "math"}
@cross_origin(origin='*')
def rest_random_question(cat=None):
    prev_id = request.args.get("prev_id")
    result = next(feed_random_questions(cat, prev_id if prev_id else None), None)
    if result is None:
        return Response('{"error":"no question available."}', status=200, mimetype='application/json')
    return jsonify(result), 200


@app.route('/add', methods=['POST'])
@cross_origin(origin='*')
def rest_add_question():
    cat = request.json.get("cat")
    ql1 = request.json.get("ql1")
    ql2 = request.json.get("ql2")
    note = request.json.get("note")
    ans1 = request.json.get("ans1")
    ans2 = request.json.get("ans2")
    if cat is None or ql1 is None or ans1 is None:
        return Response('{"error":"Missing mandatory arguments."}', status=400, mimetype='application/json')
    result = add_question(cat, [ql1] + ([ql2] if ql2 else []), [ans1] + ([ans2] if ans2 else []), note)
    if result is False:
        return Response('{"error":"Invalid question category."}', status=200, mimetype='application/json')
    return jsonify({'success': result}), 201


@app.route('/del/<qid>', methods=['DELETE'])
@cross_origin(origin='*')
def rest_delete_question(qid):
    result = del_question(qid)
    if result is False:
        return Response('{"error":"Invalid question ID."}', status=200, mimetype='application/json')
    return Response('{"success":"Deleted question with qid(' + result + ')"}', status=200)


@app.route('/getq/<qid>')
@cross_origin(origin='*')
def rest_question_by_id(qid):
    result = get_question(qid, get_all_questions())
    if result is None:
        return Response('{"error":"Invalid question ID."}', status=200, mimetype='application/json')
    return jsonify(result), 200


#######################

# Renders template for the main control panel
@app.route('/')
@cross_origin(origin='*')
def main():
    # Only showing endpoints for question retrieval
    endpoints = [
        ("Retrieving all questions (any category)", "/all"),
        ("Retrieving all questions (a particular category, either chin, eng or math)", "/all/<cat>"),
        ("Obtaining a random question (any category) (GET argument: prev_id => Previous question ID)", "/ques"),
        ("Obtaining a random question (a particular category) (GET argument: prev_id => Previous question ID)",
         "/ques/<cat>"),
        ("Obtaining a question by question ID", "/getq/<qid>"),
    ]
    return render_template("main.html", cats=zip(CATEGORIES, ("Chinese", "English", "Mathematics")),
                           display_cats=zip(["all"] + list(CATEGORIES), ("All", "Chinese", "English", "Mathematics")),
                           endpoints=endpoints)
    # cats and display_cats are replicates of one another (jinja2 has bugs in reusing variables)


if __name__ == '__main__':
    if exists(FILE_PATH) is False:
        init_db(FILE_PATH)
    load_questions()
    app.config['JSON_AS_ASCII'] = False
    app.run()
