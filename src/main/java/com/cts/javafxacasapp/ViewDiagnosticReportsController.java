package com.cts.javafxacasapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ViewDiagnosticReportsController implements Initializable {

    // Filter controls
    @FXML private TextField txtSearch;
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private ComboBox<String> cmbStatusFilter;

    // Table and Labels
    @FXML private Label lblReportCount;
    @FXML private TableView<DiagnosticReportRow> tblReports;
    @FXML private TableColumn<DiagnosticReportRow, Integer> colRuleId;
    @FXML private TableColumn<DiagnosticReportRow, String> colDate;
    @FXML private TableColumn<DiagnosticReportRow, String> colVehicle;
    @FXML private TableColumn<DiagnosticReportRow, String> colMechanic;
    @FXML private TableColumn<DiagnosticReportRow, String> colIssues;
    @FXML private TableColumn<DiagnosticReportRow, String> colStatus;

    // Status Bar
    @FXML private Circle statusIndicator;
    @FXML private Label lblStatus;

    private DatabaseConnection db;
    private ObservableList<DiagnosticReportRow> reportList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = new DatabaseConnection();

        // Setup Combo Box
        cmbStatusFilter.setItems(FXCollections.observableArrayList("All Statuses", "Completed", "Pending"));
        cmbStatusFilter.getSelectionModel().selectFirst();

        // Map Table Columns to the Data Model properties
        colRuleId.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicle"));
        colMechanic.setCellValueFactory(new PropertyValueFactory<>("mechanic"));
        colIssues.setCellValueFactory(new PropertyValueFactory<>("issues"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Initial Database load
        loadReports(null, null, null, "All Statuses");

        // Selection listener for status bar updates
        tblReports.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateStatus("Selected Report ID: " + newSelection.getReportId(), Color.web("#3b82f6")); // Blue
            }
        });
    }

    /**
     * Connects to the database and fetches the joined data. Applies filtering in-memory
     * so that search strings, dates, and status filters work cleanly with UI updates.
     */
    private void loadReports(String search, LocalDate start, LocalDate end, String statusFilter) {
        reportList.clear();
        try {
            // Join reports across Vehicles, Mechanics, and Code tables using info from DatabaseConnection.java
            String sql = "SELECT r.report_id, r.report_date, v.year, v.vehicle_make, v.vehicle_model, " +
                    "m.full_name AS mechanic_name, c.description AS issue, r.flag " +
                    "FROM tbldiagnostic_reports r " +
                    "JOIN tblvehicles v ON r.vehicle_id = v.vehicle_id " +
                    "JOIN tblmechanic m ON r.mechanic_id = m.mechanic_id " +
                    "JOIN tbldiagnostic_codes c ON r.code_id = c.code_id";

            ResultSet rs = db.conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("report_id");
                String dateRaw = rs.getString("report_date"); // DATETIME like '2023-01-01 10:00:00'
                String vehicle = rs.getInt("year") + " " + rs.getString("vehicle_make") + " " + rs.getString("vehicle_model");
                String mechanic = rs.getString("mechanic_name");
                String issue = rs.getString("issue");
                int flag = rs.getInt("flag");

                // Assuming flag 1 represents a Completed report, mapping 0 to Pending
                String status = (flag == 1) ? "Completed" : "Pending";

                // Filter logic
                boolean matchSearch = true;
                if (search != null && !search.trim().isEmpty()) {
                    String lowerSearch = search.toLowerCase();
                    matchSearch = vehicle.toLowerCase().contains(lowerSearch) || issue.toLowerCase().contains(lowerSearch);
                }

                boolean matchStatus = true;
                if (statusFilter != null && !statusFilter.equals("All Statuses")) {
                    matchStatus = status.equals(statusFilter);
                }

                boolean matchDate = true;
                if ((start != null || end != null) && dateRaw != null && dateRaw.length() >= 10) {
                    try {
                        LocalDate rowDate = LocalDate.parse(dateRaw.substring(0, 10)); // Extract 'YYYY-MM-DD'
                        if (start != null && rowDate.isBefore(start)) matchDate = false;
                        if (end != null && rowDate.isAfter(end)) matchDate = false;
                    } catch (Exception e) {
                        // ignore malformed dates
                    }
                }

                // Only add if all filters match
                if (matchSearch && matchStatus && matchDate) {
                    // Extract just the Date portion for the UI table
                    String uiDate = (dateRaw != null && dateRaw.length() > 10) ? dateRaw.substring(0,10) : dateRaw;
                    reportList.add(new DiagnosticReportRow(id, uiDate, vehicle, mechanic, issue, status));
                }
            }

            tblReports.setItems(reportList);
            lblReportCount.setText(reportList.size() + " reports found");

        } catch (SQLException e) {
            e.printStackTrace();
            updateStatus("Failed to load reports: " + e.getMessage(), Color.web("#ef4444"));
        }
    }

    // ==== ACTION HANDLERS ====

    @FXML
    private void handleBack(ActionEvent event) {
        AppUtils.navigateToDashboard(event);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String search = txtSearch.getText();
        LocalDate start = dateFrom.getValue();
        LocalDate end = dateTo.getValue();
        String status = cmbStatusFilter.getValue();

        loadReports(search, start, end, status);
        updateStatus("Filters applied. Found " + reportList.size() + " entries.", Color.web("#10b981"));
    }

    @FXML
    private void handleClearFilters(ActionEvent event) {
        txtSearch.clear();
        dateFrom.setValue(null);
        dateTo.setValue(null);
        cmbStatusFilter.getSelectionModel().selectFirst();

        loadReports(null, null, null, "All Statuses");
        updateStatus("Filters cleared.", Color.web("#10b981"));
    }

    @FXML
    private void handleViewSelected(ActionEvent event) {
        DiagnosticReportRow selected = tblReports.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateStatus("Opening detailed view for Report ID: " + selected.getReportId(), Color.web("#10b981"));
            // Custom dialog popup or scene change code goes here
        } else {
            updateStatus("Please select a report to view from the table.", Color.web("#ef4444")); // Red Error
        }
    }

    @FXML
    private void handlePrintPDF(ActionEvent event) {
        DiagnosticReportRow selected = tblReports.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateStatus("Generating PDF for Report ID: " + selected.getReportId() + "...", Color.web("#3b82f6"));
            // Add PDF generation code here (e.g., using iText or PDFBox)
        } else {
            updateStatus("Please select a report to export to PDF.", Color.web("#ef4444"));
        }
    }

    private void updateStatus(String message, Color color) {
        if (lblStatus != null) lblStatus.setText(message);
        if (statusIndicator != null) statusIndicator.setFill(color);
    }

    // ==== DATA MODEL ====

    /**
     * View-specific data model that combines Vehicle, Mechanic, and Code data
     */
    public static class DiagnosticReportRow {
        private int reportId;
        private String date;
        private String vehicle;
        private String mechanic;
        private String issues;
        private String status;

        public DiagnosticReportRow(int reportId, String date, String vehicle, String mechanic, String issues, String status) {
            this.reportId = reportId;
            this.date = date;
            this.vehicle = vehicle;
            this.mechanic = mechanic;
            this.issues = issues;
            this.status = status;
        }

        public int getReportId() { return reportId; }
        public String getDate() { return date; }
        public String getVehicle() { return vehicle; }
        public String getMechanic() { return mechanic; }
        public String getIssues() { return issues; }
        public String getStatus() { return status; }
    }
}
