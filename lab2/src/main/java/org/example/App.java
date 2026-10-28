package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main JavaFX class.
 * Loads the valid users from the input file, opens the login window,
 * and switches to the welcome screen after a successful login.
 */
public class App extends Application {

    /** The input file with all users (valid and invalid). */
    private static final String USERS_FILE = "Users.txt";

    /** The main window. We keep it so we can change its scene later. */
    private static Stage stage;

    /** Holds only the valid users from the file. */
    private static UserRepository repository;

    /**
     * Called by JavaFX when the app starts.
     * 1. Reads the file into an ArrayList of valid users.
     * 2. Shows the login screen.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        repository = new UserRepository(USERS_FILE);

        stage = primaryStage;
        stage.setTitle("Users Login");

        // Clicking X closes the window and ends the program
        stage.setOnCloseRequest(event -> Platform.exit());

        FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    /** @return the repository with the valid users */
    public static UserRepository getRepository() {
        return repository;
    }

    /**
     * Replaces the login screen with the welcome screen
     * (same window, new scene loaded from welcome.fxml).
     *
     * @param username the user who logged in
     */
    public static void showWelcome(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("welcome.fxml"));
            Scene welcomeScene = new Scene(loader.load());

            WelcomeController controller = loader.getController();
            controller.setUsername(username);

            stage.setScene(welcomeScene);
        } catch (IOException e) {
            throw new RuntimeException("Could not load welcome screen", e);
        }
    }
}