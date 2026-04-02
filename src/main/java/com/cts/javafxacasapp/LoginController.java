/**
 * Login page controller - currently just checks to see if a row exists in the
 * relevant table with inputted username and password.
 * NEED TO ADD: sessions to store IDs
 * Update: migrated showError() and hideError() to AppUtils for use in other easy use in all controllers
 */

package com.cts.javafxacasapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button registerMechanicButton;
    @FXML private Button registerCustomerButton;
    @FXML private ToggleButton mechanicBtn;
    @FXML private ToggleButton adminBtn;
    @FXML private ToggleButton guestBtn;

    @FXML private ToggleGroup roleToggleGroup;


    private static final String DEFAULT_STYLE =
            "-fx-background-color: #1a2235; -fx-border-color: #2a3650; " +
                    "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; " +
                    "-fx-text-fill: #a0a8b8; -fx-font-size: 13px; -fx-cursor: hand;";

    private static final String SELECTED_STYLE =
            "-fx-background-color: #1a2235; -fx-border-color: #cc2200; " +
                    "-fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; " +
                    "-fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-cursor: hand;";
    @FXML
    public void initialize() {
        mechanicBtn.setStyle(DEFAULT_STYLE);
        adminBtn.setStyle(SELECTED_STYLE);   // Admin selected by default
        guestBtn.setStyle(DEFAULT_STYLE);

        // Listen for toggle changes
        roleToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            mechanicBtn.setStyle(DEFAULT_STYLE);
            adminBtn.setStyle(DEFAULT_STYLE);
            guestBtn.setStyle(DEFAULT_STYLE);

            if (newToggle != null) {
                ((ToggleButton) newToggle).setStyle(SELECTED_STYLE);
            }
        });

    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AppUtils.showError(errorLabel,"Please enter username and password.");
            return;
        }
        Toggle selectedToggle = roleToggleGroup.getSelectedToggle();
        if (selectedToggle == null) {
            AppUtils.showError(errorLabel,"Please select a role.");
            return;
        }

        String tableName;

        if (selectedToggle == adminBtn) {
            tableName = "tbladministrator";
        }
        else if (selectedToggle == mechanicBtn) {
            tableName = "tblmechanic";
        }
        else if (selectedToggle == guestBtn) {
            tableName = "tblvehicle_owner";
        }
        else {
            AppUtils.showError(errorLabel,"Invalid role selected.");
            return;
        }

        try {
            DatabaseConnection dc = new DatabaseConnection();
            String query = "SELECT * FROM " + tableName + " WHERE username = ? AND password = ?";
            PreparedStatement ps = dc.conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AppUtils.hideError(errorLabel);
                System.out.println("Login successful");

                if (selectedToggle == adminBtn) {
                    SessionManager.getInstance().setUserRole("admin");
                    SessionManager.getInstance().setUsername(username);
                    JavafxACASapp.changeScene("javafx-ACAS-app-admin-dash.fxml", 1100, 750);
                }
                else if (selectedToggle == mechanicBtn) {
                    SessionManager.getInstance().setUserRole("mechanic");
                    SessionManager.getInstance().setUsername(username);
                    JavafxACASapp.changeScene("javafx-ACAS-app-dash.fxml", 1100, 750);
                }
                else if (selectedToggle == guestBtn) {
                    SessionManager.getInstance().setUserRole("customer");
                    SessionManager.getInstance().setUsername(username);
                    JavafxACASapp.changeScene("javafx-ACAS-app-customer-dash.fxml", 1100, 750);
                }

            } else {
                AppUtils.showError(errorLabel,"Invalid username or password.");
            }

        } catch (SQLException e) {
            AppUtils.showError(errorLabel,"Database error. Please try again.");
            e.printStackTrace();
        } catch (IOException e) {
            AppUtils.showError(errorLabel,"Failed to load next screen.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterMechanic() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-mechanic-register.fxml", 1100, 750);
        } catch (IOException e) {
            AppUtils.showError(errorLabel,"Failed to load registration screen.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterCustomer() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-customer-register.fxml", 1100, 750);
        } catch (IOException e) {
            AppUtils.showError(errorLabel,"Failed to load registration screen.");
            e.printStackTrace();
        }
    }

}