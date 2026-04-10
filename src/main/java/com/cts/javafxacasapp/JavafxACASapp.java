/*Main class responsible for launching the app login page
* contains metjods to change scene, the db connectivity and load app icon*/
// TODO: UPDATE HEADERS FOR EACH PAGE
// CREATE README FILE*/

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


    @Override
    public void start(Stage primaryStage) throws IOException {
        currentStg = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/cts/javafxacasapp/javafx-ACAS-app-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("ACAS - Automotive Component Advisory System");
        primaryStage.setScene(scene);

        // This allows resizing.
        primaryStage.setResizable(true);

        // This sets minimum size instead of fixed size.
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);


        //Adding logo icon
        primaryStage.getIcons().add(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/img/ACAS-icon.png")
                )
        );

        primaryStage.show();

        try {
            // Test database connection by reading users
            String query = "SELECT * FROM tbladministrator";
            dc.rst = dc.stat.executeQuery(query);

            System.out.println("=== Database Connection Successful ===");
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
            case "javafx-ACAS-app-dash.fxml":
                currentStg.setTitle("ACAS - Mechanic Dashboard");
                break;

            case "javafx-ACAS-app-admin-dash.fxml":
                currentStg.setTitle("ACAS - Administrator Dashboard");
                break;
            case "javafx-ACAS-app-customer-dash.fxml":
                currentStg.setTitle("ACAS - Customer Dashboard");
                break;
            case "javafx-ACAS-app-mechanic-part-compatibility.fxml":
                currentStg.setTitle("ACAS - Part Compatibility Checker");
                break;

            case "javafx-ACAS-app-part-compatibility.fxml":
                currentStg.setTitle("ACAS - Part Compatibility Checker");
                break;
            case "javafx-ACAS-app-mechanic-rate-application.fxml":
                currentStg.setTitle("ACAS - Rate Application");
                break;
            case "javafx-ACAS-app-rate-application.fxml":
                currentStg.setTitle("ACAS - Rate Application");
                break;

            case "javafx-ACAS-app-diagnostic.fxml":
                currentStg.setTitle("ACAS - Generate Diagnostic Report");
                break;

            case "javafx-ACAS-app-system-config.fxml":
                currentStg.setTitle("ACAS - Manage Diagnostic Rules");
                break;

            case "javafx-ACAS-app-view.fxml":
                currentStg.setTitle("ACAS - User Login");
                break;

            case"javafx-ACAS-app-customer-register.fxml":
                currentStg.setTitle("ACAS - Customer Registration");
                break;
            case"javafx-ACAS-app-mechanic-register.fxml":
                currentStg.setTitle("ACAS - Mechanic Registration");
                break;
            case"javafx-ACAS-app-view-issue-report.fxml":
                currentStg.setTitle("ACAS - Review Feedback");
                break;
            case"javafx-ACAS-app-view-reports.fxml":
                currentStg.setTitle("ACAS - View and Export Dianostic Report");
                break;
        }

        currentStg.getScene().setRoot(pane);
        currentStg.sizeToScene();

    }

    public static void main(String[] args) {
        launch();
    }
}