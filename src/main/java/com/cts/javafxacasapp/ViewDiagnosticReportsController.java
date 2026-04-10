/* This class lists all reports generated in the generated reports section giving mechanics
* the ability to flag reports and give feedback if the report generated was wrong.
* UPDATE: option to print export as pdf added
* TODO: add more info to dtc table for more robust report*/

package com.cts.javafxacasapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ViewDiagnosticReportsController implements Initializable {

    // Table and Labels
    @FXML private Label lblReportCount;
    @FXML private Label lblUser;
    @FXML private Label lblRole;
    @FXML private TableView<DiagnosticReportRow> tblReports;
    @FXML private TableColumn<DiagnosticReportRow, Integer> colRuleId;
    @FXML private TableColumn<DiagnosticReportRow, String> colDate;
    @FXML private TableColumn<DiagnosticReportRow, String> colVehicle;
    @FXML private TableColumn<DiagnosticReportRow, String> colMechanic;
    @FXML private TableColumn<DiagnosticReportRow, String> colIssues;
    @FXML private TableColumn<DiagnosticReportRow, String> colPart;

    // Adding section for feedback
    @FXML private TextArea txtFeedback;

    // Status Bar
    @FXML private Circle statusIndicator;
    @FXML private Label lblStatus;

    private DatabaseConnection db;
    private ObservableList<DiagnosticReportRow> reportList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = new DatabaseConnection();

        // getting and displaying session user and role info
        SessionManager session = SessionManager.getInstance();
        lblUser.setText("User: " + session.getUsername());
        lblRole.setText("Role: " + session.getUserRole());

        // mapping table columns
        colRuleId.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicle"));
        colMechanic.setCellValueFactory(new PropertyValueFactory<>("mechanic"));
        colPart.setCellValueFactory(new PropertyValueFactory<>("faultyPart"));
        colIssues.setCellValueFactory(new PropertyValueFactory<>("issues"));

        // populating tables with reports from session user
        loadReports(null, null, null);
    }

    // connects to database and pulls data from report table other tables foreign keys in reports table
    private void loadReports(String search, LocalDate start, LocalDate end) {
        reportList.clear();

        try {
            SessionManager session = SessionManager.getInstance();
            int mechanicId = AppUtils.getUserId(session.getUsername(), session.getUserRole());

            String sql =
                    "SELECT r.report_id, r.report_date, r.flag, " +
                            "v.year, v.vehicle_make, v.vehicle_model, " +
                            "m.full_name AS mechanic_name, " +
                            "c.description AS issue, c.faulty_part " +
                            "FROM tbldiagnostic_reports r " +
                            "JOIN tblvehicles v ON r.vehicle_id = v.vehicle_id " +
                            "JOIN tblmechanic m ON r.mechanic_id = m.mechanic_id " +
                            "JOIN tbldiagnostic_codes c ON r.code_id = c.code_id " +
                            "WHERE r.mechanic_id = ?";

            PreparedStatement ps = db.conn.prepareStatement(sql);
            ps.setInt(1, mechanicId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("report_id");
                String dateRaw = rs.getString("report_date");

                String vehicle = rs.getInt("year") + " " +
                        rs.getString("vehicle_make") + " " +
                        rs.getString("vehicle_model");

                String mechanic = rs.getString("mechanic_name");
                String issue = rs.getString("issue");
                String faultyPart = rs.getString("faulty_part");

                int flag = rs.getInt("flag");

                String status = (flag == 1) ? "Flagged" : "OK";

                String uiDate = dateRaw.substring(0, 10);

                reportList.add(new DiagnosticReportRow(
                        id, uiDate, vehicle, mechanic, issue, faultyPart, status
                ));
            }

            tblReports.setItems(reportList);
            lblReportCount.setText(reportList.size() + " reports found");

        } catch (SQLException e) {
            e.printStackTrace();
            AppUtils.showError(lblStatus, "Failed to load reports");
        }
    }

    // defining handlers from fxml file

    @FXML
    private void handleFlagReport(ActionEvent event) {

        DiagnosticReportRow selected = tblReports.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AppUtils.showError(lblStatus,"Please select a report to flag.");
            return;
        }

        String feedback = txtFeedback.getText();

        if (feedback == null || feedback.trim().isEmpty()) {
            AppUtils.showError(lblStatus,"Please enter feedback before flagging.");
            return;
        }

        try {
            String query = "UPDATE tbldiagnostic_reports SET flag = 1, feedback = ? WHERE report_id = ?";
            PreparedStatement ps = db.conn.prepareStatement(query);

            ps.setString(1, feedback);
            ps.setInt(2, selected.getReportId());

            ps.executeUpdate();

            lblStatus.setText("Report flagged successfully.");

            loadReports(null, null, null);
            txtFeedback.clear();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showError(lblStatus,"Error flagging report.");
        }
    }

    @FXML
    private void handlePrintPDF(ActionEvent event) {
        DiagnosticReportRow selected = tblReports.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AppUtils.showError(lblStatus,"Please select a report to export.");
            return;
        }

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                float y = 750; // using to track cursor positioning on doc


                //adding logo to top left

                InputStream logoStream = getClass().getResourceAsStream("/img/ACAS.png");
                if (logoStream != null) {
                    PDImageXObject logo = PDImageXObject.createFromByteArray(doc, logoStream.readAllBytes(), "ACAS-logo");

                    content.drawImage(logo, 50, 700, 100, 100);
                }

                //setting title
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.newLineAtOffset(200, y -90);
                content.showText("ACAS Diagnostic Report");
                content.endText();

                y -= 150;

                //adding guidelines for more realistic report pdf
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(50, y);
                content.showText("General Guidelines- Using your Report Details");
                content.endText();

                y -= 15;


                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.setLeading(14f);
                content.newLineAtOffset(50, y);
                content.showText("ACAS codes are verified accross manufacturer standards making it the perfect tool for your diagnostic needs.");
                content.newLine();
                content.showText("Please use the report along with our compatibility checker to ensure correct parts are used for vehicle compatibility.");
                content.newLine();
                content.showText("Remember to perform a final inspection before returning vehicle to customer.");
                content.endText();

                y -= 175;

                // creating table with report
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                drawRow(content, y, "Field", "Details");
                y -= 20;

                content.setFont(PDType1Font.HELVETICA, 11);


                y = drawRow(content, y, "Report ID", String.valueOf(selected.getReportId()));
                y = drawRow(content, y, "Date", selected.getDate());
                y = drawRow(content, y, "Vehicle", selected.getVehicle());
                y = drawRow(content, y, "Mechanic", selected.getMechanic());
                y = drawRow(content, y, "Issue", selected.getIssues());
                y = drawRow(content, y, "Faulty Part", selected.getFaultyPart());


                y -= 40;

                //adding disclaimer to footer
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                content.setLeading(12f);
                content.newLineAtOffset(50, 80);


                content.showText("Disclaimer: This report was generated based on available diagnostic data.");
                content.newLine();
                content.showText("The ACAS team and managing partners are not liable for incorrect manual inputs or external modifications");
                content.newLine();
                content.showText("and damage caused by improper use of our system.");
                content.newLine();
                content.showText("Generated by ACAS © 2026");
                content.endText();
            }

            // adding fail safe for windows users whose downloads folder is under onedrive and general fallback to user desktop if all fails
            Path downloads = Paths.get(System.getProperty("user.home"), "Downloads");
            if (!Files.exists(downloads)) {
                Path oneDriveDesktop = Paths.get(System.getProperty("user.home"), "OneDrive", "Downloads");
                if (Files.exists(oneDriveDesktop)) {
                    downloads = oneDriveDesktop;
                } else {
                    // fallback to desktop if neither location of downloads folder exists
                    downloads = Paths.get(System.getProperty("user.home"), "Desktop");
                }
            }

            Path file = downloads.resolve("Report_" + selected.getReportId() + ".pdf");
            System.out.println("Saving PDF to: " + file.toAbsolutePath());

            doc.save(file.toFile());
            lblStatus.setText("PDF saved to Downloads.");
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showError(lblStatus,"Error generating PDF: " + e.getMessage());
        }
    }



    // subclass to get data for table view and generating pdf, use of getter and setter methods for data integrity

    public static class DiagnosticReportRow {
        private int reportId;
        private String date;
        private String vehicle;
        private String mechanic;
        private String issues;
        private String faultyPart;
        private String status;

        public DiagnosticReportRow(int reportId, String date, String vehicle,
                                   String mechanic, String issues,
                                   String faultyPart, String status) {
            this.reportId = reportId;
            this.date = date;
            this.vehicle = vehicle;
            this.mechanic = mechanic;
            this.issues = issues;
            this.faultyPart = faultyPart;
            this.status = status;
        }

        public int getReportId() { return reportId; }
        public String getDate() { return date; }
        public String getVehicle() { return vehicle; }
        public String getMechanic() { return mechanic; }
        public String getIssues() { return issues; }
        public String getFaultyPart() { return faultyPart; }
        public String getStatus() { return status; }
    }

    //helper for generating pdf tables
    private float drawRow(PDPageContentStream content, float y, String col1, String col2) throws Exception {

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 11);
        content.newLineAtOffset(50, y);
        content.showText(col1 + ":");
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 11);
        content.newLineAtOffset(200, y);
        content.showText(col2);
        content.endText();

        return y - 18;
    }

    //defining back button
    @FXML
    private void handleBack(ActionEvent event) {
        AppUtils.navigateToDashboard(event);
    }
}