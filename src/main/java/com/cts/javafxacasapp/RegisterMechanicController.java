package com.cts.javafxacasapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterMechanicController {

    // ===== FIELDS FROM YOUR FXML =====
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private TextField businessNameField;
    @FXML private TextField phoneField;
    @FXML private TextField experienceField;

    @FXML private Label errorLabel;

    // ===== REGISTER BUTTON =====
    @FXML
    private void handleRegister(ActionEvent event) {

        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String business = businessNameField.getText();
        String phone = phoneField.getText();
        String experience = experienceField.getText();

        // Basic validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
                password.isEmpty() || business.isEmpty() ||
                phone.isEmpty() || experience.isEmpty()) {

            errorLabel.setText("Please fill in all fields.");
            errorLabel.setVisible(true);
            return;
        }

        // Optional: validate number
        try {
            Integer.parseInt(experience);
        } catch (NumberFormatException e) {
            errorLabel.setText("Experience must be a number.");
            errorLabel.setVisible(true);
            return;
        }

        // SUCCESS
        errorLabel.setVisible(false);

        System.out.println("=== NEW MECHANIC REGISTERED ===");
        System.out.println("Name: " + fullName);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Business: " + business);
        System.out.println("Phone: " + phone);
        System.out.println("Experience: " + experience + " years");

        showAlert("Success", "Account created successfully!");

        // OPTIONAL: Redirect to login after register
        goToLogin(event);
    }

    // ===== BACK BUTTON =====
    @FXML
    private void handleBack(ActionEvent event) {
        goToLogin(event);
    }

    // ===== SCENE SWITCH METHOD =====
    private void goToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/cts/javafxacasapp/javafx-ACAS-app-view.fxml")
            );

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load login page.");
        }
    }

    // ===== ALERT METHOD =====
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleLogin(ActionEvent actionEvent) {
    }
}