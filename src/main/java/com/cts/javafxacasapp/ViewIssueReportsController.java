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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewIssueReportsController implements Initializable {

    @FXML private ComboBox<String> cmbPriorityFilter;
    @FXML private TextField txtIssueSearch;
    @FXML private Button btnFilterAll;
    @FXML private Button btnFilterOpen;
    @FXML private Button btnFilterInProgress;
    @FXML private Button btnFilterResolved;

    @FXML private TableView<IssueRow> tblIssues;
    @FXML private TableColumn<IssueRow, Integer> colIssueId;
    @FXML private TableColumn<IssueRow, String> colSubmittedBy;
    @FXML private TableColumn<IssueRow, String> colIssueDate;
    @FXML private TableColumn<IssueRow, String> colIssueStatus;

    @FXML private Label lblIssueCount;
    @FXML private Label lblDetailId;
    @FXML private Label lblDetailSubmittedBy;
    @FXML private Label lblDetailDate;
    @FXML private Label lblDetailPriority;
    @FXML private ComboBox<String> cmbDetailStatus;
    @FXML private TextArea txtDetailDescription;
    @FXML private TextArea txtAdminNotes;
    @FXML private Label lblUser;

    @FXML private Circle statusIndicator;
    @FXML private Label lblStatus;

    private DatabaseConnection db;
    private ObservableList<IssueRow> allIssues = FXCollections.observableArrayList();
    private ObservableList<IssueRow> filteredIssues = FXCollections.observableArrayList();
    private String currentStatusFilter = "All";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new DatabaseConnection();

        cmbPriorityFilter.setItems(FXCollections.observableArrayList("All Priorities", "High", "Normal", "Low"));
        cmbPriorityFilter.getSelectionModel().selectFirst();

        cmbDetailStatus.setItems(FXCollections.observableArrayList("Open", "In Progress", "Resolved"));

        colIssueId.setCellValueFactory(new PropertyValueFactory<>("issueId"));
        colSubmittedBy.setCellValueFactory(new PropertyValueFactory<>("submittedBy"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colIssueStatus.setCellValueFactory(new PropertyValueFactory<>("issueStatus"));

        tblIssues.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                showIssueDetails(newSel);
            } else {
                clearIssueDetails();
            }
        });

        txtIssueSearch.textProperty().addListener((obs, oldText, newText) -> filterData());
        cmbPriorityFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterData());

        loadData();
    }

    private void loadData() {
        allIssues.clear();
        try {
            // Using existing tables to pull 'Issues': mapping diagnostic_reports as submitted issues
            String sql = "SELECT r.report_id, m.full_name, r.report_date, r.flag, r.feedback, c.description " +
                    "FROM tbldiagnostic_reports r " +
                    "JOIN tblmechanic m ON r.mechanic_id = m.mechanic_id " +
                    "JOIN tbldiagnostic_codes c ON r.code_id = c.code_id";
            ResultSet rs = db.conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("report_id");
                String name = rs.getString("full_name");
                String dateRaw = rs.getString("report_date");
                String date = (dateRaw != null && dateRaw.length() > 10) ? dateRaw.substring(0, 10) : dateRaw;
                int flag = rs.getInt("flag");

                String status = "Open"; // Mapping: 0 = Open, 1 = In Progress, 2 = Resolved
                if (flag == 1) status = "In Progress";
                else if (flag == 2) status = "Resolved";

                String desc = rs.getString("feedback");
                if (desc == null || desc.trim().isEmpty()) {
                    desc = "DTC Issue Report: " + rs.getString("description");
                }

                // Mock priority based on flag for UI categorization
                String priority = (flag == 0) ? "High" : "Normal";

                allIssues.add(new IssueRow(id, name, date, status, priority, desc));
            }
            filterData();
            updateStatus("Loaded " + allIssues.size() + " issues from the database.", Color.web("#10b981"));
        } catch (SQLException e) {
            e.printStackTrace();
            updateStatus("Database Connection Error: " + e.getMessage(), Color.web("#ef4444"));
        }
    }

    private void filterData() {
        filteredIssues.clear();
        String search = txtIssueSearch.getText() != null ? txtIssueSearch.getText().toLowerCase() : "";
        String prioFilter = cmbPriorityFilter.getValue() != null ? cmbPriorityFilter.getValue() : "All Priorities";

        for (IssueRow issue : allIssues) {
            boolean matchSearch = issue.getSubmittedBy().toLowerCase().contains(search) ||
                    String.valueOf(issue.getIssueId()).contains(search) ||
                    (issue.getDescription() != null && issue.getDescription().toLowerCase().contains(search));
            boolean matchPriority = prioFilter.equals("All Priorities") || issue.getPriority().equals(prioFilter);
            boolean matchStatus = currentStatusFilter.equals("All") || issue.getIssueStatus().equals(currentStatusFilter);

            if (matchSearch && matchPriority && matchStatus) {
                filteredIssues.add(issue);
            }
        }
        tblIssues.setItems(filteredIssues);
        lblIssueCount.setText(filteredIssues.size() + " issues");
    }

    @FXML private void handleFilterAll(ActionEvent event) { currentStatusFilter = "All"; filterData(); }
    @FXML private void handleFilterOpen(ActionEvent event) { currentStatusFilter = "Open"; filterData(); }
    @FXML private void handleFilterInProgress(ActionEvent event) { currentStatusFilter = "In Progress"; filterData(); }
    @FXML private void handleFilterResolved(ActionEvent event) { currentStatusFilter = "Resolved"; filterData(); }

    private void showIssueDetails(IssueRow issue) {
        lblDetailId.setText(String.valueOf(issue.getIssueId()));
        lblDetailSubmittedBy.setText(issue.getSubmittedBy());
        lblDetailDate.setText(issue.getIssueDate());
        lblDetailPriority.setText(issue.getPriority());
        cmbDetailStatus.setValue(issue.getIssueStatus());
        txtDetailDescription.setText(issue.getDescription());
        txtAdminNotes.setText(""); // Currently unsupported in DB, cleared for presentation
        updateStatus("Viewing details for Issue ID " + issue.getIssueId(), Color.web("#3b82f6"));
    }

    private void clearIssueDetails() {
        lblDetailId.setText("—");
        lblDetailSubmittedBy.setText("—");
        lblDetailDate.setText("—");
        lblDetailPriority.setText("—");
        cmbDetailStatus.setValue(null);
        txtDetailDescription.setText("");
        txtAdminNotes.setText("");
    }

    @FXML
    private void handleSaveChanges(ActionEvent event) {
        IssueRow selected = tblIssues.getSelectionModel().getSelectedItem();
        if (selected == null) {
            updateStatus("Please select an issue to save changes.", Color.web("#ef4444"));
            return;
        }

        String newStatus = cmbDetailStatus.getValue();
        if (newStatus == null) return;

        int flag = 0;
        if ("In Progress".equals(newStatus)) flag = 1;
        else if ("Resolved".equals(newStatus)) flag = 2;

        try {
            String sql = "UPDATE tbldiagnostic_reports SET flag = ? WHERE report_id = ?";
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ps.setInt(1, flag);
            ps.setInt(2, selected.getIssueId());
            ps.executeUpdate();

            selected.setIssueStatus(newStatus);
            tblIssues.refresh();
            filterData();
            updateStatus("Status for Issue ID " + selected.getIssueId() + " successfully updated to '" + newStatus + "'.", Color.web("#10b981"));
        } catch (SQLException e) {
            e.printStackTrace();
            updateStatus("Failed to update issue status. Error: " + e.getMessage(), Color.web("#ef4444"));
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        AppUtils.navigateToDashboard(event);
    }

    @FXML
    private void handleExportIssue(ActionEvent event) {
        IssueRow selected = tblIssues.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateStatus("Preparing to export Issue ID " + selected.getIssueId() + " to PDF...", Color.web("#3b82f6"));
        } else {
            updateStatus("Please choose an issue to export.", Color.web("#ef4444"));
        }
    }

    private void updateStatus(String msg, Color color) {
        if(lblStatus != null) lblStatus.setText(msg);
        if(statusIndicator != null) statusIndicator.setFill(color);
    }

    // ==== Model Class ====
    public static class IssueRow {
        private int issueId;
        private String submittedBy;
        private String issueDate;
        private String issueStatus;
        private String priority;
        private String description;

        public IssueRow(int issueId, String submittedBy, String issueDate, String issueStatus, String priority, String description) {
            this.issueId = issueId;
            this.submittedBy = submittedBy;
            this.issueDate = issueDate;
            this.issueStatus = issueStatus;
            this.priority = priority;
            this.description = description;
        }

        public int getIssueId() { return issueId; }
        public String getSubmittedBy() { return submittedBy; }
        public String getIssueDate() { return issueDate; }
        public String getIssueStatus() { return issueStatus; }
        public void setIssueStatus(String status) { this.issueStatus = status; }
        public String getPriority() { return priority; }
        public String getDescription() { return description; }
    }
}
