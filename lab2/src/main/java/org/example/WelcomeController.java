package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for welcome.fxml.
 * Shows a welcome message for the user who logged in.
 */
public class WelcomeController {

    @FXML
    private Label welcomeLabel;

    /**
     * Sets the welcome text with the user's name.
     * Called by App right after loading this screen.
     */
    public void setUsername(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }
}