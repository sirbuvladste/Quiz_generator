package com.example.project;

import java.io.File;
import java.io.IOException;

public class Files {
    static File fUser = new File("src/main/java/com/example/project/users.txt");
    static File fQuestions = new File("src/main/java/com/example/project/questions.txt");
    static File fQuiz = new File("src/main/java/com/example/project/quizzes.txt");
    static File fSubmit = new File("src/main/java/com/example/project/submissions.txt");

    /**
     * Delete all the files and reset variables.
     * @param variant - true for reset all static variables and delete files /
     *               false delete only files
     */
    public static void cleanup_all(boolean variant) {
        if(variant) {
            User.resetStaticUserID();
            Question.resetStaticID();
            Question.resetStaticVariablesQuestions();
            Quiz.resetStaticVariablesQuiz();
        }
        if(fUser.delete())
            System.out.print("{'status':'error','message': 'Failed to delete the file with the path \"" + fUser +  "\"'}");

        if(fQuestions.delete())
            System.out.print("{'status':'error','message': 'Failed to delete the file with the path \"" + fQuestions +  "\"'}");

        if(fQuiz.delete())
            System.out.print("{'status':'error','message': 'Failed to delete the file with the path \"" + fQuiz +  "\"'}");

        if(fSubmit.delete())
            System.out.print("{'status':'error','message': 'Failed to delete the file with the path \"" + fSubmit +  "\"'}");

    }
    public static void create_files() {
        try {
            fUser.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred creating the file " + fUser);
            e.printStackTrace();
        }

        try {
            fQuestions.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred creating  the file " + fQuestions);
            e.printStackTrace();
        }

        try {
            fQuiz.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred creating the file " + fQuiz);
            e.printStackTrace();
        }

        try {
            fSubmit.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred creating  the file " + fSubmit);
            e.printStackTrace();
        }
    }
}
