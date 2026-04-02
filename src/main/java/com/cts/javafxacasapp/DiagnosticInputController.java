/**
 * Diagnostic Report form. Currently adds result to report table upon submission
 * TO DO: Generate PDF upon tapping generate report
 */

package com.cts.javafxacasapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.sql.PreparedStatement;

public class DiagnosticInputController {

    @FXML
    private Label lblUser;

    @FXML
    private Label lblRole;

    @FXML
    private Label lblStatus;

    @FXML
    private ComboBox<String> cmbMake;

    @FXML
    private ComboBox<String> cmbModel;

    @FXML
    private ComboBox<String> cmbYear;

    @FXML
    private ComboBox<String> cmbEngineType;

    @FXML
    private ComboBox<String> cmbDiagnosticCode;

    private final DatabaseConnection dc = new DatabaseConnection();

    /**
     * Initialize the form with data from database
     */
    @FXML
    public void initialize() {

        //populating combo boxes using methods in AppUtil
        AppUtils.loadMakes(cmbMake);
        AppUtils.loadYears(cmbYear);
        AppUtils.loadEngines(cmbEngineType);
        AppUtils.loadDiagnosticCodes(cmbDiagnosticCode);

        //adding listener to populate models based on make
        cmbMake.valueProperty().addListener((obs, oldMake, newMake) -> {
            if (newMake != null) {
                cmbModel.getItems().clear();
                AppUtils.loadModels(cmbModel, newMake);
            }
        });

        lblStatus.setText("Ready to diagnose");

        //getting username and role for display
        SessionManager session = SessionManager.getInstance();
        lblUser.setText("User: " + session.getUsername());
        lblRole.setText("Role: " + session.getUserRole());
    }

    /**
     * Storing report in report table for viewing in another Ui
     */
    @FXML
    private void handleGenerateDiagnosis() {

        // Validate inputs first
        if (!validateInputs()) {
            return;
        }

        try {

            String make = cmbMake.getValue();
            String model = cmbModel.getValue();
            int year = Integer.parseInt(cmbYear.getValue());
            String engine = cmbEngineType.getValue();
            String code = cmbDiagnosticCode.getValue();

            DatabaseConnection db = new DatabaseConnection();

            //grabbing IDs
            SessionManager session = SessionManager.getInstance();
            String username = session.getUsername();
            String role = session.getUserRole();

            int mechanicId = AppUtils.getUserId(username, role);
            int vehicleId = AppUtils.getVehicleId(db, make, model, engine, year);
            int codeId = AppUtils.getCodeId(code);

            // catching error in database return
            if (mechanicId == -1 || vehicleId == -1 || codeId == -1) {
                AppUtils.showError(lblStatus, "Error retrieving required data.");
                return;
            }

            lblStatus.setText("Saving diagnostic report...");

            //inserting into reports table
            String query = """
                INSERT INTO tbldiagnostic_reports
                (mechanic_id, code_id, vehicle_id, flag, report_date)
                VALUES (?, ?, ?, ?, NOW())
            """;

            PreparedStatement ps = db.conn.prepareStatement(query);

            ps.setInt(1, mechanicId);
            ps.setInt(2, codeId);
            ps.setInt(3, vehicleId);
            ps.setInt(4, 0);


            ps.executeUpdate();

            lblStatus.setText("Report generated successfully!Please tap View Reports to access your report.");

            // redirrecting to view reports screen
            JavafxACASapp.changeScene("javafx-ACAS-app-view-reports.fxml", 1100, 750);

        } catch (Exception e) {
            AppUtils.showError(lblStatus, "We encountered an error generating your report.");
            e.printStackTrace();
        }
    }

    /**
     * Validate form inputs
     */
    public boolean validateInputs() {

        if (cmbMake.getValue() == null) {
            AppUtils.showError(lblStatus,"Please select a vehicle make");
            return false;
        }
        if (cmbModel.getValue() == null) {
            AppUtils.showError(lblStatus,"Please select a vehicle model");
            return false;
        }
        if (cmbYear.getValue() == null) {
            AppUtils.showError(lblStatus,"Please select a year");
            return false;
        }
        if (cmbEngineType.getValue() == null) {
            AppUtils.showError(lblStatus,"Please select an engine type");
            return false;
        }
        if (cmbDiagnosticCode.getValue() == null) {
            AppUtils.showError(lblStatus,"Please select or enter a diagnostic code");
            return false;
        }
        if (!AppUtils.findDTC(cmbDiagnosticCode.getValue())) {
            AppUtils.showError(lblStatus,"Selected diagnostic code is not currently accessible ACAS' database");
            return false;
        }

        return true;
    }

    /**
     * Clear diagnostic code field
     */
    @FXML
    private void handleClearCode() {
        cmbDiagnosticCode.setValue(null);
        lblStatus.setText("Diagnostic code cleared");
    }

    /**
     * Reset the entire form
     */
    @FXML
    private void handleReset() {
        cmbMake.setValue(null);
        cmbModel.setValue(null);
        cmbYear.setValue(null);
        cmbEngineType.setValue(null);
        cmbDiagnosticCode.setValue(null);
        lblStatus.setText("Form reset - ready for new diagnosis");
    }

    /**
     * Go back to dashboard
     */
    @FXML
    private void handleBack(ActionEvent event) {
        AppUtils.navigateToDashboard(event);
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        lblStatus.setText("✓ " + message);
        lblStatus.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");
    }
}