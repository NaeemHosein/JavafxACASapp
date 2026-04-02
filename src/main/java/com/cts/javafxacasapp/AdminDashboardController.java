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

    @FXML
    private VBox cardManageRules;

    @FXML
    private VBox cardReviewFeedback;

    /**
     * Called after login to set up the dashboard for the logged-in user
     */
    public void initialize(String username, String role) {
        lblWelcome.setText("Welcome, " + username);

        // Show admin cards if user is admin
        if ("admin".equalsIgnoreCase(role)) {
            cardManageRules.setVisible(true);
            cardManageRules.setManaged(true);
            cardReviewFeedback.setVisible(true);
            cardReviewFeedback.setManaged(true);
        }

        // Set connection status
        lblStatus.setText("● Connected");
    }

    @FXML
    private void handleNewDiagnostic(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-diagnostic.fxml", 1100, 750);
        } catch (Exception e) {
            showError("Could not load diagnostic screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewReports(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-view-reports.fxml", 1100, 750);
        } catch (Exception e) {
            showError("Could not load reports screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePartCheck(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-part-compatibility.fxml", 1100, 750);
        } catch (Exception e) {
            showError("Could not load compatibility screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReportIssue(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-report-issue.fxml", 1100, 750);
        } catch (Exception e) {
            showError("Could not load issue report screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageRules(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("manage-rules.fxml", 1100, 750);
        } catch (Exception e) {
            showError("Could not load rules management screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReviewFeedback(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-view-issue-reports.fxml", 1100, 750);
        } catch (Exception e) {
            showError("Could not load feedback review screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-view.fxml", 1100, 750);
        } catch (Exception e) {
            showError("Could not return to login screen");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleViewIssueReports() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-view-issue-report", 1100, 750);
        } catch (Exception e) {
            showError("Could not load view issue reports screen");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSystemConfig() {
        try {
            JavafxACASapp.changeScene("", 1100, 750);
        } catch (Exception e) {
            showError("Could not return to login screen");
            e.printStackTrace();
        }
    }

    /**
     * Helper method to show error messages
     */
    private void showError(String message) {
        lblStatus.setText("● Error: " + message);
        lblStatus.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px;");
    }
}