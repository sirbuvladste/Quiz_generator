package com.example.project;

import static com.example.project.Files.cleanup_all;
import static com.example.project.Files.create_files;
import static com.example.project.GetDetails.getMySolutions;
import static com.example.project.GetDetails.printQuizDetailsByID;
import static com.example.project.Question.*;
import static com.example.project.Quiz.*;
import static com.example.project.Submit.submitQuiz;
import static com.example.project.User.*;


public class Tema1 {
    public static void command_create_user(final String[] args) {
        if (args.length >= 2 && args[1].startsWith("-u") && args[1].substring(2).length() > 1) {
            if (args.length >= 3 && args[2].startsWith("-p") && args[2].substring(2).length() > 1) {
                if (checkUser(args[1].substring(2), args[2].substring(2)) == 1) {
                    addUser(args[1].substring(3), args[2].substring(3));
                }
            } else {
                System.out.print("{'status':'error','message':'Please provide password'}");
            }
        } else {
            System.out.print("{'status':'error','message':'Please provide username'}");
        }
    }

    public static int checkLogin(final String[] args) {
        if (args.length < 3 || !(args[1].startsWith("-u") && args[2].startsWith("-p") &&
                args[1].substring(2).length() > 1 && args[2].substring(2).length() > 1)) {
            System.out.print("{'status':'error','message':'You need to be authenticated'}");
            return 0;
        }

        if (User.checkUser(args[1].substring(3), args[2].substring(3)) != 0) {
            // checkPassword returns 0 if the user with the given username and password exists
            System.out.print("{ 'status' : 'error', 'message' : 'Login failed'}");
            return 0;
        }

        return 1;
    }

    public static void command_create_question(final String[] args) {

        if (checkLogin(args) == 0) {
            return;
        }

        if (args.length < 4 || (!args[3].startsWith("-text") || args[3].substring(6).length() <= 1)) {
            System.out.print("{'status':'error','message':'No question text provided'}");
            return;
        }

        if (args.length < 5 || !args[4].startsWith("-type") || args[4].substring(6).length() <= 1) {
            System.out.print("{'status':'error','message':'Please provide a type'}");
            return;
        }

        if (args.length == 5) {
            System.out.print("{'status':'error','message':'No answer provided'}");
            return;
        }

        if (args.length == 7 && args[5].startsWith("-answer")) {
            System.out.println("{ 'status' : 'error', 'message' : 'Only one answer provided'}");
            return;
        }

        if (checkAnswersAndCreate(args, args[4].substring(6).equals("'single'") ? 1 : 2)) {
            User userQuestion = getUser(args[1].substring(3), args[2].substring(3));
            Question question = new Question(userQuestion, args[3].substring(6), args[4].substring(6).equals("'single'") ? 1 : 2);
            question.addQuestion();
        }

        Question.resetStaticVariablesQuestions();

    }

    public static void get_question_id_by_text(final String[] args) {
        if (checkLogin(args) == 0) {
            return;
        }

        if (args.length < 4 || (!args[3].startsWith("-text") || args[3].substring(6).length() <= 1)) {
            System.out.print("{'status':'error','message':'No question text provided'}");
            return;
        }

        int id = getQuestionIDByText(args[3].substring(6));
        if (id == 0 || id == -1) {
            System.out.print("{ 'status' : 'error', 'message' : 'Question does not exist'}");
        } else {
            System.out.print("{ 'status' : 'ok', 'message' : '" + id + "'}");
        }

    }

    public static void get_all_questions(final String[] args) {
        if (checkLogin(args) == 0) {
            return;
        }

        System.out.print("{ 'status' : 'ok', 'message' : '[");
        getAllQuestions();
        System.out.print("]'}");
    }

    public static void create_quiz(final String[] args) {
        if (checkLogin(args) == 0) {
            return;
        }

        if (args.length < 4 || (!args[3].startsWith("-name") || args[3].substring(6).length() <= 1)) {
            System.out.print("{'status':'error','message':'No quiz text provided'}");
            return;
        }

        if (args.length - 4 <= 0) {
            System.out.print("{'status':'error','message':'Quiz needs at least one question'}");
            return;
        }

        if (args.length - 4 >= 10) {
            System.out.print("{'status':'error','message':'Quiz has more than 10 questions'}");
            return;
        }

        int checkText = checkQuizIDByName(args[3].substring(6));
        if (checkText > 0) {
            System.out.print("{'status':'error','message':'Quizz name already exists'}");
            return;
        }

        if (checkQuestionsForQuizAndCreate(args) == 0) {
            // the error message is printed in the checkQuestionsForQuizAndCreate method
            return;
        }


        if (checkText == 0) {
            User user = getUser(args[1].substring(3), args[2].substring(3));
            Quiz quiz = new Quiz(args[3].substring(6), user);
            quiz.addQuiz();
        }
    }

