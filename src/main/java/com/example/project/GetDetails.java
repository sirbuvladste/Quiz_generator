package com.example.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class GetDetails {

    private static final String splitBy = ",";
    private static int staticAnswerID = 0;

    public static int getStaticAnswerID() {
        return staticAnswerID;
    }

    public static void setStaticAnswerID(int staticAnswerID) {
        GetDetails.staticAnswerID = staticAnswerID;
    }

    /**
     * Print the details of the Quiz with the given ID.
     *
     * @param ID - the ID of the Quiz
     */
    public static void printQuizDetailsByID(String ID) {
        String line;

        String[] quiz;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    "src/main/java/com/example/project/quizzes.txt"));

            if ((line = bufferedReader.readLine()) != null) {
                quiz = line.split(splitBy);
                do {
                    // if the quiz was found get the questions for it
                    if (quiz[0].equals(ID)) {
                        for (int index = 4; index < quiz.length; index++) {
                            System.out.print("{");
                            printQuestionByID(quiz[index]);
                            System.out.print("]\"}");
                            if (index < quiz.length - 1) {
                                System.out.print(", ");
                            }
                        }

                        if ((line = bufferedReader.readLine()) != null)
                            System.out.print(", ");

                    }
                } while (line != null);
                bufferedReader.close();
                return;
            }
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print the details of the Question with the given ID.
     *
     * @param ID - the ID of the Question
     */
    public static void printQuestionByID(String ID) {
        String line;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    "src/main/java/com/example/project/questions.txt"));
            while ((line = bufferedReader.readLine()) != null) {
                String[] question = line.split(splitBy);
                if (question[0].equals(ID)) {
                    System.out.print("\"question-name\":\"" + question[2].substring(1, question[2].length() - 1) + "\", ");
                    System.out.print("\"question_index\":\"" + question[0].charAt(1) + "\", ");
                    if (question[3].equals("1"))
                        System.out.print("\"question_type\":\"single\", ");
                    else System.out.print("\"question_type\":\"multiple\", ");
                    System.out.print("\"answers\":\"[");
                    int nAnswers = parseInt(question[4]);
                    for (int i = 0; i < nAnswers * 2; i += 2) {
                        System.out.print("{\"answer_name\":\"" + question[5 + i].substring(1, question[5 + i].length() - 1) + "\", ");
                        setStaticAnswerID(getStaticAnswerID() + 1);
                        System.out.print("\"answer_id\":\"" + getStaticAnswerID() + "\"}");

                        if (i < nAnswers * 2 - 2)
                            System.out.print(", ");
                    }
                    bufferedReader.close();
                    return;
                }

            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print the details the submissions of a user.
     *
     * @param user - the user
     */
    public static void getMySolutions(String user) {
        String line;
        String[] quiz;
        int indexInList = 0;
        int indexInListForComma = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    "src/main/java/com/example/project/submissions.txt"));

            if ((line = bufferedReader.readLine()) != null) {
                quiz = line.split(splitBy);
                do {
                    // if the quiz was found get the questions for it
                    String userID = "'" + quiz[0] + "'";
                    User userCheck = User.getUserByID(userID);
                    if (userCheck.getUsername().equals(user)) {

                        System.out.print("{\"quiz-id\" : \"" + quiz[1].split("'")[1] + "\", ");
                        System.out.print("\"quiz-name\" : \"" + quiz[2].split("'")[1]);
                        System.out.print("\", \"score\" : \"" + quiz[3] + "\", ");
                        indexInList++;
                        System.out.print("\"index_in_list\" : \"" + indexInList + "\"}");
                        if ((line = bufferedReader.readLine()) != null)
                            System.out.print(", ");

                    }
                    if (indexInListForComma == indexInList)
                        line = bufferedReader.readLine();

                } while (line != null);
                bufferedReader.close();
                return;
            }
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
