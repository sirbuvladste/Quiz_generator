package com.example.project;

import java.io.*;

import static com.example.project.Files.fQuiz;
import static com.example.project.Question.checkQuestionID;
import static com.example.project.Submit.getCompletedState;

public class Quiz {
    private static final String splitBy = ",";
    private static int staticQuizID = 0;
    private static String[] qestionIDs = new String[10];
    private static int nrQuestionsInQuiz = 0;
    private int quizID;
    private User user;
    private String nameQuiz;
    private Question[] quizQuestions;

    public Quiz(String name, User user) {
        this.quizID = ++staticQuizID;
        this.nameQuiz = name;
        this.user = user;
    }

    public Quiz(String ID, String name, User user) {
        this.quizID = Integer.parseInt(ID.split("'")[1]);
        this.nameQuiz = name;
        this.user = user;
    }

    public static int getStaticQuizID() {
        return staticQuizID;
    }

    public static int getNrQuestionsInQuiz() {
        return nrQuestionsInQuiz;
    }

    public static void setNrQuestionsInQuiz(int nrQuestionsInQuiz) {
        Quiz.nrQuestionsInQuiz = nrQuestionsInQuiz;
    }

    public static void resetStaticVariablesQuiz() {
        qestionIDs = new String[10];
        nrQuestionsInQuiz = 0;
        staticQuizID = 0;
    }

    /**
     * Check if the all the given question IDs are is valid.
     * If so, add them to the static array of quiz questions.
     *
     * @param args - the command arguments
     * @return <p>1 - all IDs are valid</p>
     * <p>0 - an ID does not exist</p>
     * <p>-1 - error</p>
     */
    public static int checkQuestionsForQuizAndCreate(final String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fQuiz));
            int length = args.length - 4; // -4 because the first 4 arguments are not questions
            while (nrQuestionsInQuiz < length) {
                String strID = (args[nrQuestionsInQuiz + 4].split(" "))[1];
                int check = checkQuestionID(strID);
                if (check == 0 || check == -1) {
                    br.close();
                    System.out.print("{'status':'error','message':'Question ID for question " + strID.split("'")[1] + " does not exist'}");
                    return 0;
                }
                qestionIDs[nrQuestionsInQuiz] = (args[nrQuestionsInQuiz + 4].split(" "))[1];
                setNrQuestionsInQuiz(nrQuestionsInQuiz + 1);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    /**
     * Check if the quiz name is valid to add or return its ID.
     *
     * @param name - ID of the quiz
     * @return <p>a number greater than 0 - quiz ID was found by name</p>
     * <p>0 - quiz name was not in file, can be used to create a new one</p>
     * <p>-1 - error</p>
     */
    public static int checkQuizIDByName(String name) {

        String line;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fQuiz));
            while ((line = bufferedReader.readLine()) != null) {
                String[] quiz = line.split(splitBy);
                if (quiz[2].equals(name)) {
                    bufferedReader.close();
                    return Integer.parseInt(quiz[0].split("'")[1]); // quiz name found
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0; // quiz name was not found
    }

    /**
     * Check if the ID is in file.
     *
     * @param ID of the quiz
     * @return <p>1 - quiz ID was found</p>
     * <p>0 - quiz ID is not in file</p>
     * <p>-1 - error</p>
     */
    public static int checkQuizID(String ID) {
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fQuiz));
            while ((line = bufferedReader.readLine()) != null) {
                String[] quiz = line.split(splitBy);
                if (quiz[0].equals(ID)) {
                    bufferedReader.close();
                    return 1; // quiz ID found
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0; // quiz ID was not found
    }


    /**
     * Print all the quizzes in the file and their state if submitted.
     */
    public static void getAllQuizzes() {
        String line;

        int nrQuizzes = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fQuiz));
            while ((line = bufferedReader.readLine()) != null) {
                System.out.print("{\"quizz_id\" : \"" + line.split(splitBy)[0].split("'")[1] + "\", ");
                System.out.print("\"quizz_name\" : \"" + line.split(splitBy)[2].split("'")[1] + "\", ");
                String state = getCompletedState(line.split(splitBy)[0].split("'")[1], line.split(splitBy)[1].split("'")[1]) ? "True" : "False";
                System.out.print("\"is_completed\" : \"" + state + "\"}");
                if (nrQuizzes < staticQuizID - 1) {
                    System.out.print(", ");
                    nrQuizzes++;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a copy of the "quizzes.txt" to "quiz_copy.txt" except the quiz with the given ID.
     *
     * @param quizIDToDelete - ID of the quiz to be deleted
     * @return <p>1 - quiz was deleted</p>
     * <p>0 - delet unsuccessful</p>
     */
    public static int deleteQuizFromFile(String quizIDToDelete) {
        String line;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fQuiz));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    "src/main/java/com/example/project/quiz_copy.txt"));
            PrintWriter out = new PrintWriter(bufferedWriter);
            while ((line = bufferedReader.readLine()) != null) {
                String[] quiz = line.split(splitBy);
                if (!quiz[0].equals(quizIDToDelete))
                    out.println(line);
            }

            bufferedReader.close();
            out.close();
            bufferedWriter.close();

            File fileQuizOld = new File("src/main/java/com/example/project/quizzes.txt");
            if (fileQuizOld.delete()) {
                File fileQuizNew = new File("src/main/java/com/example/project/quizzes.txt");
                File fileQuizCopy = new File("src/main/java/com/example/project/quiz_copy.txt");
                if (fileQuizCopy.renameTo(fileQuizNew))
                    return 1;
            }
            return 0;


        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Add the quiz to the file.
     */
    public void addQuiz() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fQuiz, true));
            PrintWriter out = new PrintWriter(bufferedWriter);
            out.print("'" + this.quizID + "'" + "," + "'" + this.user.getUserID() + "'" + ",");
            out.print(this.nameQuiz + "," + nrQuestionsInQuiz + ",");
            // get all the questions ID and add them to the quiz line
            for (int index = 0; index < nrQuestionsInQuiz; index++) {
                out.print(qestionIDs[index]);
                if (index < nrQuestionsInQuiz - 1)
                    out.print(",");
            }
            out.println();
            System.out.print("{ 'status' : 'ok', 'message' : 'Quizz added succesfully'}");
            out.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getQuizUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getQuizID() {
        return quizID;
    }

    public void setQuizID(String ID) {
        this.quizID = Integer.parseInt((ID.split("'"))[1]);
    }

    public void setNewQuizID() {
        this.quizID = ++staticQuizID;
    }

    public String getNameQuiz() {
        return nameQuiz;
    }

    public void setNameQuiz(String nameQuiz) {
        this.nameQuiz = nameQuiz;
    }

    public Question[] getQuizQuestions() {
        return quizQuestions;
    }

    public void setQuizQuestions(Question[] quizQuestions) {
        this.quizQuestions = quizQuestions;
    }


}