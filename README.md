# AnswerMe

COMP3330 Course Project: Group 19


### Question Bank
- Question Bank control panel: [https://answermeapi.pythonanywhere.com](https://answermeapi.pythonanywhere.com)
- REST-ful API endpoints available at the same domain suffixed with the below URIs
- Available endpoints:
  - /add  (POST): Adds a question (keys: cat, ql1, ql2, note, ans1, ans2)
  - /all  (GET): Obtains all questions
  - /all/<cat>  (GET): Obtains all questions on a specific category
  - /ques  (GET): Obtains a random question from all categories (GET arg: prev_id -> id of previous question)
  - /ques/<cat>  (GET): Obtains a random question from a category (GET arg: prev_id -> id of previous question)
  - /getq/<qid>  (GET): Obtains a specific question by qid
  - /del/<qid>  (DELETE): Deletes a question by its qid
