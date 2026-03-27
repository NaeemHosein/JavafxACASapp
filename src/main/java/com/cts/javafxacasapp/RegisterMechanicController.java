/**
 * Controller for Mechanic login fxml file.
 * Registered info is stored in Mechanic Table and
 * Users are redirected to login page automatically
 * Added functional back button for navigation and some field validations.
 * */


package com.cts.javafxacasapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.PreparedStatement;

public class RegisterMechanicController {


    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private TextField businessNameField;
    @FXML private TextField phoneField;
    @FXML private TextField experienceField;

    @FXML private Label errorLabel;

    // defining register button - storing data in Mechanics Table
    @FXML
    private void handleRegister(ActionEvent event) throws IOException {

        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String business = businessNameField.getText();
        String phone = phoneField.getText();
        String experience = experienceField.getText();

        // added validation for missing fields
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
                password.isEmpty() || business.isEmpty() ||
                phone.isEmpty() || experience.isEmpty()) {

            errorLabel.setText("Please fill in all fields.");
            errorLabel.setVisible(true);
            return;
        }



        // adding info to mechanic table
        try {
            DatabaseConnection db = new DatabaseConnection();

            String query = """
            INSERT INTO tblmechanic
            (full_name, username, password, email, business_name, years_experience, phone_number)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;   //

            PreparedStatement mechanic = db.conn.prepareStatement(query);

            mechanic.setString(1, fullName);
            mechanic.setString(2, username);
            mechanic.setString(3, password);
            mechanic.setString(4, email);
            mechanic.setString(5, business);
            mechanic.setInt(6, Integer.parseInt(experience));
            mechanic.setString(7, phone);

            int rowsInserted = mechanic.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Mechanic successfully inserted into database.");
            }

            mechanic.close();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlert("Error", "Registration Failed! Please ensure that all fields are filled according to the guidelines provided.");
            return;
        }

        errorLabel.setVisible(false);
        System.out.println("New Mechanic Registered! Redirecting to Login... ");

        AppUtils.showAlert("Success", "Your account is now active! Please login using your registered credentials.");

        JavafxACASapp.changeScene("javafx-ACAS-app-view.fxml",1280,720);
    }

    // defining back button action - returns to login page
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-view.fxml",1280,720);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

