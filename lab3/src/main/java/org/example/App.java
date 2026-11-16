package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Main JavaFX class.
 * Reads n and t from the command line, loads the valid users,
 * opens the login window, and switches to the welcome screen
 * after a successful login.
 */
public class App extends Application {

    /** The input file with all users (valid and invalid). */
    private static final String USERS_FILE = "Users.txt";

    /** The main window. We keep it so we can change its scene later. */
    private static Stage stage;

    /** Holds only the valid users from the file. */
    private static UserRepository repository;

    /** n: wrong logins allowed before blocking. */
    private static int maxFailures;

    /** t: how long (seconds) an email stays blocked. */
    private static int blockSeconds;

    /**
     * Called by JavaFX when the app starts.
     * 1. Reads and checks n and t (asks the user if they're invalid).
     * 2. Reads the file into an ArrayList of valid users.
     * 3. Shows the login screen.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // NEW: read n and t before opening any screen
        List<String> args = getParameters().getRaw();
        maxFailures  = readArg(args, 0, "n (max wrong logins)");
        blockSeconds = readArg(args, 1, "t (block time in seconds)");
        System.out.println("n = " + maxFailures + ", t = " + blockSeconds);

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

    /** @return n */
    public static int getMaxFailures() {
        return maxFailures;
    }

    /** @return t in seconds */
    public static int getBlockSeconds() {
        return blockSeconds;
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

    // ---------- Argument checking (NEW in Lab 3) ----------

    /**
     * Returns args[index] if it is a whole number >= 1.
     * If it's missing or invalid, asks the user to type it.
     */
    private static int readArg(List<String> args, int index, String name) {
        if (index < args.size()) {
            int value = toInt(args.get(index));
            if (value >= 1) {
                return value;
            }
        }
        return askUser(name);
    }

    /** Converts text to int, or returns -1 if it isn't a whole number. */
    private static int toInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Shows a small input window until the user types a valid number. */
    private static int askUser(String name) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Missing argument");
        dialog.setHeaderText("Enter " + name + " (whole number ≥ 1):");

        while (true) {
            Optional<String> input = dialog.showAndWait();
            if (input.isEmpty()) {        // user pressed Cancel or X
                System.exit(0);
            }
            int value = toInt(input.get());
            if (value >= 1) {
                return value;
            }
            dialog.setHeaderText(name + " must be a whole number ≥ 1. Try again:");
        }
    }
}