    public static void get_quiz_by_name(final String[] args) {
        if (args.length < 3 || !(args[1].startsWith("-u") && args[2].startsWith("-p") &&
                args[1].substring(2).length() > 1 && args[2].substring(2).length() > 1)) {
            System.out.print("{'status':'error','message':'You need to be authenticated'}");
            return;
        }

        if (checkUser(args[1].substring(3), args[2].substring(3)) != 0) {
            // checkPassword returns 0 if the user with the given username and password exists
            System.out.print("{ 'status' : 'error', 'message' : 'Login failed'}");
            return;
        }

        if (args.length < 4 || (!args[3].startsWith("-name") || args[3].substring(6).length() <= 1)) {
            System.out.print("{'status':'error','message':'No quiz text provided'}");
            return;
        }

        int quizID = checkQuizIDByName(args[3].substring(6));
        if (quizID == 0 || quizID == -1) {
            System.out.print("{ 'status' : 'error', 'message' : 'Quizz does not exist'}");
        } else {
            System.out.print("{ 'status' : 'ok', 'message' : '" + quizID + "'}");
        }

    }

    private static void get_all_quizzes(String[] args) {
        if (checkLogin(args) == 0) {
            return;
        }

        System.out.print("{ 'status' : 'ok', 'message' : '[");
        getAllQuizzes();
        System.out.print("]'}");
    }

    public static void get_quizz_details_by_id(final String[] args) {
        if (checkLogin(args) == 0) {
            return;
        }

        if (args.length < 4 || (!args[3].startsWith("-id") || args[3].substring(4).length() <= 1)) {
            System.out.print("{'status':'error','message':'No quiz id provided'}");
            return;
        }

        System.out.print("{ 'status' : 'ok', 'message' : '[");
        printQuizDetailsByID(args[3].substring(4));
        System.out.print("]'}");
    }

    public static void submit_quiz(final String[] args) {
        if (checkLogin(args) == 0) {
            return;
        }

        if (args.length < 4 || !args[3].startsWith("-quiz-id")) {
            System.out.print("{'status':'error','message':'No quizz identifier was provided'}");
            return;
        }

        int submitValue = submitQuiz(args);

        if (submitValue == 0) {
            System.out.print("{'status':'error','message': 'No quiz was found'}");
            return;
        }

        if (submitValue == 2) {
            System.out.print("{ 'status' : 'error', 'message' : 'You cannot answer your own quizz'}");
        }


    }

    public static void delete_quiz_by_id(final String[] args) {
        if (checkLogin(args) == 0) {
            return;
        }

        if (args.length < 4 || (!args[3].startsWith("-id") || args[3].substring(4).length() <= 1)) {
            System.out.print("{'status':'error','message':'No quizz identifier was provided'}");
            return;
        }

        int quizID = checkQuizID(args[3].split(" ")[1]);
        if (quizID == 0 || quizID == -1) {
            System.out.print("{ 'status' : 'error', 'message' : 'No quiz was found'}");
        } else {
            if (deleteQuizFromFile(args[3].substring(4)) == 1)
                System.out.print("{ 'status' : 'ok', 'message' : 'Quizz deleted successfully'}");
        }

    }

    public static void get_my_solutions(final String[] args) {
        if (checkLogin(args) == 0) {
            return;
        }

        System.out.print("{ 'status' : 'ok', 'message' : '[");
        getMySolutions(args[1].split(" ")[1]);
        System.out.print("]'}");
    }

    public static void main(final String[] args) {
        if (args == null) {
            System.out.print("Hello world!");
        } else {


            create_files();

            // check the first argument of args -> the command given
            switch (args[0]) {
                case "-create-user":
                    command_create_user(args);
                    break;
                case "-create-question":
                    command_create_question(args);
                    break;
                case "-cleanup-all":
                    cleanup_all(true);
                    break;
                case "-get-question-id-by-text":
                    get_question_id_by_text(args);
                    break;
                case "-get-all-questions":
                    get_all_questions(args);
                    break;
                case "-create-quizz":
                    create_quiz(args);
                    break;
                case "-get-quizz-by-name":
                    get_quiz_by_name(args);
                    break;
                case "-get-all-quizzes":
                    get_all_quizzes(args);
                    break;
                case "-get-quizz-details-by-id":
                    get_quizz_details_by_id(args);
                    break;
                case "-submit-quizz":
                    submit_quiz(args);
                    break;
                case "-delete-quizz-by-id":
                    delete_quiz_by_id(args);
                    break;
                case "-get-my-solutions":
                    get_my_solutions(args);
                    break;
                default:
                    System.out.print("{'status':'error','message':'Please enter a valid command'}");
                    cleanup_all(true);
            }

        }
    }
}