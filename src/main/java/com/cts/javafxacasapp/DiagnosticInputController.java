package com.cts.javafxacasapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.sql.SQLException;


public class DiagnosticInputController {

    @FXML
    private Label lblUser;

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
    private ComboBox<String> cmbSymptom;

    @FXML
    private ComboBox<String> cmbDiagnosticCode;

    @FXML
    private TextArea txtNotes;

    private final DatabaseConnection dc = new DatabaseConnection();

    /**
     * Initialize the form with data from database
     */
    @FXML
    public void initialize() {
        loadVehicleMakes();
        loadSymptoms();
        loadDiagnosticCodes();
        loadYears();
        loadEngineTypes();

        lblStatus.setText("Ready to diagnose");
    }

    /**
     * Load vehicle makes
     */
    private void loadVehicleMakes() {
        ObservableList<String> makes = FXCollections.observableArrayList();
        makes.addAll("Toyota", "Honda", "Ford", "Chevrolet", "Nissan", "BMW",
                "Mercedes-Benz", "Volkswagen", "Audi", "Hyundai", "Kia",
                "Mazda", "Subaru", "Jeep", "Ram", "GMC");
        cmbMake.setItems(makes);
    }

    /**
     * Load vehicle models
     */
    private void loadModels() {
        ObservableList<String> models = FXCollections.observableArrayList();
        models.addAll("Camry", "Civic", "F-150", "Silverado", "Corolla", "Accord",
                "CR-V", "RAV4", "Tacoma", "Mustang", "Altima", "Rogue");
        cmbModel.setItems(models);
    }

    /**
     * Load years (past 20 years)
     */
    private void loadYears() {
        ObservableList<String> years = FXCollections.observableArrayList();
        int currentYear = java.time.Year.now().getValue();
        for (int year = currentYear; year >= currentYear - 20; year--) {
            years.add(String.valueOf(year));
        }
        cmbYear.setItems(years);
    }

    /**
     * Load engine types
     */
    private void loadEngineTypes() {
        ObservableList<String> engines = FXCollections.observableArrayList();
        engines.addAll(
                "4-Cylinder",
                "V6",
                "V8",
                "Turbo 4-Cylinder",
                "Hybrid",
                "Electric",
                "Diesel",
                "1.5L 4-Cylinder",
                "2.0L 4-Cylinder",
                "2.5L 4-Cylinder",
                "3.5L V6",
                "5.0L V8"
        );
        cmbEngineType.setItems(engines);
    }

    /**
     * Load symptoms from database
     */
    private void loadSymptoms() {
        try {
            ObservableList<String> symptoms = FXCollections.observableArrayList();
            String query = "SELECT symptom_name FROM tblsymptoms ORDER BY symptom_name";
            dc.rst = dc.stat.executeQuery(query);

            while (dc.rst.next()) {
                symptoms.add(dc.rst.getString("symptom_name"));
            }

            cmbSymptom.setItems(symptoms);
            lblStatus.setText("Loaded " + symptoms.size() + " symptoms from database");

        } catch (SQLException e) {
            showError("Error loading symptoms: " + e.getMessage());
            e.printStackTrace();
        }
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
                String code = dc.rst.getString("code");
                String desc = dc.rst.getString("description");
                codes.add(code + " - " + desc);
            }

            cmbDiagnosticCode.setItems(codes);

        } catch (SQLException e) {
            showError("Error loading diagnostic codes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle make selection change to load related models
     */
    @FXML
    private void handleMakeSelected() {
        if (cmbMake.getValue() != null) {
            loadModels();
        }
    }

    /**
     * Generate diagnosis based on inputs
     * TODO: GROUP MEMBER TO IMPLEMENT DIAGNOSTIC MATCHING LOGIC
     */
    @FXML
    private void handleGenerateDiagnosis() {
        // Validate inputs first
        if (!validateInputs()) {
            return;
        }

        try {
            // Get input values
            String make = cmbMake.getValue();
            String model = cmbModel.getValue();
            int year = Integer.parseInt(cmbYear.getValue());
            String engine = cmbEngineType.getValue();
            String symptom = cmbSymptom.getValue();
            String codeText = cmbDiagnosticCode.getValue();
            String notes = txtNotes.getText();

            // Extract just the code (e.g., "P0300" from "P0300 - Description")
            String code = codeText != null ? codeText.split(" - ")[0] : null;

            lblStatus.setText("Searching for matching diagnostic rules...");

            // ============================================================
            // TODO: GROUP MEMBER IMPLEMENTATION REQUIRED
            // ============================================================
            // 1. Build SQL query to search tbldiagnostic_rules table
            // 2. Query should match on:
            //    - symptom (from tblsymptoms)
            //    - code (from tbldiagnostic_codes) if provided
            //    - vehicle make, model, year range, engine type
            // 3. Join with tblparts to get part recommendation
            // 4. If match found:
            //    - Save report to tbldiagnostic_reports table
            //    - Show success message
            //    - Optional: Navigate to results screen
            // 5. If no match found:
            //    - Show "No recommendations found" message
            // ============================================================

            // PLACEHOLDER - Remove this after implementing logic above
            showWarning("Diagnostic logic not yet implemented. Group member needs to add this.");

        } catch (Exception e) {
            showError("Error generating diagnosis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validate form inputs
     */
    private boolean validateInputs() {
        if (cmbMake.getValue() == null) {
            showError("Please select a vehicle make");
            return false;
        }
        if (cmbModel.getValue() == null) {
            showError("Please select a vehicle model");
            return false;
        }
        if (cmbYear.getValue() == null) {
            showError("Please select a year");
            return false;
        }
        if (cmbEngineType.getValue() == null) {
            showError("Please select an engine type");
            return false;
        }
        if (cmbSymptom.getValue() == null) {
            showError("Please select a symptom");
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
        cmbSymptom.setValue(null);
        cmbDiagnosticCode.setValue(null);
        txtNotes.clear();
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
     * Show error message
     */
    private void showError(String message) {
        lblStatus.setText("❌ " + message);
        lblStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        lblStatus.setText("✓ " + message);
        lblStatus.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");
    }

    /**
     * Show warning message
     */
    private void showWarning(String message) {
        lblStatus.setText("⚠ " + message);
        lblStatus.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 12px;");
    }
}