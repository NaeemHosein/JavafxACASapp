package com.cts.javafxacasapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class RegisterController {

    // Text Fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField streetField;
    @FXML private TextField villageField;
    @FXML private TextField cityField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;

    // Controls
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextArea notesArea;

    // Buttons (already exist in your FXML)
    @FXML private Button loginButton;     // REGISTER button
    @FXML private Button registerButton;  // BACK button

    // Initialize method (runs automatically)
    @FXML
    public void initialize() {
        // Populate ComboBoxes
        countryComboBox.getItems().addAll(
                "Trinidad and Tobago", "USA", "Canada", "UK"
        );

        roleComboBox.getItems().addAll(
                "Admin", "Mechanic", "Customer"
        );
    }

    // REGISTER button
    @FXML
    private void handleLogin() {
        // Collect data
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty() ||
                email.isEmpty() || username.isEmpty() || password.isEmpty()) {

            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        // Simulate registration logic
        System.out.println("User Registered:");
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Username: " + username);

        showAlert("Success", "Registration successful!");
    }

    // BACK button
    @FXML
    private void handleRegister() {
        // Navigation logic (you can replace with scene switch)
        System.out.println("Back to login screen");
    }

    // Utility Alert Method
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}