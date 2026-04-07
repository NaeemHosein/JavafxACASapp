/*Morphing Customer rate application into rate screen for Mechanic as well
*Stores mechanic rating in feedback and rating column of vehicle owner table
*Update: feedback and rating fields added to mechanic table
*Update: method added to app utils to handle adding feedback and rating to table (this is the only major change to code (yayy!!:) */

package com.cts.javafxacasapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MechanicRateAppController implements Initializable {

    //defining fxml tags

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
    @FXML private Label  lblRole;


    // setting state of stars

    private int selectedRating = 0;   // 0 = nothing selected yet

    private static final String STAR_ON  = "-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 42px; -fx-cursor: hand; -fx-padding: 0;";
    private static final String STAR_OFF = "-fx-background-color: transparent; -fx-text-fill: #334155; -fx-font-size: 42px; -fx-cursor: hand; -fx-padding: 0;";

    //initializing status label and session

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblStatus.setText("Select a star rating and leave your feedback");

        //getting username and role for display
        SessionManager session = SessionManager.getInstance();
        lblUser.setText("User: " + session.getUsername());
        lblRole.setText("Role: " + session.getUserRole());
    }

    // defining star handlers

    @FXML private void handleStar1(ActionEvent event) { applyRating(1); }
    @FXML private void handleStar2(ActionEvent event) { applyRating(2); }
    @FXML private void handleStar3(ActionEvent event) { applyRating(3); }
    @FXML private void handleStar4(ActionEvent event) { applyRating(4); }
    @FXML private void handleStar5(ActionEvent event) { applyRating(5); }

    // defining action handlers from fxml

    @FXML
    private void handleClear(ActionEvent event) {
        selectedRating = 0;
        txtFeedback.clear();
        highlightStars(0);
        lblRatingText.setText("Click a star to rate");
        lblStatus.setText("Cleared. Select a rating and leave your feedback.");
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        // validating rating to ensure there is a selection
        if (selectedRating == 0) {
            AppUtils.showError(lblStatus,"Please select a star rating before submitting.");
            return;
        }

        String feedback = txtFeedback.getText().trim();

        SessionManager session = SessionManager.getInstance();
        String username = session.getUsername();

        boolean success = AppUtils.saveMechanicRating(username, selectedRating, feedback);

        if (success) {
            lblStatus.setText("Thank you! Your rating has been submitted.");

            // return to mechanic dashboard after submitting if successful
            AppUtils.navigateToDashboard(event);

        } else {
            AppUtils.showError(lblStatus,"Error saving your feedback. Please try again.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        AppUtils.navigateToDashboard(event);
    }

    //helper methods for UI functionality

    /** Sets the selected rating and updates the star UI. */
    private void applyRating(int rating) {
        selectedRating = rating;
        highlightStars(rating);

        String[] labels = { "", "Poor", "Fair", "Good", "Very Good", "Excellent" };
        lblRatingText.setText(rating + "/5 — " + labels[rating]);
        lblStatus.setText("You selected " + rating + " star(s). Add feedback and hit Submit.");
    }

    /** Colours in stars when selected and dims when not */
    private void highlightStars(int upTo) {
        List<Button> stars = List.of(btnStar1, btnStar2, btnStar3, btnStar4, btnStar5);
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setStyle(i < upTo ? STAR_ON : STAR_OFF);
        }
    }


}
