/* This controller defines the actions of the fxml scene that
prints reports flagged by mechanics for admins to review.
* gives admin option to resolve issue
* Update: side pane added to view more info about report*/

package com.cts.javafxacasapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewIssueReportsController implements Initializable {

    @FXML private TableView<IssueTable> tblIssues;
    @FXML private TableColumn<IssueTable, Integer> colReportId;
    @FXML private TableColumn<IssueTable, String> colSubmittedBy;
    @FXML private TableColumn<IssueTable, String> colIssueDate;
    @FXML private TableColumn<IssueTable, String> colDtcCode;

    @FXML private Label lblIssueCount;
    @FXML private Label lblReportId;
    @FXML private Label lblMechanicId;
    @FXML private Label lblDetailDate;
    @FXML private Label lblDtcCode;
    @FXML private TextArea txtFeedback;
    @FXML private Label lblUser;
    @FXML private Label lblRole;
    @FXML private Label lblVehicleDetails;

    @FXML private Circle statusIndicator;
    @FXML private Label lblStatus;

    private DatabaseConnection db;
    private ObservableList<IssueTable> allIssues = FXCollections.observableArrayList();
    private ObservableList<IssueTable> filteredIssues = FXCollections.observableArrayList();

    private String currentStatusFilter = "All";

    //initializing UI
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new DatabaseConnection();

        //grabbing session username and role
        SessionManager session = SessionManager.getInstance();
        lblUser.setText("User: " + session.getUsername());
        lblRole.setText("Role: " + session.getUserRole());


        //forming/ defining table cells
        colReportId.setCellValueFactory(new PropertyValueFactory<>("issueId"));
        colSubmittedBy.setCellValueFactory(new PropertyValueFactory<>("submittedBy"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDtcCode.setCellValueFactory(new PropertyValueFactory<>("dtcCode"));

        tblIssues.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) showIssueDetails(newSel);
            else clearIssueDetails();
        });

        loadData();
    }

// loading data into tables
    private void loadData() {
        allIssues.clear();

        try {
            String sql = """
                SELECT r.report_id, m.full_name, r.report_date, r.flag, r.feedback, c.code, r.vehicle_id
                FROM tbldiagnostic_reports r
                JOIN tblmechanic m ON r.mechanic_id = m.mechanic_id
                JOIN tbldiagnostic_codes c ON r.code_id = c.code_id
                """;

            ResultSet rs = db.conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                allIssues.add(new IssueTable(
                        rs.getInt("report_id"),
                        rs.getString("full_name"),
                        rs.getString("report_date"),
                        rs.getString("code"),
                        rs.getString("feedback"),
                        rs.getInt("flag"),
                        rs.getInt("vehicle_id")
                ));
            }

            filterData();
            lblStatus.setText("Loaded " + allIssues.size() + " reports");

        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Error loading reports");
        }
    }

    //status filter will be defined later so table result can toggle between flagged and resolved issues
    private void filterData() {
        filteredIssues.clear();

        for (IssueTable issue : allIssues) {

            if (currentStatusFilter.equals("All") && issue.getFlag() == 1) {
                filteredIssues.add(issue);
            }

            else if (currentStatusFilter.equals("Resolved") && issue.getFlag() == 0) {
                filteredIssues.add(issue);
            }
        }

        tblIssues.setItems(filteredIssues);
        lblIssueCount.setText(filteredIssues.size() + " issues");
    }

    //method to define each record in table
    private void showIssueDetails(IssueTable issue) {
        lblReportId.setText(String.valueOf(issue.getIssueId()));
        lblMechanicId.setText(issue.getSubmittedBy());
        lblDetailDate.setText(issue.getIssueDate());
        lblDtcCode.setText(issue.getDtcCode());
        txtFeedback.setText(issue.getFeedback());

        //grabbing vehicle details from app util method
        String[] vehicle = AppUtils.getVehicleDetails(db, issue.getVehicleId());

        if (vehicle != null) {
            lblVehicleDetails.setText(
                    vehicle[0] + " " + vehicle[1] + " " + vehicle[2] + " (" + vehicle[3] + ")"
            );
        } else {
            lblVehicleDetails.setText("No vehicle info");
        }

        lblStatus.setText("Viewing report " + issue.getIssueId());
    }

    //updating status filter to show either flagged or resolved reports
    @FXML
    private void handleFilterAll(ActionEvent event) {
        currentStatusFilter = "All";
        filterData();
    }

    @FXML
    private void handleFilterResolved(ActionEvent event) {
        currentStatusFilter = "Resolved";
        filterData();
    }

    //calling method in app util to unflag report
    @FXML
    private void handleMarkResolved(ActionEvent event) {
        IssueTable selected = tblIssues.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AppUtils.showError(lblStatus, "Please select a report first");
            return;
        }

        boolean success = AppUtils.resolveReport(selected.getIssueId());

        if (success) {
            selected.setFlag(0);
            filterData();
            lblStatus.setText("Report marked as resolved");
        } else {
            AppUtils.showError(lblStatus, "Error updating report");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        AppUtils.navigateToDashboard(event);
    }

    private void clearIssueDetails() {
        lblReportId.setText("-");
        lblMechanicId.setText("-");
        lblDetailDate.setText("-");
        lblDtcCode.setText("-");
        txtFeedback.clear();
        lblVehicleDetails.setText("-");
    }

    // defining table column fields and getter and setter methods for attributes
    public static class IssueTable {
        private int issueId;
        private String submittedBy;
        private String issueDate;
        private String dtcCode;
        private String feedback;
        private int flag; // 1 = flagged, 0 = resolved flagged set as int so that other numbers can represent other things in future upgrades
        private int vehicleId; //adding vehicle details

        //building constructor
        public IssueTable(int issueId, String submittedBy, String issueDate,
                        String dtcCode, String feedback, int flag, int vehicleId) {
            this.issueId = issueId;
            this.submittedBy = submittedBy;
            this.issueDate = issueDate;
            this.dtcCode = dtcCode;
            this.feedback = feedback;
            this.flag = flag;
            this.vehicleId = vehicleId;
        }

        public int getIssueId() { return issueId; }
        public String getSubmittedBy() { return submittedBy; }
        public String getIssueDate() { return issueDate; }
        public String getDtcCode() { return dtcCode; }
        public String getFeedback() { return feedback; }
        public int getFlag() { return flag; }
        public int getVehicleId() { return vehicleId; }

        public void setFlag(int flag) { this.flag = flag; }
    }
}