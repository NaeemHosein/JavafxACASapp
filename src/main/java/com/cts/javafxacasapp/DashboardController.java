/**
 * Mechanic Dashboard- handles navigation to main mechanic use cases
 * Status label is updated with real time error catching and uses appUtil methods
 * TO DO: integrate session class so that username and role is stored and passed through
 * Update: added check part compatibility feature for mechanics as well
 */

package com.cts.javafxacasapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;


public class DashboardController {

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblStatus;



    public void initialize() {

        //attempting to initialize session
        SessionManager session = SessionManager.getInstance();

        lblWelcome.setText("Welcome, " + session.getUsername());



        // setting connection status
        lblStatus.setText(" Connected");
    }

    @FXML
    private void handleNewDiagnostic(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-diagnostic.fxml", 1100, 750);
        } catch (Exception e) {
            AppUtils.showError(lblStatus,"⚠ Error: Could not load diagnostic screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewReports(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-view-reports.fxml", 1100, 750);
        } catch (Exception e) {
            AppUtils.showError(lblStatus,"⚠ Error: Could not load reports screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePartCheck(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-part-compatibility.fxml", 1100, 750);
        } catch (Exception e) {
            AppUtils.showError(lblStatus, "⚠ Error: Could not load compatibility screen");
            e.printStackTrace();
        }
    }


    @FXML
    private void handleLogout() {
        AppUtils.Logout();
    }


}