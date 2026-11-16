package org.example;

import javafx.application.Application;

/**
 * Entry point of the program.
 * Starting JavaFX from a separate class (one that doesn't extend
 * Application) avoids the "JavaFX runtime components are missing" error.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}