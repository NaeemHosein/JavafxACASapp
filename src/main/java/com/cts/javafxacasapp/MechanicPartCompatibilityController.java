/*
* Tabling this until all other features are finished as mechanics checking part compatibility
*  wasn't in the doc
* Mechanics can check part's compatibility with an inputted vehicle.
* Search feature added so words similar to what is typed in part seacrh box is listed
* TODO: List only parts compatible with vehicles
* Fix UI on hidden results section so it shows better
* */

package com.cts.javafxacasapp;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class MechanicPartCompatibilityController {


    @FXML
    private Label lblStatus;

    @FXML
    private Label lblUser;

    @FXML
    private Label lblRole;


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
    private ComboBox<String> cmbPartName;


    @FXML
    private VBox resultsSection;

    @FXML
    private Label lblCompatibilityResult;


    private final DatabaseConnection dc = new DatabaseConnection();

  // populating combo boxes using AppUtil
    @FXML
    public void initialize() {
        String make = cmbMake.getValue();

        AppUtils.loadMakes(cmbMake);
        AppUtils.loadModels(cmbModel,"make");
        AppUtils.loadYears(cmbYear);
        AppUtils.loadEngines(cmbEngineType);
        AppUtils.loadPartNames(cmbPartName);

        //adding listener to populate models based on make

        cmbMake.valueProperty().addListener((obs, oldMake, newMake) -> {
            if (newMake != null) {
                cmbModel.getItems().clear();
                AppUtils.loadModels(cmbModel, newMake);
            }
        });




        lblStatus.setText("Enter vehicle details to check compatibility");

        //getting username and role for display
        SessionManager session = SessionManager.getInstance();

        lblUser.setText("User: " + session.getUsername());
        lblRole.setText("Role: " + session.getUserRole());

        //attempting to get label to display multiple lines
        lblCompatibilityResult.setWrapText(true);

    }






    // attempting to use this button to generate list of parts in system that match search for selection
    @FXML
    private void handleSearchParts() {
        String searchText = cmbPartName.getEditor().getText();

        if (searchText == null || searchText.isEmpty()) {
            AppUtils.showError(lblStatus, "Please enter a part name");
            return;
        }

        ObservableList<String> matches = AppUtils.searchPartName(searchText); // updated to call method in AppUtils

        if (matches.isEmpty()) {
            AppUtils.showError(lblStatus, "No matching parts found. Please refine search.");
            cmbPartName.getItems().clear();
        } else {
            cmbPartName.setItems(matches);
            cmbPartName.show(); // this opens dropdown automatically
            lblStatus.setText(matches.size() + " match(es) found");

        }
    }

    // defining compatibility button to check part table and compare compatibility
    @FXML
    private void handleGetCompatibilityStatus() {
        if (!validateVehicleInputs()) return;

        try {
            String partName = cmbPartName.getValue();
            String make = cmbMake.getValue();
            String model = cmbModel.getValue();
            int year = Integer.parseInt(cmbYear.getValue());
            String engine = cmbEngineType.getValue();

            String partQuery = "SELECT * FROM tblparts WHERE part_name = ?";
            PreparedStatement ps = dc.conn.prepareStatement(partQuery);
            ps.setString(1, partName);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                AppUtils.showError(lblStatus,"Part Not Found: Please tap the Search Part button and select a part. ");
                return;
            }

            String partType = rs.getString("part_type");
            String compMake = rs.getString("compatible_make");
            String compModel = rs.getString("compatible_model");
            String compEngine = rs.getString("engine_type");
            int yearMin = rs.getInt("year_min");
            int yearMax = rs.getInt("year_max");

            boolean isCompatible =
                    (compMake.equals("ANY") || compMake.equalsIgnoreCase(make)) &&
                            (compModel.equals("ANY") || compModel.equalsIgnoreCase(model)) &&
                            (compEngine.equals("ANY") || compEngine.equalsIgnoreCase(engine)) &&
                            (year >= yearMin && year <= yearMax);

            if (isCompatible) {
                lblCompatibilityResult.setText(
                        "✅ This Part is Compatible with your Vehicle!\n\n" +
                                "Part Compatibility Information:\n"+
                                "Make: " + compMake+
                                "\t Model: " + compModel +
                                "\n Engine: " + compEngine +
                                "\t Years: " + yearMin + "-" + yearMax
                );
            } else {
                // finding alternative compatible parts
                String altQuery = "SELECT part_name FROM tblparts WHERE part_type = ?";
                PreparedStatement altPs = dc.conn.prepareStatement(altQuery);
                altPs.setString(1, partType);

                //this lists all parts under part name for now can come back and set it to list only compatible parts with vehicle type!!
                ResultSet altRs = altPs.executeQuery();

                StringBuilder alternatives = new StringBuilder();

                while (altRs.next()) {
                    alternatives.append("- ").append(altRs.getString("part_name")).append("\n");
                }

                lblCompatibilityResult.setText(
                        "❌ This Part is not Compatible with your vehicle:\n\n" +
                                "Please try the " + partType + "s listed below:\n" +
                                alternatives);

                lblStatus.setText(" PLEASE USE TOOL TO VERIFY COMPATIBILITY OF LISTED PARTS WITH YOUR VEHICLE");

            }
            resultsSection.setVisible(true);
            resultsSection.setManaged(true);

        } catch (Exception e) {
            AppUtils.showError(lblStatus, "Error checking compatibility: " + e.getMessage());
            e.printStackTrace();
        }
    }


// setting Clear form to hide results section
    @FXML
    private void handleClearResults() {
        resultsSection.setVisible(false);
        resultsSection.setManaged(false);
        lblStatus.setText("Results cleared - ready for new search");
        statusIndicator.setStyle("-fx-fill: #10b981;");
    }

// error handling for if fields are empty
    private boolean validateVehicleInputs() {
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
            AppUtils.showError(lblStatus, "Please select an engine type");
            return false;
        }

        if (cmbPartName.getValue() == null || cmbPartName.getValue().trim().isEmpty()) {
                AppUtils.showError(lblStatus, "Please select a valid part from the dropdown");
                return false;
            }
        return true;
    }

    // pointing back button to Mechanic Dashboard
    @FXML
    private void handleBack(ActionEvent event) {
        AppUtils.navigateToDashboard(event);
    }




}