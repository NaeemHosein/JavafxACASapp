package com.cts.javafxacasapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RateAppController implements Initializable {

    // ── FXML Injections ──────────────────────────────────────────────────────

    @FXML private Label  lblUser;
    @FXML private Label  lblRatingText;

    @FXML private Button btnStar1;
    @FXML private Button btnStar2;
    @FXML private Button btnStar3;
    @FXML private Button btnStar4;
    @FXML private Button btnStar5;

    @FXML private TextArea txtFeedback;

    @FXML private Circle statusIndicator;
    @FXML private Label  lblStatus;

    // ── State ─────────────────────────────────────────────────────────────────

    private int selectedRating = 0;   // 0 = nothing selected yet

    private static final String STAR_ON  = "-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 42px; -fx-cursor: hand; -fx-padding: 0;";
    private static final String STAR_OFF = "-fx-background-color: transparent; -fx-text-fill: #334155; -fx-font-size: 42px; -fx-cursor: hand; -fx-padding: 0;";

    // ── Initializable ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateStatus("Select a star rating and leave your feedback", Color.web("#10b981"));
    }

    // ── Star Handlers ─────────────────────────────────────────────────────────

    @FXML private void handleStar1(ActionEvent event) { applyRating(1); }
    @FXML private void handleStar2(ActionEvent event) { applyRating(2); }
    @FXML private void handleStar3(ActionEvent event) { applyRating(3); }
    @FXML private void handleStar4(ActionEvent event) { applyRating(4); }
    @FXML private void handleStar5(ActionEvent event) { applyRating(5); }

    // ── Action Handlers ───────────────────────────────────────────────────────

    @FXML
    private void handleClear(ActionEvent event) {
        selectedRating = 0;
        txtFeedback.clear();
        highlightStars(0);
        lblRatingText.setText("Click a star to rate");
        updateStatus("Cleared. Select a rating and leave your feedback.", Color.web("#10b981"));
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        // Validate: a star must be selected
        if (selectedRating == 0) {
            updateStatus("Please select a star rating before submitting.", Color.web("#ef4444"));
            return;
        }

        String feedback = txtFeedback.getText().trim();

        // TODO: persist to database, e.g.:
        // db.saveRating(selectedRating, feedback);
        System.out.println("[RateApplication] Rating : " + selectedRating + "/5");
        System.out.println("[RateApplication] Feedback: " + feedback);

        updateStatus("Thank you! Your " + selectedRating + "-star rating has been submitted.", Color.web("#10b981"));

        // Reset after submit
        handleClear(event);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        AppUtils.navigateToDashboard(event);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Sets the selected rating and updates the star UI. */
    private void applyRating(int rating) {
        selectedRating = rating;
        highlightStars(rating);

        String[] labels = { "", "Poor", "Fair", "Good", "Very Good", "Excellent" };
        lblRatingText.setText(rating + "/5 — " + labels[rating]);
        updateStatus("You selected " + rating + " star(s). Add feedback and hit Submit.", Color.web("#10b981"));
    }

    /** Colours in stars 1‥n and dims stars n+1‥5. */
    private void highlightStars(int upTo) {
        List<Button> stars = List.of(btnStar1, btnStar2, btnStar3, btnStar4, btnStar5);
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setStyle(i < upTo ? STAR_ON : STAR_OFF);
        }
    }

    /** Updates the status bar label and indicator dot colour. */
    private void updateStatus(String message, Color color) {
        if (lblStatus != null)       lblStatus.setText(message);
        if (statusIndicator != null) statusIndicator.setFill(color);
    }
}
