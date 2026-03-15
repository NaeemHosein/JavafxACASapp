package com.cts.javafxacasapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class JavafxACASapp extends Application {

    private static final Logger logger = Logger.getLogger(JavafxACASapp.class.getName());
    private static JavafxACASapp instance;  // ← singleton instance

    DatabaseConnection dc = new DatabaseConnection();
    private static Stage currentStg;

    public static JavafxACASapp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        instance = this;  // ← save instance on start
        currentStg = primaryStage;
        primaryStage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/cts/javafxacasapp/javafx-ACAS-app-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("ACAS - Automotive Component Advisory System: User Login");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(750);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            String query = "SELECT * FROM tblusers";
            dc.rst = dc.stat.executeQuery(query);
            System.out.println("=== Database Connection Successful ===");
            while(dc.rst.next()) {
                System.out.print("ID: " + dc.rst.getInt("user_id"));
                System.out.print(" | Username: " + dc.rst.getString("username"));
                System.out.print(" | Role: " + dc.rst.getString("role"));
                System.out.println();
            }
            System.out.println("=====================================");
        } catch (SQLException e) {
            logger.severe("An error occurred: Database connectivity failed");
            logger.severe(e.toString());
            System.exit(0);
        }
    }

    public void changeScene(String fxml, Integer sWidth, Integer sHeight) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("/com/cts/javafxacasapp/" + fxml));

        switch (fxml) {
            case "mechanic-dashboard-view.fxml":
                currentStg.setTitle("ACAS - Mechanic Dashboard");
                break;
            case "admin-dashboard-view.fxml":
                currentStg.setTitle("ACAS - Administrator Dashboard");
                break;
            case "owner-verification-view.fxml":
                currentStg.setTitle("ACAS - Part Compatibility Verification");
                break;
            case "diagnostic-report-view.fxml":
                currentStg.setTitle("ACAS - Diagnostic Report");
                break;
            case "manage-rules-view.fxml":
                currentStg.setTitle("ACAS - Manage Diagnostic Rules");
                break;
            case "acas-login-view.fxml":
                currentStg.setTitle("ACAS - User Login");
                break;
        }

        currentStg.setWidth(sWidth);
        currentStg.setHeight(sHeight);
        currentStg.getScene().setRoot(pane);
    }

    public static void main(String[] args) {
        launch();
    }
}