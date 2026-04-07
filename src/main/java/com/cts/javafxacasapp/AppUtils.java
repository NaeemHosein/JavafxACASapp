/**
 * Dedicated space to store methods that can be used across all controllers
 *
 * UI Helpers:
 * showAlert(title, message) - generates a popup alert with inserted title and message
 * showError(message) - sets error label to visible and prints inserted message
 * hideError() - hides error label (technically this one isn't as necessary)
 *
 * Database Helpers:
 * loadMakes(cmbMake) - loads list of vehicle makes from vehicles table for combo box
 * loadYears(cmbYear) - loads years 1990-2026 for combo box
 * loadEngines(cmbEngineType) - loads list of engines from vehicles table for combo box
 * loadModels(cmbModel, String make) - loads all models for a selected make
 * getVehicleId(db, make, model, engine, year) - gets matching vehicle id from the vehicle table or creates one if it doesn't exist.
 * getVehicleId(db, username)- get vehicle id for logged in customer
 * getVehicleDetails( db, vehicleId)- get vehicle make, model, year and engine type from vehicleid
 * loadDiagnosticCodes(cmbDiagnosticCode) - loads DTC for combo box
 * findDTC(code) - checks if DTC is in the system
 * getUserId(username, role) - grabbing userID using username and role stored in session manager
 * getCodeId(code) - grabs code ID from dtc table for code in parameter
 * saveRating(username, rating, feedback) - saves rating to session (customer) user table and returns true when successful
 *
 * Navigation Helpers:
 * navigateToDashboard(ActionEvent event) - navigate to customer, mechanic or admin dashboard based on user role
 * AppUtils.Logout() - clears session and logs out
 */

package com.cts.javafxacasapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.sql.SQLException;
import java.util.Objects;


public class AppUtils {

    // -------------------------------------------------------------------------
    // UI Helpers
    // -------------------------------------------------------------------------

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(Label errorLabel, String message) {
        errorLabel.setText("⚠ Error: " + message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px;");
    }

    public static void hideError(Label errorLabel) {
        errorLabel.setText("");
        errorLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
    }

    // -------------------------------------------------------------------------
    // Database Helpers
    // -------------------------------------------------------------------------

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

    public static void loadDiagnosticCodes(ComboBox<String> cmbDiagnosticCode) {
        try {
            DatabaseConnection db = new DatabaseConnection();
            String query = "SELECT DISTINCT code FROM tbldiagnostic_codes";
            PreparedStatement ps = db.conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbDiagnosticCode.getItems().add(rs.getString("code"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadPartNames(ComboBox<String> cmbPartName) {
        try {
            DatabaseConnection db = new DatabaseConnection();
            String query = "SELECT DISTINCT part_name FROM tblparts";
            PreparedStatement ps = db.conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbPartName.getItems().add(rs.getString("part_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getVehicleId(DatabaseConnection db, String make, String model, String engine, int year) {
        try {
            // Check if vehicle already exists
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

            // Return existing id if found
            if (rs.next()) {
                return rs.getInt("vehicle_id");
            }

            // Not found — insert new vehicle
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

            // Return generated id
            ResultSet generatedKeys = newVehicle.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


    public static int getVehicleId(DatabaseConnection db, String username) {
        try {
            String query = "SELECT vehicle_id FROM tblvehicle_owner WHERE username = ?";
            PreparedStatement ps = db.conn.prepareStatement(query);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("vehicle_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


    public static String[] getVehicleDetails(DatabaseConnection db, int vehicleId) {
        try {
            String query = "SELECT year, vehicle_make, vehicle_model, engine_type FROM tblvehicles WHERE vehicle_id = ?";
            PreparedStatement ps = db.conn.prepareStatement(query);
            ps.setInt(1, vehicleId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new String[]{
                        String.valueOf(rs.getInt("year")),
                        rs.getString("vehicle_make"),
                        rs.getString("vehicle_model"),
                        rs.getString("engine_type")
                };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean findDTC(String code) {
        try {
            DatabaseConnection db = new DatabaseConnection();
            String query = "SELECT COUNT(*) FROM tbldiagnostic_codes WHERE code = ?";
            PreparedStatement ps = db.conn.prepareStatement(query);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getUserId(String username, String role) {
        try {
            DatabaseConnection db = new DatabaseConnection();
            String query;
            String idColumn;

            // Choose table and id column based on role
            switch (role) {
                case "mechanic" -> {
                    query = "SELECT mechanic_id FROM tblmechanic WHERE username = ?";
                    idColumn = "mechanic_id";
                }
                case "admin" -> {
                    query = "SELECT admin_id FROM tbladministrator WHERE username = ?";
                    idColumn = "admin_id";
                }
                case "owner" -> {
                    query = "SELECT owner_id FROM tblvehicle_owner WHERE username = ?";
                    idColumn = "owner_id";
                }
                default -> {
                    return -1;
                }
            }

            PreparedStatement ps = db.conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(idColumn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // Use for error handling
    }

    public static int getCodeId(String code) {
        try {
            DatabaseConnection db = new DatabaseConnection();
            String query = "SELECT code_id FROM tbldiagnostic_codes WHERE code = ?";
            PreparedStatement ps = db.conn.prepareStatement(query);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("code_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static ObservableList<String> searchPartName(String searchText) {
        ObservableList<String> parts = FXCollections.observableArrayList();

        try {
            DatabaseConnection dc = new DatabaseConnection();

            String query = "SELECT DISTINCT part_name FROM tblparts WHERE part_name LIKE ?";
            PreparedStatement ps = dc.conn.prepareStatement(query);
            ps.setString(1, "%" + searchText + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                parts.add(rs.getString("part_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parts;
    }

    public static boolean saveRating(String username, int rating, String feedback) {
        try {
            DatabaseConnection db = new DatabaseConnection();

            String query = """
                UPDATE tblvehicle_owner
                SET rating = ?, feedback = ?
                WHERE username = ?
                """;

            PreparedStatement ps = db.conn.prepareStatement(query);
            ps.setInt(1, rating);
            ps.setString(2, feedback);
            ps.setString(3, username);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // Navigation Helpers
    // -------------------------------------------------------------------------

    public static void navigateToDashboard(ActionEvent event) {
        String role = SessionManager.getInstance().getUserRole();
        String fxml;

        switch (role) {
            case "admin":
                fxml = "javafx-ACAS-app-admin-dash.fxml";
                break;
            case "mechanic":
                fxml = "javafx-ACAS-app-dash.fxml";
                break;
            case "owner":
                fxml = "javafx-ACAS-app-customer-dash.fxml";
                break;
            default:
                SessionManager.getInstance().clearSession();
                fxml = "javafx-ACAS-app-login.fxml";
                break;
        }

        navigateTo(event, fxml);
    }

    public static void navigateTo(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(AppUtils.class.getResource(fxml))
            );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Logout() {
        try {
            SessionManager.clearSession();
            JavafxACASapp.changeScene("javafx-ACAS-app-view.fxml", 1100, 750);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}