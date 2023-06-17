package com.example.project;

import java.io.*;

import static com.example.project.Files.fUser;

public class User {
    private static final String splitBy = ",";
    private static int staticUserID = 0;
    private String username;
    private String password;
    private int userID;

    /**
     * Used for creating a new user in the database.
     */
    public User(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
        this.setUserID(getEtIncrementStaticUserID());
    }

    public User(String ID, String username, String password) {
        int convertID = Integer.parseInt(ID.substring(1, 2));
        this.setUsername(username);
        this.setPassword(password);
        this.setUserID(convertID);
    }

    public static void resetStaticUserID() {
        staticUserID = 0;
    }

    /**
     * This method checks if the username and password are provided, valid and
     * can be added to the database.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return <p>0 username with password are in the database.</p>
     * <p>1 username is not in the database, can be used.</p>
     * <p>2 password for the given username is incorrect.</p>
     * <p>3 username is not given.</p>
     * <p>4 password is not given.</p>
     */
    public static int checkUser(String username, String password) {
        String line;
        if (username == null) {
            System.out.print("{'status':'error','message':'Please provide password'}");
            return 3; // username was not specified
        }
        if (password == null) {
            System.out.print("{'status':'error','message':'Please provide password'}");
            return 4; // password was not specified
        }
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(fUser));
            while ((line = buffer.readLine()) != null) {

                String[] user = line.split(splitBy);
                if (user[1].equals(username)) {
                    if (user[2].equals(password)) {
                        buffer.close();
                        return 0; // username with password already exists
                    } else {
                        buffer.close();
                        return 2; // password is incorrect
                    }
                }
            }
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1; // username does not exist in the database, can be used
    }

    /**
     * Add the ID, username and password of the user to "users.txt".
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public static void addUser(String username, String password) {
        int userCheck = checkUser(username, password);
        if (userCheck == 1) {
            User user = new User(username, password);
            try {
                FileWriter fw = new FileWriter(fUser, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
                out.println("'" + user.userID + "'" + "," + user.username + "," + user.password);
                out.close();
                bw.close();
                fw.close();
                System.out.print("{ 'status' : 'ok', 'message' : 'User created successfully'}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (userCheck == 0)
            System.out.print("{'status':'error','message':'User already exists'}");
    }

    /**
     * Return the user as object if username and password are correct.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return <p>Return the user as User object if username and password are correct</p>
     * <p>Return null if username and password are incorrect</p>
     */
    public static User getUser(String username, String password) {
        String line;
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(
                    fUser));
            while ((line = buffer.readLine()) != null) {
                String[] user = line.split(splitBy);
                if (user[1].equals(username) && user[2].equals(password)) {
                    buffer.close();
                    return new User(user[0], user[1], user[2]);
                }
            }
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return the user as object if ID is in the "users.txt".
     *
     * @param ID The ID of the user.
     * @return <p>Return the user as User object if username and password are correct</p>
     * <p>Return null if username and password are incorrect</p>
     */
    public static User getUserByID(String ID) {
        String line;
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(
                    fUser));
            while ((line = buffer.readLine()) != null) {
                String[] user = line.split(splitBy);
                if (user[0].equals(ID)) {
                    buffer.close();
                    return new User(user[0], user[1], user[2]);
                }
            }
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getEtIncrementStaticUserID() {
        return ++staticUserID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}