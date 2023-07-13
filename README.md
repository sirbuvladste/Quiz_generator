# Simple quiz generator. #
Users of this program will log into the application on any system call (for simplicity) other than user creation. Users logged into the system will be able to: create questions (with only one correct answer, or with several correct answers), create quizzes based on previously added questions, and will be able to answer others' quizzes, only once.
Any question is characterized by an identifier, a text and a list of possible answers. An answer is characterized by an identifier, a text, and its truth value (False/True).

**A short description of the classes:**

## Tema1
- the command is checked if all the arguments are given.
- create all the files if the first argument is correct.
- check the authentication and the login.

## User
- here are handled all the matters that involve the work with a user.
- checks if the username and password were provided.
- add the ID, username and password of the user to _"users.txt"_.
- the ID is generated for every newly created user using a static variable.
- a method get a User object.

## Question
- here are handled all the matters that involve the work with a question.
- check if answers in the command are valid and adds them to the static answers array.
- check the rest of the given parameters and add the question data and answers to the _"questions.txt"_.

## Quiz
- here are handled all the matters that involve the work with a quiz
- check if the arguments given for the quiz and the question IDs are valid, if so add the quiz to the file
- in order to delete a quiz, create a copy of the _"quizzes.txt"_ except the line of the quiz with the given ID and than rename the copy to _"quizzes.txt"_

## GetDetails
- methods to print the details of a Quiz, a Question or all submissions of a user

## Submit
- handles the submission checking if the arguments given are correct and by calculating the score
- the method of calculating is:
  - create an int array of 50 columns
    > the index of a position represents the ID of the answer and the value at the index is the question ID of that certain answer
  - create a float array of 50 elements for the points of every answer
  - the quiz in total has 100 points, witch are divided equally between the questions
  - the points for the questions are divided equally between every correct answer and every wrong answer, while the correct answer will have a positive value in the points array, the wrong ones will have a negative value
    > example: if a question has 10 points and 5 answers and 2 are correct, each correct answer will grant 5 points and each wrong answer will have -10/3 points in the points array
  - sums up all the points in the points array from the indexes corresponding to the answer IDs
- there is a method to return the score if a user has submitted a quiz

## Files
- handles the creation and delete of files and the reset of all static variables

_Cases that have been treated and are not in the requirement of the problem:_
1. Can not put the same answer twice at a submission.
2. No correct answers provided to a question.
3. Only one correct answer provided to a multiple answers question.
4. The first argument of the command is not valid; the command is not valid.
5. If the deletion of a file fails print an error message.
