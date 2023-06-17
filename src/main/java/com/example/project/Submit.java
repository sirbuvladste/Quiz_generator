package com.example.project;

import java.io.*;

import static com.example.project.Files.fSubmit;
import static com.example.project.Question.getQuestionByID;
import static java.lang.Integer.parseInt;

public class Submit {

    private static final String splitBy = ",";

    /**
     * Check if the given question ID is valid and return the question.
     *
     * @param questionID - the question ID
     * @return - Question object of the given ID
     */
    public static Question getQuestionsAndAnswers(String questionID) {
        String line;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    "src/main/java/com/example/project/questions.txt"));
            while ((line = bufferedReader.readLine()) != null) {
                String[] questionLine = line.split(splitBy);
                if (questionLine[0].equals(questionID)) {
                    Question question = getQuestionByID(questionLine[0]);
                    bufferedReader.close();
                    return question;
                }
            }
            // if quiz not found
            bufferedReader.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculate the score of the quiz and then call the method to write it to file.
     *
     * @param quizLine - data of the quiz from the command
     * @param args     - the arguments from the command
     */
    public static void setQuiz(String[] quizLine, final String[] args) {
        /*
         * create an int array of 50 columns, every position is the question ID for every answer
         * create a float array of 50 elements for the points of every answer
         * the quiz in total has 100 points, witch are divided equally between the questions
         * the points for the questions are divided equally between the correct answers and the wrong answers
         * example: if a question has 10 points and 5 answers and 2 are correct, each correct answer will grant 5 points
         * and each wrong answer will subtract -10/3 points
         */
        User user = User.getUser(args[1].split(" ")[1], args[2].split(" ")[1]);
        Quiz quiz = new Quiz(quizLine[0], quizLine[2], user);
        int[] ans = new int[10];

        int[] questionIDForAnswers = new int[50];
        int answersIndex = 0;
        float[] answersPoints = new float[50];


        int numberOfQuestions = parseInt(quizLine[3]);
        float questionsPoints = 100 / ((float) numberOfQuestions);

        int numberOfAnswers = 0;
        float pointsPerCorrectAnswer = 0;
        float pointsPerWrongAnswer = 0;

        Question question = null;

        for (int i = 0; i < numberOfQuestions; i++) {
            question = getQuestionsAndAnswers(quizLine[4 + i]);
            if (question != null) {
                numberOfAnswers = question.getNumberOfAnswers();
                if (question.getType() == 1) {
                    pointsPerCorrectAnswer = questionsPoints;
                    pointsPerWrongAnswer = -questionsPoints;
                } else {
                    pointsPerCorrectAnswer = questionsPoints / question.getNumberOfCorrectAnswers();
                    pointsPerWrongAnswer = -questionsPoints / (numberOfAnswers - question.getNumberOfCorrectAnswers());
                }
                // set the points for the answers
                for (int j = 0; j < numberOfAnswers; j++) {
                    questionIDForAnswers[answersIndex] = question.getQuestionID();
                    if (question.getCorrectAnswers()[j] == 1) {
                        answersPoints[answersIndex] = pointsPerCorrectAnswer;
                    } else {
                        answersPoints[answersIndex] = pointsPerWrongAnswer;
                    }
                    answersIndex++;
                }
            } else {
                // question not found :(
                return;
            }
        }

        // check the answers ID form args and add the points to the quiz
        float totalPoints = 0;
        int index = 0;
        for (int i = 0; i < args.length - 4; i++) {
            index = parseInt(args[i + 4].split("'")[1]);
            if (answersPoints[index - 1] == 0) {
                System.out.print("{'status':'error','message':'Can not put the same answer twice!'}");
                return;
            }
            totalPoints += answersPoints[index - 1];
            answersPoints[index - 1] = 0;
        }

        if (totalPoints < 0) {
            totalPoints = 0;
        }
        assert user != null;
        addResultToSubmission(user, quiz.getNameQuiz(), totalPoints, quizLine[0]);
        System.out.print("{ 'status' : 'ok', 'message' : '" + Math.round(totalPoints) + " points'}");
    }

    /**
     * Add the data of the submission to the "submissions.txt" file.
     *
     * @param user        - the user that submitted the quiz
     * @param quizName    - the name of the quiz
     * @param totalPoints - the total points of the quiz
     * @param quizID      - the ID of the quiz
     */
    public static void addResultToSubmission(User user, String quizName, float totalPoints, String quizID) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(
                    "src/main/java/com/example/project/submissions.txt", true));
            PrintWriter out = new PrintWriter(bw);

            out.print(user.getUserID() + "," + quizID + "," + quizName + "," + (int) (totalPoints) + "\n");

            out.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the arguments and call the function to set the quiz and add to the "submissions.txt" file.
     *
     * @param args the command line arguments
     * @return <p>0 = quiz was not found</p>
     * <p>1 = quiz was found</p>
     * <p>2 = user tries to submit his own quiz</p>
     */
    public static int submitQuiz(final String[] args) {
        String line = "";

        String quizID = args[3].split(" ")[1];
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    "src/main/java/com/example/project/quizzes.txt"));
            while ((line = bufferedReader.readLine()) != null) {
                // quizLine = the line of the quiz in the quizzes.txt file
                String[] quizLine = line.split(splitBy);
                if (quizLine[0].equals(quizID)) {
                    setQuiz(quizLine, args);
                    bufferedReader.close();
                    return 1;
                }
            }
            // if quiz not found
            bufferedReader.close();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Check if the user submitted the quiz.
     *
     * @param quizID of the quiz
     * @param userID of the user
     * @return <p>true - quiz was submitted</p>
     * <p>false - quiz was not submitted</p>
     */
    public static boolean getCompletedState(String quizID, String userID) {
        String line = "";

        String[] quiz;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fSubmit));

            while ((line = bufferedReader.readLine()) != null) {
                quiz = line.split(splitBy);

                // if the quiz was found get the questions for it
                if (quiz[1].equals(quizID) && quiz[0].equals(userID)) {
                    bufferedReader.close();
                    return true;
                }
            }
            bufferedReader.close();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
