/*Allows admin to create, read, update and delete details from dtc and vehicle tables
* todo: implement the same functionality for part compatibility logic... will need larger table maybe?*/

package com.cts.javafxacasapp;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class SystemConfigController implements Initializable {

    @FXML private TableView<ObservableList<String>> tblDatabase;
    @FXML private TableColumn<ObservableList<String>, String> col1, col2, col3, col4;

    @FXML private TextField txtField1;
    @FXML private TextField txtField2;
    @FXML private TextField txtField3;
    @FXML private TextField txtField4;
    @FXML private TextField txtField5;

    @FXML private TextField lbl1;
    @FXML private TextField lbl2;
    @FXML private TextField lbl3;
    @FXML private TextField lbl4;
    @FXML private TextField lbl5;
    @FXML private Label lblStatus;
    @FXML private Label lblUser;
    @FXML private Label lblRole;

    @FXML private Button btnSaveChanges;

    private String currentTable = "DTC";
    private int selectedId = -1;

    private DatabaseConnection db;

    //initializing scene
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        db = new DatabaseConnection();

        // getting user name and role from session logic
        SessionManager session = SessionManager.getInstance();
        lblUser.setText("User: " + session.getUsername());
        lblRole.setText("Role: " + session.getUserRole());

        txtField1.setEditable(false); // ID field locked

        // testing recommended solution to manually build tables from AppUtils
        col1.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().get(0)));
        col2.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().get(1)));
        col3.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().get(2)));
        col4.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().get(3)));

        setupSelection();
        loadDTC();
    }

   // todo: try to implement toggle logic like in login screen for these filters when they are clicked

    @FXML
    private void handleFilterDTC(ActionEvent e) {
        currentTable = "DTC";
        loadDTC();
    }

    @FXML
    private void handleFilterVehicles(ActionEvent e) {
        currentTable = "VEHICLES";
        loadVehicles();
    }

    // loading table data for left side of screen

    private void loadDTC() {

        setLabels("ID", "Code", "Faulty Part", "Description", "Resolution");

        AppUtils.loadTable(tblDatabase,
                "SELECT code_id, code, faulty_part, description FROM tbldiagnostic_codes");

        clearFields();
    }

    private void loadVehicles() {

        setLabels("ID", "Make", "Model", "Engine", "Year");

        AppUtils.loadTable(tblDatabase,
                "SELECT vehicle_id, vehicle_make, vehicle_model, engine_type FROM tblvehicles");

        clearFields();
    }

    // defining selection logic we want the button to change when a record is selected

    private void setupSelection() {

        tblDatabase.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, field) -> {

            if (field != null) {

                selectedId = Integer.parseInt(field.get(0));

                txtField1.setText(field.get(0));
                txtField2.setText(field.get(1));
                txtField3.setText(field.get(2));
                txtField4.setText(field.get(3));

                btnSaveChanges.setText("Update Record"); //text switches to update record when a record is clicked


                if (currentTable.equals("DTC")) {
                    loadDTCExtra(selectedId);
                } else {
                    loadVehicleExtra(selectedId);
                }

            }
        });
    }

    private void loadDTCExtra(int id) {
        try {
            ResultSet rs = db.conn.createStatement().executeQuery(
                    "SELECT description, resolution FROM tbldiagnostic_codes WHERE code_id = " + id
            );

            if (rs.next()) {
                txtField4.setText(rs.getString("description"));
                txtField5.setText(rs.getString("resolution"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVehicleExtra(int id) {
        try {
            ResultSet rs = db.conn.createStatement().executeQuery(
                    "SELECT year FROM tblvehicles WHERE vehicle_id = " + id
            );

            if (rs.next()) {
                txtField5.setText(String.valueOf(rs.getInt("year")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //using AppUtils methods to handle sql queries for saving info to dtc and vehicle table

    @FXML
    private void handleSaveChanges(ActionEvent e) {

        boolean success = false;

        if (currentTable.equals("DTC")) {

            success = AppUtils.saveDTC(
                    selectedId,
                    txtField2.getText(), // dtc
                    txtField4.getText(), // description
                    txtField5.getText(), // resolution
                    txtField3.getText()  // faulty part
            );

            loadDTC();
        }

        else if (currentTable.equals("VEHICLES")) {

            success = AppUtils.saveVehicle(
                    selectedId,
                    txtField2.getText(), // make
                    txtField3.getText(), // model
                    txtField4.getText(), // engine
                    txtField5.getText()  // year
            );

            loadVehicles();
        }

        lblStatus.setText(success ? "Saved successfully" : "Error saving");
        clearFields();
    }

    // using AppUtil to Handle deleting

    @FXML
    private void handleDeleteRecord(ActionEvent e) {

        if (selectedId == -1) {
            lblStatus.setText("Select a record first");
            return;
        }

        boolean success = AppUtils.deleteRecord(currentTable, selectedId);

        lblStatus.setText(success ? "Deleted successfully" : "Delete failed");

        if (currentTable.equals("DTC")) loadDTC();
        else loadVehicles();
    }

    // class helpers

    private void clearFields() {

        selectedId = -1;

        txtField1.clear();
        txtField2.clear();
        txtField3.clear();
        txtField4.clear();
        txtField5.clear();

        btnSaveChanges.setText("Save New Details");
    }

    // text labels based on table view
    private void setLabels(String l1, String l2, String l3, String l4, String l5) {
        lbl1.setText(l1);
        lbl2.setText(l2);
        lbl3.setText(l3);
        lbl4.setText(l4);
        lbl5.setText(l5);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        AppUtils.navigateToDashboard(event);
    }
}