package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Holds the list of valid users and answers login checks.
 * Reuses the Lab 1 logic: a line is kept only if User's
 * constructor accepts its username and password.
 */
public class UserRepository {

    /** Only the valid users from the input file. */
    private final ArrayList<User> users = new ArrayList<>();

    /**
     * Reads the input file. Each line should be: "username password".
     * Lines with the wrong format, or rejected by User (invalid email
     * or password), are skipped.
     *
     * @param fileName path to the users file
     * @throws FileNotFoundException if the file doesn't exist
     */
    public UserRepository(String fileName) throws FileNotFoundException {
        try (Scanner reader = new Scanner(new File(fileName))) {
            while (reader.hasNextLine()) {
                String[] parts = reader.nextLine().trim().split("\\s+");
                if (parts.length != 2) {
                    continue; // not "username password"
                }
                try {
                    users.add(new User(parts[0], parts[1]));
                } catch (IllegalArgumentException e) {
                    // invalid user, so it's not added to the list
                }
            }
        }
    }

    /**
     * Checks if a user with exactly this username and password exists.
     *
     * @return true if found, false otherwise
     */
    public boolean isValidLogin(String username, String password) {
        for (User user : users) {
            if (user.GetUsername().equals(username)
                    && user.GetPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    /** @return the list of valid users */
    public ArrayList<User> getUsers() {
        return users;
    }
}