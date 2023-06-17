package com.example.project;

import java.io.*;

import static com.example.project.Files.fQuestions;

public class Question {
    private static final String splitBy = ",";
    private static int staticNumberOfAnswers; // number of answers
    private static String[] answers = new String[6]; // array of answers for the question
    private static int[] staticCorrectAnswers = new int[6]; // the truth value of the question: 0 - false, 1 - true
    private static int staticQuestionID = 1; // the ID "generator" for the questions
    private User qUser; // user that created the question
    private String text; // question text
    private int type; // 0 - yes/no question, 1 - multiple choice question
    private int numberOfAnswers; // number of answers
    private int[] correctAnswers; // the truth value of the question: 0 - false, 1 - true
    private int questionID;

    public Question(User user, String text, int type) {
        this.qUser = user;
        this.text = text;
        this.type = type;
        this.numberOfAnswers = staticNumberOfAnswers;
    }

    public Question(String ID, User user, String text, int type, int nrAnswers) {
        this.questionID = Integer.parseInt(ID.split("'")[1]);
        this.qUser = user;
        this.text = text;
        this.type = type;
        this.numberOfAnswers = nrAnswers;
    }

    public static int getEtIncrementStaticQuestionID() {
        return ++staticQuestionID;
    }

    public static void addAnswer(String answer, int correctAnswer) {
        /* can be done with clonable if I have time*/
        if (staticNumberOfAnswers < 6) {
            answers[staticNumberOfAnswers] = answer;
            staticCorrectAnswers[staticNumberOfAnswers] = correctAnswer;
            staticNumberOfAnswers++;
        }

    }

    public static void resetStaticID() {
        staticQuestionID = 1;
    }

    public static void resetStaticVariablesQuestions() {
        staticNumberOfAnswers = 0;
        answers = new String[6];
        staticCorrectAnswers = new int[6];
    }

    /**
     * <p>Check if the answers are valid (they have a text and have a state) and adds them to the answers array
     * each answer should be followed by a state (0 or 1).</p>
     * <p>If the question is single answer, there should be only one correct answer.</p>
     * <p>If the question is multiple answers, there should be more than one correct answer.</p>
     *
     * @param args array of the command line arguments
     *             from the position 5 to the end of the array
     * @param type of the question
     *             <p>1 = single answer, 2 = multiple answers</p>
     * @return <p>false =  the answer is not valid</p>
     * <p>true = the answer is valid</p>
     */
    public static boolean checkAnswersAndCreate(final String[] args, int type) {
        int nTrueAnswers = 0;
        int nAnswers = 1;
        int position = 0;

        while (position / 2 < (args.length - 5) / 2) {
            if (args[5 + position].startsWith("-answer-" + (nAnswers) + " ") &&
                    args[5 + position].substring(10).length() > 0) {
                if (args[5 + position + 1].startsWith("-answer-" + (nAnswers) + "-is-correct") &&
                        args[5 + position + 1].substring(21).length() > 0) {
                    // format: "-answer-i 'text'" "-answer-i-is-correct '1'"
                    // check if the answer text is already in the array
                    for (int i = 0; i < staticNumberOfAnswers; i++) {
                        if (args[5 + position].substring(10).equals(answers[i])) {
                            System.out.println("{'status':'error','message':'Same answer provided more than once'}");
                            return false;
                        }
                    }

                    /* check if the answer state is valid */
                    if (args[5 + position + 1].substring(21).equals("'1'")) {
                        addAnswer(args[5 + position].substring(10), 1);
                        nTrueAnswers++;
                    } else if (args[5 + position + 1].substring(21).equals("'0'")) {
                        addAnswer(args[5 + position].substring(10), 0);
                    }

                    nAnswers++;
                    position += 2;
                } else {
                    System.out.print("{'status':'error','message':'Answer " + (nAnswers) + " has no answer correct flag'}");
                    return false;
                }
            } else {
                System.out.print("{ 'status' : 'error', 'message' : 'Answer " + (nAnswers) + " has no answer description'}");
                return false;
            }
        } // while loop

        if (nTrueAnswers == 0) {
            System.out.print("{ 'status' : 'error', 'message' : 'No correct answers provided'}");
            return false;
        }

        if (type == 2 && nTrueAnswers == 1) {
            System.out.print("{ 'status' : 'error', 'message' : 'Only one correct answer provided to a multiple answers question'}");
            return false;
        }

        if (type == 1 && nTrueAnswers > 1) {
            System.out.print("{ 'status' : 'error', 'message' : 'Single correct answer question has more than one correct answer'}");
            return false;
        }
        return true;
    }

