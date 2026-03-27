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

    DatabaseConnection dc = new DatabaseConnection();
    private static Stage currentStg;

    public static JavafxACASapp getInstance() {
        return null;
    }

    public static void changeScene(String s, String s1) {
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        currentStg = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/cts/javafxacasapp/javafx-ACAS-app-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("ACAS - Automotive Component Advisory System: User Login");
        primaryStage.setScene(scene);

        // This allows resizing.
        primaryStage.setResizable(true);

        // This sets minimum size instead of fixed size.
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        primaryStage.show();

        try {
            // Test database connection by reading users
            String query = "SELECT * FROM tbladministrator";
            dc.rst = dc.stat.executeQuery(query);

            System.out.println("=== Database Connection Successful ===");
            System.out.println("Users in database:");
            while(dc.rst.next()) {
                System.out.print("ID: " + dc.rst.getInt("admin_id"));

            }
            System.out.println("=====================================");
        } catch (SQLException e) {
            // Log the exception using the Java logger
            logger.severe("An error occurred: Database connectivity failed");
            logger.severe(e.toString());
            System.exit(0);
        }
    }

    public static void changeScene(String fxml, Integer sWidth, Integer sHeight) throws IOException {
        Parent pane = FXMLLoader.load(JavafxACASapp.class.getResource("/com/cts/javafxacasapp/" + fxml));

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

            case "javafx-ACAS-app-view.fxml":
                currentStg.setTitle("ACAS - User Login");
                break;
        }

        currentStg.getScene().setRoot(pane);
        currentStg.sizeToScene(); // optional

    }

    public static void main(String[] args) {
        launch();
    }
}