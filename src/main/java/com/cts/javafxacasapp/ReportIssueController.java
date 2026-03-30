package com.cts.javafxacasapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportIssueController {

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblError;

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

    @FXML
    private ComboBox<String> cmbIssueType;

    @FXML
    private TextField txtIncorrectPart;

    @FXML
    private TextField txtCorrectSolution;

    @FXML
    private TextArea txtFeedback;

    private final DatabaseConnection dc = new DatabaseConnection();

    /**
     * Initialize the form - Using AppUtils for reusable methods
     */
    @FXML
    public void initialize() {
        // Using AppUtils methods for loading dropdowns (showing modularity)
        AppUtils.loadMakes(cmbMake);
        AppUtils.loadYears(cmbYear);
        AppUtils.loadEngines(cmbEngineType);

        // Load other data
        loadDiagnosticCodes();
        loadIssueTypes();

        lblStatus.setText("Ready to submit feedback");
    }

    /**
     * Load diagnostic codes from database
     */
    private void loadDiagnosticCodes() {
        try {
            ObservableList<String> codes = FXCollections.observableArrayList();
            String query = "SELECT code, description FROM tbldiagnostic_codes ORDER BY code";
            dc.rst = dc.stat.executeQuery(query);

            while (dc.rst.next()) {
                int code = dc.rst.getInt("code");
                String desc = dc.rst.getString("description");
                codes.add("P0" + code + " - " + desc);
            }

            cmbDiagnosticCode.setItems(codes);

        } catch (SQLException e) {
            AppUtils.showError(lblError, "Error loading diagnostic codes");
            e.printStackTrace();
        }
    }

    /**
     * Load issue types
     */
    private void loadIssueTypes() {
        ObservableList<String> issueTypes = FXCollections.observableArrayList();
        issueTypes.addAll(
                "Incorrect Part Recommendation",
                "Part Does Not Fit Vehicle",
                "Wrong Diagnostic Code Match",
                "Missing Compatibility Information",
                "Outdated Part Information",
                "System Did Not Find Solution",
                "Other"
        );
        cmbIssueType.setItems(issueTypes);
    }

    /**
     * Handle make selection - Using AppUtils method
     */
    @FXML
    private void handleMakeSelected() {
        String selectedMake = cmbMake.getValue();
        if (selectedMake != null) {
            // Using AppUtils to load models based on selected make
            AppUtils.loadModels(cmbModel, selectedMake);
        }
    }

    /**
     * Handle submit report
     */
    @FXML
    private void handleSubmitReport() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        try {
            // Get all input values
            String make = cmbMake.getValue();
            String model = cmbModel.getValue();
            int year = Integer.parseInt(cmbYear.getValue());
            String engine = cmbEngineType.getValue();
            String codeText = cmbDiagnosticCode.getValue();
            String issueType = cmbIssueType.getValue();
            String incorrectPart = txtIncorrectPart.getText().trim();
            String correctSolution = txtCorrectSolution.getText().trim();
            String feedback = txtFeedback.getText().trim();

            // Extract just the code number
            int codeId = extractCodeId(codeText);

            // Get or create vehicle ID using AppUtils
            int vehicleId = AppUtils.getVehicleId(dc, make, model, engine, year);

            if (vehicleId == -1) {
                AppUtils.showError(lblError, "Could not find or create vehicle record");
                return;
            }

            lblStatus.setText("Submitting report...");

            // Insert feedback into database
            String insertQuery = """
                INSERT INTO tblfeedback 
                (mechanic_id, vehicle_id, code_id, issue_type, incorrect_recommendation, 
                 correct_solution, detailed_feedback, report_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 'pending')
            """;

            PreparedStatement pst = dc.conn.prepareStatement(insertQuery);
            pst.setInt(1, 1); // TODO: Get actual logged-in mechanic ID
            pst.setInt(2, vehicleId);
            pst.setInt(3, codeId);
            pst.setString(4, issueType);
            pst.setString(5, incorrectPart.isEmpty() ? null : incorrectPart);
            pst.setString(6, correctSolution.isEmpty() ? null : correctSolution);
            pst.setString(7, feedback);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                // Success!
                AppUtils.showAlert("Report Submitted",
                        "Thank you! Your feedback has been submitted successfully.\n\n" +
                                "Our team will review it and update the system accordingly.");

                lblStatus.setText("Report submitted successfully!");
                handleClearForm();
            } else {
                AppUtils.showError(lblError, "Failed to submit report");
            }

        } catch (SQLException e) {
            AppUtils.showError(lblError, "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AppUtils.showError(lblError, "Error submitting report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extract code ID from combo box text
     */
    private int extractCodeId(String codeText) throws SQLException {
        // Extract code number from "P0300 - Description" format
        String codeStr = codeText.split(" - ")[0].replace("P0", "");
        int code = Integer.parseInt(codeStr);

        // Get code_id from database
        String query = "SELECT code_id FROM tbldiagnostic_codes WHERE code = ?";
        PreparedStatement pst = dc.conn.prepareStatement(query);
        pst.setInt(1, code);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getInt("code_id");
        }

        return -1;
    }

    /**
     * Validate form inputs
     */
    private boolean validateInputs() {
        AppUtils.hideError(lblError);

        if (cmbMake.getValue() == null) {
            AppUtils.showError(lblError, "Please select a vehicle make");
            return false;
        }
        if (cmbModel.getValue() == null) {
            AppUtils.showError(lblError, "Please select a vehicle model");
            return false;
        }
        if (cmbYear.getValue() == null) {
            AppUtils.showError(lblError, "Please select a year");
            return false;
        }
        if (cmbEngineType.getValue() == null) {
            AppUtils.showError(lblError, "Please select an engine type");
            return false;
        }
        if (cmbDiagnosticCode.getValue() == null || cmbDiagnosticCode.getValue().isEmpty()) {
            AppUtils.showError(lblError, "Please select a diagnostic code");
            return false;
        }
        if (cmbIssueType.getValue() == null) {
            AppUtils.showError(lblError, "Please select an issue type");
            return false;
        }
        if (txtFeedback.getText().trim().isEmpty()) {
            AppUtils.showError(lblError, "Please provide detailed feedback");
            return false;
        }
        if (txtFeedback.getText().trim().length() < 20) {
            AppUtils.showError(lblError, "Feedback must be at least 20 characters");
            return false;
        }

        return true;
    }

    /**
     * Clear the form
     */
    @FXML
    private void handleClearForm() {
        cmbMake.setValue(null);
        cmbModel.setValue(null);
        cmbModel.getItems().clear();
        cmbYear.setValue(null);
        cmbEngineType.setValue(null);
        cmbDiagnosticCode.setValue(null);
        cmbIssueType.setValue(null);
        txtIncorrectPart.clear();
        txtCorrectSolution.clear();
        txtFeedback.clear();

        AppUtils.hideError(lblError);
        lblStatus.setText("Form cleared - ready for new report");
    }

    /**
     * Go back to dashboard
     */
    @FXML
    private void handleBack() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-dash.fxml", 1100, 750);
        } catch (Exception e) {
            AppUtils.showError(lblError, "Could not return to dashboard");
            e.printStackTrace();
        }
    }
}