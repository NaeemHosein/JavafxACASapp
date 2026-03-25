package com.cts.javafxacasapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ToggleButton mechanicBtn;
    @FXML private ToggleButton adminBtn;
    @FXML private ToggleButton guestBtn;
    @FXML private ToggleGroup roleToggleGroup;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    // Styles for role buttons
    private static final String STYLE_UNSELECTED =
            "-fx-background-color: #1a2235;" +
                    "-fx-border-color: #2a3650;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-text-fill: #a0a8b8;" +
                    "-fx-font-size: 13px;" +
                    "-fx-cursor: hand;";

    private static final String STYLE_SELECTED =
            "-fx-background-color: transparent;" +
                    "-fx-border-color: #cc2200;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-size: 13px;" +
                    "-fx-cursor: hand;";

    @FXML
    public void initialize() {
        // Ensure at least one role always selected
        roleToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
            } else {
                updateRoleStyles();
            }
        });
        // Set initial styles
        updateRoleStyles();

    }

    private void updateRoleStyles() {
        Toggle selected = roleToggleGroup.getSelectedToggle();
        mechanicBtn.setStyle(selected == mechanicBtn ? STYLE_SELECTED : STYLE_UNSELECTED);
        adminBtn.setStyle(selected == adminBtn ? STYLE_SELECTED : STYLE_UNSELECTED);
        guestBtn.setStyle(selected == guestBtn ? STYLE_SELECTED : STYLE_UNSELECTED);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = getSelectedRole();
        String table = getTable();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        try {
            DatabaseConnection dc = new DatabaseConnection();
            getSelectedRole();
            String query = "SELECT * FROM" + table + "WHERE username = '" + username +
                    "' AND password = '" + password + "'";
            dc.rst = dc.stat.executeQuery(query);

            if (dc.rst.next()) {
                hideError();
                System.out.println("Login successful: Welcome" + username + " | You are logged in as a " + role);

                switch (role) {
                    case "Admin":
                        JavafxACASapp.changeScene("admin-dashboard-view.fxml", 1100, 750);
                        break;
                    case "Mechanic":
                        JavafxACASapp.changeScene("javafx-ACAS-app-dash.fxml", 1100, 750);
                        break;
                    case "Guest":
                        JavafxACASapp.changeScene("owner-verification-view.fxml", 1100, 750);
                        break;
                }
            } else {
                showError("Invalid username, password, or role.");
            }

        } catch (SQLException e) {
            showError("Database error. Please try again.");
            e.printStackTrace();
        } catch (IOException e) {
            showError("Failed to load next screen.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-register.fxml", 1100, 750);
        } catch (IOException e) {
            showError("Failed to load registration screen.");
            e.printStackTrace();
        }
    }

    private String getSelectedRole() {
        Toggle selected = roleToggleGroup.getSelectedToggle();
        if (selected == mechanicBtn) return "Mechanic";
        if (selected == adminBtn)    return "Admin";
        if (selected == guestBtn)    return "Guest";
        return "Guest";
    }

    private String getTable() {
        Toggle selected = roleToggleGroup.getSelectedToggle();
        if (selected == mechanicBtn) return "tblmechanic";
        if (selected == adminBtn)    return "tbladministrator";
        if (selected == guestBtn)    return "tblvehicle_owner";
        return "Guest";
    }



    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}