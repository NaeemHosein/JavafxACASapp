package com.cts.javafxacasapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class AdminDashboardController {

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblStatus;


    //setting up dashboard
    public void initialize(String username, String role) {

        //getting username for display
        SessionManager session = SessionManager.getInstance();

        lblWelcome.setText("Welcome, " + username);


        // Set connection status
        lblStatus.setText("● Connected");
    }


    @FXML
    private void handleLogout() {
        AppUtils.Logout();
    }

    @FXML
    private void handleViewIssueReports() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-view-issue-report.fxml", 1100, 750);
        } catch (Exception e) {
            AppUtils.showError(lblStatus, "Could not load view issue reports screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSystemConfig() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-system-config.fxml", 1100, 750);
        } catch (Exception e) {
            AppUtils.showError(lblStatus, "Could not load System Configuration Screen");
            e.printStackTrace();
        }
    }
}

