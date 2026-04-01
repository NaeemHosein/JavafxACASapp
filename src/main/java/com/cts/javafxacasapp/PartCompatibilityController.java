package com.cts.javafxacasapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;
import java.sql.SQLException;

public class PartCompatibilityController {


    @FXML
    private Label lblStatus;

    @FXML
    private Label lblResultCount;

    @FXML
    private Circle statusIndicator;

    @FXML
    private ComboBox<String> cmbMake;

    @FXML
    private ComboBox<String> cmbModel;

    @FXML
    private ComboBox<String> cmbYear;

    @FXML
    private ComboBox<String> cmbEngineType;

    @FXML
    private ComboBox<String> cmbPartType;

    @FXML
    private TextField txtPartSearch;

    @FXML
    private VBox resultsSection;

    @FXML
    private TableView<PartResult> tblResults;

    @FXML
    private TableColumn<PartResult, String> colPartName;

    @FXML
    private TableColumn<PartResult, String> colPartType;

    @FXML
    private TableColumn<PartResult, String> colCompatible;

    @FXML
    private TableColumn<PartResult, String> colYearRange;

    @FXML
    private TableColumn<PartResult, String> colEngineType;

    private final DatabaseConnection dc = new DatabaseConnection();

    /**
     * Initialize the form
     */
    @FXML
    public void initialize() {
        loadVehicleMakes();
        loadYears();
        loadEngineTypes();
        loadPartTypes();
        setupTableColumns();

        lblStatus.setText("Enter vehicle details to check compatibility");
    }

    /**
     * Load vehicle makes
     */
    private void loadVehicleMakes() {
        ObservableList<String> makes = FXCollections.observableArrayList();
        makes.addAll("Toyota", "Honda", "Ford", "Chevrolet", "Nissan", "BMW",
                "Mercedes-Benz", "Volkswagen", "Audi", "Hyundai", "Kia",
                "Mazda", "Subaru", "Jeep", "Ram", "GMC", "Suzuki");
        cmbMake.setItems(makes);
    }

    /**
     * Load vehicle models
     */
    private void loadModels() {
        ObservableList<String> models = FXCollections.observableArrayList();
        models.addAll("Camry", "Civic", "F-150", "Silverado", "Corolla", "Accord",
                "CR-V", "RAV4", "Tacoma", "Mustang", "Altima", "Rogue",
                "Hilux", "Land Cruiser Prado", "X-Trail", "Fit", "Swift",
                "Vitara", "BT-50", "3", "CX-5", "Elantra", "Sportage");
        cmbModel.setItems(models);
    }

    /**
     * Load years
     */
    private void loadYears() {
        ObservableList<String> years = FXCollections.observableArrayList();
        int currentYear = java.time.Year.now().getValue();
        for (int year = currentYear; year >= currentYear - 30; year--) {
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
                "1.3L I4", "1.5L I4", "1.6L I4", "1.8L I4", "2.0L I4", "2.4L I4", "2.5L I4",
                "2.7L I4", "3.0L Diesel", "2.5L Diesel", "3.5L V6", "4.0L V6", "5.0L V8",
                "5.3L V8", "5.4L V8", "V6", "V8", "4-Cylinder", "Hybrid", "Electric", "ANY"
        );
        cmbEngineType.setItems(engines);
    }

