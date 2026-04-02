package com.cts.javafxacasapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.cts.javafxacasapp.AppUtils.showError;

public class CustomerDashController implements Initializable {

    // ── FXML Injections ──────────────────────────────────────────────────────

    @FXML private Label lblWelcome;
    @FXML private Label lblStatus;
    @FXML private Label lblVehicleName;
    @FXML private Label lblVehicleVin;
    @FXML private VBox  vboxRecentSearches;

    // ── Inner model ───────────────────────────────────────────────────────────

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

    // ── State ─────────────────────────────────────────────────────────────────

    /** Simulated logged-in customer name – replace with session/auth data. */
    private String customerName = "John Doe";

    /** Simulated vehicle data – replace with DB lookup by customer. */
    private String vehicleName = "2019 Toyota Camry";
    private String vehicleVin  = "VIN: 4T1B11HK2KUXXXXXX";

    /** Recent part searches – replace with DB/persistent store. */
    private final List<PartSearchEntry> recentSearches = new ArrayList<>();

    // ── Initializable ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate welcome label
        lblWelcome.setText("Welcome, " + customerName);

        // Populate vehicle info
        lblVehicleName.setText(vehicleName);
        lblVehicleVin.setText(vehicleVin);

        // Seed some demo recent searches
        recentSearches.add(new PartSearchEntry("Brake Pad Set - OEM",         true));
        recentSearches.add(new PartSearchEntry("Transmission Fluid - ATF IV",  false));
        recentSearches.add(new PartSearchEntry("Oil Filter - Mobil 1",         true));

        // Render them
        refreshRecentSearches();

        // Status bar
        lblStatus.setText("Connected");
    }

    // ── Event Handlers ────────────────────────────────────────────────────────

    /** Called when the user clicks the "Part Compatibility" card. */
    @FXML
    private void handlePartCheck(MouseEvent event) {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-part-compatibility.fxml", 1100, 750);
        } catch (Exception e) {
            showError();
            e.printStackTrace();
        }
    }
    /** Called when the user clicks the "Rate Application" card. */
    @FXML
    private void handleRateApplication() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-rate-application.fxml", 1100, 750);
        } catch (Exception e) {
            showError();
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogout() {
        try {
            JavafxACASapp.changeScene("javafx-ACAS-app-view.fxml", 1100, 750);
        } catch (Exception e) {
            showError();
            e.printStackTrace();
        }
    }

    private void showError() {
    }

    // Public API (call from other controllers)

    /**
     * Set the logged-in customer's name.  Call this after the user logs in
     * before the scene is displayed.
     */
    public void setCustomerName(String name) {
        this.customerName = name;
        if (lblWelcome != null) lblWelcome.setText("Welcome, " + name);
    }

    /**
     * Update the vehicle info panel.
     */
    public void setVehicleInfo(String name, String vin) {
        this.vehicleName = name;
        this.vehicleVin  = vin;
        if (lblVehicleName != null) lblVehicleName.setText(name);
        if (lblVehicleVin  != null) lblVehicleVin.setText(vin);
    }

    /**
     * Add a new part-search result to the recent-searches list and re-render.
     */
    public void addRecentSearch(String partName, boolean compatible) {
        // Keep the list bounded to the 5 most recent
        recentSearches.add(0, new PartSearchEntry(partName, compatible));
        if (recentSearches.size() > 5) {
            recentSearches.remove(recentSearches.size() - 1);
        }
        refreshRecentSearches();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Rebuild the recent-searches VBox from the current {@link #recentSearches} list.
     * Keeps the first child (the "Recent Searches" header label) and replaces
     * everything below it.
     */
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
