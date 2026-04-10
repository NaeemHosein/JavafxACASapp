/**
 * Controller for Customer login fxml file.
 * Registered info is stored in Vehicle Owner Table and
 * Users are redirected to login page automatically
 * Added functional back button for navigation and some field validations.
 * */


package com.cts.javafxacasapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;

public class RegisterCustomerController {


    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private ComboBox<String> cmbMake;
    @FXML private ComboBox<String> cmbModel;
    @FXML private ComboBox<String> cmbEngineType;
    @FXML private ComboBox<String> cmbYear;

    @FXML private Label errorLabel;

    // loading data into combo boxes for selection
    @FXML
    public void initialize() {
        String make = cmbMake.getValue();

        AppUtils.loadMakes(cmbMake);
        AppUtils.loadYears(cmbYear);
        AppUtils.loadEngines(cmbEngineType);

        // Adding listener for make selection
        cmbMake.valueProperty().addListener((obs, oldMake, newMake) -> {
            if (newMake != null) {
                cmbModel.getItems().clear();
                AppUtils.loadModels(cmbModel, newMake);
            }
        });
    }

    // defining register button - storing data in Vehicle Owner Table
    @FXML
    private void handleRegister(ActionEvent event) throws IOException {

        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String make = cmbMake.getValue();
        String model = cmbModel.getValue();
        String engineType = cmbEngineType.getValue();
        String year = cmbYear.getValue();



        // added validation for missing fields
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
                password.isEmpty() || make.isEmpty() ||
                model.isEmpty() || engineType.isEmpty() || year.isEmpty()) {

            AppUtils.showError(errorLabel, "Please fill in all fields.");
            return;
        }



        // adding info to vehicle owner table
        try {
            DatabaseConnection db = new DatabaseConnection();
            

            //Get or create vehicle
            int vehicleId = AppUtils.getVehicleId(db, make, model, engineType, Integer.parseInt(year));


            String query = """
            INSERT INTO tblvehicle_owner
            (vehicle_id, email, full_name, username, password)
            VALUES (?, ?, ?, ?, ?)
            """;

            PreparedStatement owner = db.conn.prepareStatement(query);

            owner.setInt(1, vehicleId);
            owner.setString(2, email);
            owner.setString(3, fullName);
            owner.setString(4, username);
            owner.setString(5, password);

            int rowsInserted = owner.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Vehicle Owner successfully inserted into database.");
            }

            owner.close();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlert("Error", "Registration Failed! Please ensure that fields are filled correctly.");
            return;
        }

        AppUtils.hideError(errorLabel);
        System.out.println("New Customer Registered! Redirecting to Login... ");

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

