package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for login.fxml.
 * Reads the username and password the user typed and, when the
 * login button is clicked, checks them against the valid users.
 */
public class LoginController {

    // These are connected automatically to the fx:id's in login.fxml
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    /**
     * Event handler for the login button (onAction="#onLoginClick").
     * If the user exists: switches to the welcome screen.
     * Otherwise: shows an error message inside the window (not a pop-up).
     */
    @FXML
    private void onLoginClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (App.getRepository().isValidLogin(username, password)) {
            App.showWelcome(username);
        } else {
            errorLabel.setText("user or password do not match");
        }
    }
}