    /**
     * Load part types from database
     */
    private void loadPartTypes() {
        try {
            ObservableList<String> partTypes = FXCollections.observableArrayList();
            String query = "SELECT DISTINCT part_type FROM tblparts ORDER BY part_type";
            dc.rst = dc.stat.executeQuery(query);

            while (dc.rst.next()) {
                partTypes.add(dc.rst.getString("part_type"));
            }

            cmbPartType.setItems(partTypes);

        } catch (SQLException e) {
            showError("Error loading part types: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colPartName.setCellValueFactory(data -> data.getValue().partNameProperty());
        colPartType.setCellValueFactory(data -> data.getValue().partTypeProperty());
        colCompatible.setCellValueFactory(data -> data.getValue().compatibleProperty());
        colYearRange.setCellValueFactory(data -> data.getValue().yearRangeProperty());
        colEngineType.setCellValueFactory(data -> data.getValue().engineTypeProperty());
    }

    /**
     * Handle vehicle field change
     */
    @FXML
    private void handleVehicleFieldChange() {
        // Load models when make is selected
        if (cmbMake.getValue() != null && cmbModel.getItems().isEmpty()) {
            loadModels();
        }
    }

    /**
     * Handle Search Parts button
     */
    @FXML
    private void handleSearchParts() {
        String searchText = txtPartSearch.getText().trim();

        if (searchText.isEmpty()) {
            showError("Please enter a part name or number to search");
            return;
        }

        if (!validateVehicleInputs()) {
            return;
        }

        try {
            String make = cmbMake.getValue();
            String model = cmbModel.getValue();
            int year = Integer.parseInt(cmbYear.getValue());
            String engine = cmbEngineType.getValue();

            lblStatus.setText("Searching for: " + searchText + "...");

            // Query for parts matching search text
            String query = "SELECT part_name, part_type, engine_type, year_min, year_max " +
                    "FROM tblparts " +
                    "WHERE (part_name LIKE '%" + searchText + "%') " +
                    "AND (compatible_make = '" + make + "' OR compatible_make = 'ANY') " +
                    "AND (compatible_model = '" + model + "' OR compatible_model = 'ANY') " +
                    "AND " + year + " BETWEEN year_min AND year_max " +
                    "AND (engine_type = '" + engine + "' OR engine_type = 'ANY')";

            dc.rst = dc.stat.executeQuery(query);

            ObservableList<PartResult> results = FXCollections.observableArrayList();

            while (dc.rst.next()) {
                String partName = dc.rst.getString("part_name");
                String partType = dc.rst.getString("part_type");
                String engineType = dc.rst.getString("engine_type");
                int yearMin = dc.rst.getInt("year_min");
                int yearMax = dc.rst.getInt("year_max");

                results.add(new PartResult(
                        partName,
                        partType,
                        "✓ Compatible",
                        yearMin + " - " + yearMax,
                        engineType
                ));
            }

            displayResults(results, "Search: " + searchText);

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError("Error searching parts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Browse by Type button
     */
    @FXML
    private void handleBrowseByType() {
        if (!validateVehicleInputs()) {
            return;
        }

        if (cmbPartType.getValue() == null) {
            showError("Please select a part type first");
            return;
        }

        try {
            String make = cmbMake.getValue();
            String model = cmbModel.getValue();
            int year = Integer.parseInt(cmbYear.getValue());
            String engine = cmbEngineType.getValue();
            String partType = cmbPartType.getValue();

            lblStatus.setText("Checking compatibility for " + partType + "...");

            // Query for compatible parts of selected type
            String query = "SELECT part_name, part_type, engine_type, year_min, year_max " +
                    "FROM tblparts " +
                    "WHERE part_type = '" + partType + "' " +
                    "AND (compatible_make = '" + make + "' OR compatible_make = 'ANY') " +
                    "AND (compatible_model = '" + model + "' OR compatible_model = 'ANY') " +
                    "AND " + year + " BETWEEN year_min AND year_max " +
                    "AND (engine_type = '" + engine + "' OR engine_type = 'ANY')";

            dc.rst = dc.stat.executeQuery(query);

            ObservableList<PartResult> results = FXCollections.observableArrayList();

            while (dc.rst.next()) {
                String partName = dc.rst.getString("part_name");
                String type = dc.rst.getString("part_type");
                String engineType = dc.rst.getString("engine_type");
                int yearMin = dc.rst.getInt("year_min");
                int yearMax = dc.rst.getInt("year_max");

                results.add(new PartResult(
                        partName,
                        type,
                        "✓ Compatible",
                        yearMin + " - " + yearMax,
                        engineType
                ));
            }

            displayResults(results, partType);

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError("Error browsing parts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Show All Parts button
     */
    @FXML
    private void handleShowAllParts() {
        if (!validateVehicleInputs()) {
            return;
        }

        try {
            String make = cmbMake.getValue();
            String model = cmbModel.getValue();
            int year = Integer.parseInt(cmbYear.getValue());
            String engine = cmbEngineType.getValue();

            lblStatus.setText("Finding all compatible parts...");

            // Query for ALL compatible parts
            String query = "SELECT part_name, part_type, engine_type, year_min, year_max " +
                    "FROM tblparts " +
                    "WHERE (compatible_make = '" + make + "' OR compatible_make = 'ANY') " +
                    "AND (compatible_model = '" + model + "' OR compatible_model = 'ANY') " +
                    "AND " + year + " BETWEEN year_min AND year_max " +
                    "AND (engine_type = '" + engine + "' OR engine_type = 'ANY') " +
                    "ORDER BY part_type, part_name";

            dc.rst = dc.stat.executeQuery(query);

            ObservableList<PartResult> results = FXCollections.observableArrayList();

            while (dc.rst.next()) {
                String partName = dc.rst.getString("part_name");
                String partType = dc.rst.getString("part_type");
                String engineType = dc.rst.getString("engine_type");
                int yearMin = dc.rst.getInt("year_min");
                int yearMax = dc.rst.getInt("year_max");

                results.add(new PartResult(
                        partName,
                        partType,
                        "✓ Compatible",
                        yearMin + " - " + yearMax,
                        engineType
                ));
            }

            displayResults(results, "All compatible parts");

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError("Error loading parts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Display results in table
     */
    private void displayResults(ObservableList<PartResult> results, String searchType) {
        if (results.isEmpty()) {
            showWarning("No compatible parts found for " + searchType);
            resultsSection.setVisible(false);
            resultsSection.setManaged(false);
        } else {
            tblResults.setItems(results);
            lblResultCount.setText(results.size() + " compatible part(s) found");
            resultsSection.setVisible(true);
            resultsSection.setManaged(true);
            showSuccess("Found " + results.size() + " compatible part(s)");
        }
    }

    /**
     * Clear results
     */
    @FXML
    private void handleClearResults() {
        tblResults.getItems().clear();
        resultsSection.setVisible(false);
        resultsSection.setManaged(false);
        lblStatus.setText("Results cleared - ready for new search");
        statusIndicator.setStyle("-fx-fill: #10b981;");
    }

    /**
     * Validate vehicle inputs
     */
    private boolean validateVehicleInputs() {
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
        return true;
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
        statusIndicator.setStyle("-fx-fill: #ef4444;");
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        lblStatus.setText("✓ " + message);
        lblStatus.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");
        statusIndicator.setStyle("-fx-fill: #10b981;");
    }

    /**
     * Show warning message
     */
    private void showWarning(String message) {
        lblStatus.setText("⚠ " + message);
        lblStatus.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 12px;");
        statusIndicator.setStyle("-fx-fill: #f59e0b;");
    }

    /**
     * Inner class for table results
     */
    public static class PartResult {
        private final SimpleStringProperty partName;
        private final SimpleStringProperty partType;
        private final SimpleStringProperty compatible;
        private final SimpleStringProperty yearRange;
        private final SimpleStringProperty engineType;

        public PartResult(String partName, String partType, String compatible,
                          String yearRange, String engineType) {
            this.partName = new SimpleStringProperty(partName);
            this.partType = new SimpleStringProperty(partType);
            this.compatible = new SimpleStringProperty(compatible);
            this.yearRange = new SimpleStringProperty(yearRange);
            this.engineType = new SimpleStringProperty(engineType);
        }

        public SimpleStringProperty partNameProperty() { return partName; }
        public SimpleStringProperty partTypeProperty() { return partType; }
        public SimpleStringProperty compatibleProperty() { return compatible; }
        public SimpleStringProperty yearRangeProperty() { return yearRange; }
        public SimpleStringProperty engineTypeProperty() { return engineType; }
    }
}