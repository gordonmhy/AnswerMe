# AnswerMe

COMP3330 Course Project: Group 19


### Contribution Guidelines:

- Exercise minimal changes to App.js, which will act as a base for launching and navigating between views/components.
- Implement pages/views or any components in standalone files in the components/ directory.
- Implement any functions, APIs or utility items in files categorized in accordance with app functionalities in the utils/ directory.
- Place any styling related files (categorized in accordance with app functionalities) in the styles/ directory.

### Example repositories with similar project structure

- GitterMobile (React Native):
[https://github.com/apiko-dev/GitterMobile/tree/master/app](https://github.com/apiko-dev/GitterMobile/tree/master/app)


### Question Bank
- A question bank is hosted on a Flask server (URL will be provided later)
- At this stage the frontend panel is not yet usable.
- Recommended interacting protocol: via HTTPIE or CURL
- Available endpoints:
  - /add  (POST): Adds a question (keys: cat, ql1, ql2, note, ans1, ans2)
  - /all  (GET): Obtains all questions
  - /all/<cat>  (GET): Obtains all questions on a specific category
  - /ques  (GET): Obtains a random question from all categories (GET arg: prev_id -> id of previous question)
  - /ques/<cat>  (GET): Obtains a random question from a category (GET arg: prev_id -> id of previous question)
  - /del/<qid>  (DELETE): Deletes a question by its qid


### Dependencies (libraries/packages used)

- \\\ To be updated