    /**
     * Check if the ID of the question is in the file.
     *
     * @param ID Of the question.
     * @return <p>1 - question ID exists</p>
     * <p>0 - question ID was not found</p>
     * <p>-1 - error</p>
     */
    public static int checkQuestionID(String ID) {
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fQuestions));
            while ((line = bufferedReader.readLine()) != null) {
                String[] question = line.split(splitBy);
                if (question[0].equals(ID)) {
                    bufferedReader.close();
                    return 1;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * Check if the question text corresponds to an ID.
     *
     * @param text of the question
     * @return <p>1 - question text found</p>
     * <p>0 - question text was not found</p>
     * <p>-1 - error</p>
     */
    public static int getQuestionIDByText(String text) {

        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fQuestions));
            while ((line = bufferedReader.readLine()) != null) {
                String[] question = line.split(splitBy);
                if (question[2].equals(text)) {
                    bufferedReader.close();
                    return 1;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * Print all the questions in the file and their ID.
     */
    public static void getAllQuestions() {
        int nQuestions = 1;
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fQuestions));
            while ((line = bufferedReader.readLine()) != null) {
                System.out.print("{\"question_id\" : \"" + line.split(splitBy)[0].split("'")[1] + "\", ");
                System.out.print("\"question_name\" : \"" + line.split(splitBy)[2].split("'")[1] + "\"}");
                if (nQuestions < staticQuestionID - 1) {
                    System.out.print(", ");
                    nQuestions++;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get a Question object from the file by ID.
     *
     * @param strID of the question.
     * @return Question object corresponding to the ID.
     */
    public static Question getQuestionByID(String strID) {
        String line;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    fQuestions));
            while ((line = bufferedReader.readLine()) != null) {
                String[] question = line.split(splitBy);
                if (question[0].equals(strID)) {
                    // questionID, userID, questionText, questionType, numberOfAnswers, answer1, correctAnswer1, ..., answerN, correctAnswerN
                    User user = User.getUserByID(question[1]);
                    Question gotQuestion = new Question(question[0], user, question[2], Integer.parseInt(question[3]), Integer.parseInt(question[4]));
                    int[] answersValue = new int[gotQuestion.getNumberOfAnswers()];
                    for (int index = 0; index < gotQuestion.getNumberOfAnswers(); index++) {
                        answersValue[index] = Integer.parseInt(question[5 + index * 2 + 1]);
                    }
                    gotQuestion.setCorrectAnswers(answersValue);
                    bufferedReader.close();
                    return gotQuestion;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null; // question ID was not found
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String[] getanswers() {
        return answers;
    }

    public int[] getCorrectanswers() {
        return staticCorrectAnswers;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getqUser() {
        return qUser;
    }

    public void setqUser(User qUser) {
        this.qUser = qUser;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(int numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public int[] getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int[] correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    /**
     * Check if the question is valid.
     *
     * @param text of the question
     * @return <p>1 - question text found</p>
     * <p>0 - question text was not found</p>
     * <p>-1 - error</p>
     */
    public int checkQuestionText(String text) {
        // check if the question text is empty
        String line;
        if (text == null) {
            System.out.print("{'status':'error','message':'No question text provided'}");
            return -1; // question text was not specified
        } else {
            try {
                BufferedReader buffer = new BufferedReader(new FileReader(fQuestions));
                while ((line = buffer.readLine()) != null) {

                    String[] question = line.split(splitBy);
                    if (question[2] != null && question[2].equals(text)) {
                        System.out.print("{'status':'error','message':'Question already exists'}");
                        buffer.close();
                        return 0; // question text already exists
                    }
                }
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 1; // question text is valid
    }

    /**
     * Adds to the file "questions.txt" the question details by this order:
     * questionID, userID, questionText, questionType, numberOfAnswers, answer1, correctAnswer1, ..., answerN, correctAnswerN
     */
    public void addQuestion() {
        if (1 == checkQuestionText(this.getText())) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(fQuestions, true));
                PrintWriter out = new PrintWriter(bw);
                // add to file qestion ID and the user ID
                questionID = staticQuestionID;
                staticQuestionID++;
                out.print("'" + this.questionID + "'" + "," + "'" + this.qUser.getUserID() + "'" + ",");
                // add to file the text, the type and number of answers of the question
                out.print(this.text + "," + this.type + "," + staticNumberOfAnswers + ",");
                // add to file the answers
                for (int i = 0; i < staticNumberOfAnswers; i++) {
                    if (i == staticNumberOfAnswers - 1) {
                        out.println(this.getanswers()[i] + "," + this.getCorrectanswers()[i]);
                    } else {
                        out.print(this.getanswers()[i] + "," + this.getCorrectanswers()[i] + ",");
                    }
                }
//                resetStaticVariablesQuestions();
                System.out.println("{ 'status' : 'ok', 'message' : 'Question added successfully'}");
                out.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @return the number of correct answers of the current question
     */
    public int getNumberOfCorrectAnswers() {
        int numberCorrect = 0;
        for (int i = 0; i < getNumberOfAnswers(); i++) {
            if (correctAnswers[i] == 1) {
                numberCorrect++;
            }
        }
        return numberCorrect;
    }


}



