/**
 * Dedicated space to store methods that can be used across all controllers
 * showAlert(title, message) - generates a popup alert with inserted title and message
 * showError(message) - sets error label to visible and prints inserted message
 * hideError() - hides error label (technically this one isn't as necessary)
 */

package com.cts.javafxacasapp;


import javafx.scene.control.*;

import javafx.scene.control.Label;


public class AppUtils {


    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public static void hideError(Label errorLabel) {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}