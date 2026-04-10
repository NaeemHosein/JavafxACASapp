/*Customer/ Vehicle Owner Dashboard- customers can check part compatibility and rate the app
* update: made session edits so that customers can view previous searches from part compatibility in dashboard
* during the session only.*/

package com.cts.javafxacasapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;



public class CustomerDashController implements Initializable {


    @FXML private Label lblWelcome;
    @FXML private Label lblStatus;
    @FXML private Label lblVehicleName;
    @FXML private Label lblVehicleDetails;
    @FXML private VBox  vboxRecentSearches;

    private List<PartSearchEntry> recentSearches; //storing recent searches as an array from session



    @Override
    public void initialize(URL location, ResourceBundle resources) {



        //attempting to initialize session
        SessionManager session = SessionManager.getInstance();

        lblWelcome.setText("Welcome, " + session.getUsername());


        // Populating vehicle info section

        try {

            int vehicleId = AppUtils.getVehicleId(new DatabaseConnection(), session.getUsername());

            if (vehicleId != -1) {
                String[] vehicle = AppUtils.getVehicleDetails(new DatabaseConnection(), vehicleId);

                if (vehicle != null) {
                    String year = vehicle[0];
                    String make = vehicle[1];
                    String model = vehicle[2];
                    String engine = vehicle[3];

                    // adding vehicle name
                    lblVehicleName.setText(make + " " + model);

                    // adding year and engine type as details
                    lblVehicleDetails.setText("Year: " + year + "  Engine Type: " + engine);
                }
            } else {
                lblVehicleName.setText("No Vehicle Found");
                lblVehicleDetails.setText("Please contact support");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblVehicleName.setText("Error loading vehicle");
        }

        //loading searches

        recentSearches = session.getRecentSearches();


        //refreshes searches everytime customer returns to dashboard
        refreshRecentSearches();

        // setting status bar text
        lblStatus.setText("Connected");
    }

    /** Represents a single part-compatibility search result. */
    public static class PartSearchEntry {
        private final String partName;
        private final boolean compatible;

        public PartSearchEntry(String partName, boolean compatible) {
            this.partName   = partName;
            this.compatible = compatible;
        }

        public String  getPartName()   { return partName;   }
        public boolean isCompatible()  { return compatible; }
    }





    // defining action handlers

     //check compatibility button goes to part compatibility screen
    @FXML
    private void handlePartCheck(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-part-compatibility.fxml", 1100, 750);
        } catch (Exception e) {
            AppUtils.showError(lblStatus, "Something went wrong");
            e.printStackTrace();
        }
    }
    // rate application opens rate screen ui
    @FXML
    private void handleRateApplication() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-rate-application.fxml", 1100, 750);
        } catch (Exception e) {
            AppUtils.showError(lblStatus, "Something went wrong");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUserGuide(MouseEvent event) {
        AppUtils.openPDF("/docs/ACAS_customerManual.pdf");     }


    @FXML
    private void handleLogout() {
        AppUtils.Logout();
    }




   // methods to attempt to capture recent searches from compatibility screen without creating a new table :')
    private void refreshRecentSearches() {
        // Remove all rows (index 1 onward), keep the header label at index 0
        if (vboxRecentSearches.getChildren().size() > 1) {
            vboxRecentSearches.getChildren().subList(1, vboxRecentSearches.getChildren().size()).clear();
        }

        for (PartSearchEntry entry : recentSearches) {
            vboxRecentSearches.getChildren().add(buildSearchRow(entry));
        }
    }

    /** Builds a single coloured row for a part search result. */
    private HBox buildSearchRow(PartSearchEntry entry) {
        boolean ok = entry.isCompatible();

        // Colour scheme: green for compatible, red for not
        String bgColor     = ok ? "rgba(16, 185, 129, 0.1)" : "rgba(239, 68, 68, 0.1)";
        String borderColor = ok ? "rgba(16, 185, 129, 0.2)" : "rgba(239, 68, 68, 0.2)";
        String statusColor = ok ? "#10b981"                 : "#ef4444";
        String statusText  = ok ? "✓ Compatible"            : "✗ Not Compatible";

        HBox row = new HBox(10);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-padding: 12;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-radius: 8;"
        );

        Label partLabel = new Label(entry.getPartName());
        partLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label statusLabel = new Label(statusText);
        statusLabel.setStyle(
                "-fx-text-fill: " + statusColor + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );

        VBox inner = new VBox(2, partLabel, statusLabel);
        HBox.setHgrow(inner, Priority.ALWAYS);
        row.getChildren().add(inner);

        return row;
    }
}
