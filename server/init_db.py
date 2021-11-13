import json


# Initializes database, if there is not one
def init_db(FILE_PATH):
    with open(FILE_PATH, "w", encoding='utf8') as file:
        # Write default questions
        file.write(json.dumps((  # PLEASE DO NOT DIRECTLY EDIT THE JSON FILE. USE WEB API
            {
                "qid": "1",
                "q": ["I to school by bus.",  # question line 1
                      "A verb is missing. What verb?"],  # question line 2  ... (possible to have more lines)
                "note": "Fill in 2 letters",  # note / hint / whatever
                "a": ["go"],  # acceptable answers
                "cat": "eng",  # category of question
            },
            {
                "qid": "2",
                "q": ["「唧唧復唧唧，木蘭當戶織。」下一句是？"],
                "note": "______，_____。(連標點符號作答)",
                "a": ["不聞機杼聲，惟聞女嘆息。"],
                "cat": "chin",
            },
            {
                "qid": "3",
                "q": ["Given that two arbitrary odd numbers are picked, what is the probability that their sum is "
                      "even?", "已知兩個數字為單數，其和為雙數的概率是？"],
                "note": "Answer a number (請回答一個數字)",
                "a": ["1"],
                "cat": "math",
            },
        ), ensure_ascii=False, indent=4))
    print("Database initialized successfully.")
