package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for login.fxml.
 * Kept "thin": it reads the fields, starts the background tasks,
 * and shows their results. There is no counting or timing logic
 * here: counting is in AttemptTracker/UserState, timing is in
 * CountdownTask.
 */
public class LoginController {

    // These are connected automatically to the fx:id's in login.fxml
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button testButton;

    @FXML
    private Label testLabel;

    /**
     * Event handler for the login button (onAction="#onLoginClick").
     * Runs on the JavaFX thread, so it must not do slow work:
     * it only disables the inputs and starts a LoginTask thread.
     */
    @FXML
    private void onLoginClick() {
        String email = usernameField.getText().trim();
        String password = passwordField.getText();

        setInputsDisabled(true);   // prevents double clicks
        errorLabel.setText("");

        LoginTask task = new LoginTask(email, password,
                App.getRepository(), App.getTracker(), App.getFailureQueue(),
                this::showResult);
        App.startBackground(task, "LoginTask");
    }

    /**
     * Shows the result of a login.
     * Called by LoginTask through Platform.runLater,
     * so it runs on the JavaFX thread (allowed to touch the screen).
     */
    private void showResult(LoginResult result) {
        switch (result.getType()) {
            case SUCCESS -> App.showWelcome(result.getEmail());

            case WRONG_DETAILS -> {
                errorLabel.setText("Wrong details");
                setInputsDisabled(false);
            }

            case WRONG_PASSWORD -> {
                errorLabel.setText("Wrong details, "
                        + result.getAttemptsLeft() + " attempts left");
                setInputsDisabled(false);
            }

            case BLOCKED -> startCountdown(result.getSecondsLeft());
        }
    }

    /**
     * Keeps the inputs disabled and starts a live countdown.
     * When it ends, the message disappears and the inputs open again.
     */
    private void startCountdown(long seconds) {
        showBlocked(seconds);
        CountdownTask countdown = new CountdownTask(seconds,
                this::showBlocked,
                () -> {
                    errorLabel.setText("");
                    setInputsDisabled(false);
                });
        App.startBackground(countdown, "CountdownTask");
    }

    /** Shows the blocked message (called every second by CountdownTask). */
    private void showBlocked(long secondsLeft) {
        errorLabel.setText("Account blocked, " + secondsLeft + " seconds left");
    }

    /**
     * Event handler for the "Run concurrency test" button.
     * Runs ConcurrencyTest in the background and shows its report.
     */
    @FXML
    private void onTestClick() {
        testButton.setDisable(true);
        testLabel.setText("Running...");
        ConcurrencyTest test = new ConcurrencyTest(report -> {
            testLabel.setText(report);
            testButton.setDisable(false);
        });
        App.startBackground(test, "ConcurrencyTest");
    }

    /** Enables or disables the fields and the login button together. */
    private void setInputsDisabled(boolean disabled) {
        usernameField.setDisable(disabled);
        passwordField.setDisable(disabled);
        loginButton.setDisable(disabled);
    }
}
