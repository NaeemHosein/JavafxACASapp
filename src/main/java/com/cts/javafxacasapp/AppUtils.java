/**
 * Dedicated space to store methods that can be used across all controllers
 *UI Helpers:
 * showAlert(title, message) - generates a popup alert with inserted title and message
 * showError(message) - sets error label to visible and prints inserted message
 * hideError() - hides error label (technically this one isn't as necessary)
 * Database Helpers:
 * loadMakes(cmbMake) -loads list of vehicle makes from vehicles table for combo box
 * loadYears(cmbYear) - loads years 1990-2026 for combo box
 * loadEngines(cmbEngineType) -loads list of engines from vehicles table for combo box
 * loadModels(cmbModel, String make) - loads all models for a selected make
 * getVehicleId( db, make, model, engine, year)- gets matching vehicle id from the vehicle table or creates one if it doesn't exixt.
 */

package com.cts.javafxacasapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AppUtils {

    //UI helpers:


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

    public static void loadMakes(ComboBox<String> cmbMake) {
        try {
            DatabaseConnection db = new DatabaseConnection();

            String query = "SELECT DISTINCT vehicle_make FROM tblvehicles";
            PreparedStatement ps = db.conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbMake.getItems().add(rs.getString("vehicle_make"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadYears(ComboBox<String> cmbYear) {
        for (int i = 1990; i <= 2026; i++) {
            cmbYear.getItems().add(String.valueOf(i));
        }
    }

    public static void loadEngines(ComboBox<String> cmbEngineTypes) {
        try {
            DatabaseConnection db = new DatabaseConnection();

            String query = "SELECT DISTINCT engine_type FROM tblvehicles";
            PreparedStatement ps = db.conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbEngineTypes.getItems().add(rs.getString("engine_type"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadModels(ComboBox<String> cmbModel, String make) {
        try {
            DatabaseConnection db = new DatabaseConnection();

            String query = "SELECT DISTINCT vehicle_model FROM tblvehicles WHERE vehicle_make = ?";
            PreparedStatement ps = db.conn.prepareStatement(query);
            ps.setString(1, make);

            ResultSet rs = ps.executeQuery();

            cmbModel.getItems().clear();

            while (rs.next()) {
                cmbModel.getItems().add(rs.getString("vehicle_model"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getVehicleId(DatabaseConnection db, String make, String model, String engine, int year) {

        try {
            // checking for vehicle in table
            String checkQuery = """
            SELECT vehicle_id FROM tblvehicles
            WHERE vehicle_make = ?
            AND vehicle_model = ?
            AND engine_type = ?
            AND year = ?
        """;

            PreparedStatement vehicles = db.conn.prepareStatement(checkQuery);
            vehicles.setString(1, make);
            vehicles.setString(2, model);
            vehicles.setString(3, engine);
            vehicles.setInt(4, year);

            ResultSet rs = vehicles.executeQuery();

            // if match found return id
            if (rs.next()) {
                return rs.getInt("vehicle_id");
            }

            // no match found add to table
            String insertQuery = """
            INSERT INTO tblvehicles
            (vehicle_make, vehicle_model, engine_type, year)
            VALUES (?, ?, ?, ?)
        """;

            PreparedStatement newVehicle = db.conn.prepareStatement(
                    insertQuery,
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            newVehicle.setString(1, make);
            newVehicle.setString(2, model);
            newVehicle.setString(3, engine);
            newVehicle.setInt(4, year);

            newVehicle.executeUpdate();

            // getting id of new vehicle
            ResultSet generatedKeys = newVehicle.getGeneratedKeys();

            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Navigation Helpers:

    public static void navigateToDashboard(ActionEvent event) {
        String role = SessionManager.getInstance().getUserRole();
        String fxml = switch (role) {
            case "admin" -> "javafx-ACAS-app-admin-dash.fxml";
            case "mechanic" -> "javafx-ACAS-app-dash.fxml";
            case "customer" -> "javafx-ACAS-app-customer-dash.fxml";
            default -> {
                SessionManager.getInstance().clearSession();
                yield "javafx-ACAS-app-login.fxml";
            }
        };

        navigateTo(event, fxml);
    }

    public static void navigateTo(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(
                    AppUtils.class.getResource(fxml)
            );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Logout() {
    }
}
