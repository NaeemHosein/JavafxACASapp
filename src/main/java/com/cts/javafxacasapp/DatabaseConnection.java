package com.cts.javafxacasapp;

/* DatabaseConnection.java
 * This class creates a database object to load drivers and perform queries.
 * Upon creation of an instance of this class a connection to the database is
 * established using a data source. From an instance of this class objects of
 * the Connection, Statement and ResultSet Interface classes can be obtained.
 * Provides the API for accessing and processing data stored in a
 * data source.

 *Added Initialize Tables feature to create tables with the app
 */

import java.sql.*;
import java.util.logging.Logger;


//connection to the MySQL Database
public class DatabaseConnection  {
    public PreparedStatement pst;
    public Connection conn;

    /*
     * Create a Connection object to the Database
     * Protected keyword in Java refers to one of its access modifiers.
     * The methods or data members declared as protected can be accessed from:
     *  1. Within the same class
     *  2. Subclasses of same packages
     *  3. Different classes of same packages
     *  4. Subclasses of different packages
     */

    protected Connection con = null;

    //--------------------------------------------------------------------------
    /*
     * An object used for executing a static SQL statement and returning the
     * results it produces.
     */
    protected Statement stat = null;

    //--------------------------------------------------------------------------
    /*
     * An object used for executing a static SQL statement and returning the
     * results it produces.
     */
    protected PreparedStatement ps = null;

    //--------------------------------------------------------------------------
    /*
     * An object that maintains a cursor pointing to its current row of data
     */
    protected ResultSet rst = null;

    //--------------------------------------------------------------------------
    /*
     * This constructor connects to a MySQL database. It creates instances
     * of the Statements and ResultSet classes to be used by other classes.
     */

    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());


    DatabaseConnection() {
        createConnection(); initializeDatabase();
    }

    //--------------------------------------------------------------------------
    /*
     *Connection information for the MYSQL database on server
     *@exception Exception:if no connection was found.
     */
    public void createConnection() {
        try {

            /* Returns a database connection from the currently active connection provider */
            //--------------------------------------------------------------------------
            //JavaFXProjectDemo is the name of our database
            String JDBC_URL = "JDBC:mysql://localhost:3306/javafxACASapp";
            con = DriverManager.getConnection(JDBC_URL, "root", "mysql");

            /*
             * Creates a Statement object that will generate ResultSet objects with the given
             * type and concurrency.
             */
            stat = con.createStatement();
            //ps = con.prepareStatement("");
        }
        catch(SQLException e) {
            // Log the exception using the Java logger
            logger.severe("An error occurred: Database connectivity failed");
            logger.severe(e.toString());
            System.exit(0);
        }
    }

    // attempting to create tables for database automatically upon startup
        public void initializeDatabase() {
                try {
                    Statement sql = con.createStatement();

                    // building admin table
                    sql.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS tbladministrator (
                                admin_id INT AUTO_INCREMENT PRIMARY KEY,
                                full_name VARCHAR(50) NOT NULL,
                                username VARCHAR(50) NOT NULL UNIQUE,
                                password VARCHAR(20) NOT NULL
                            )
                            """);
                    // building vehicle table
                    sql.executeUpdate(""" 
                            CREATE TABLE IF NOT EXISTS tblvehicles (
                            vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
                            vehicle_make VARCHAR(50) NOT NULL,
                            vehicle_model VARCHAR(50) NOT NULL,
                            engine_type VARCHAR(50) NOT NULL,
                            year INT NOT NULL)
                            """);

                    // building parts table
                    sql.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS tblparts (
                            part_id INT AUTO_INCREMENT PRIMARY KEY,
                            part_name VARCHAR(100) NOT NULL,
                            part_type VARCHAR(50) NOT NULL,
                            compatible_make VARCHAR(50),
                            compatible_model VARCHAR(50),
                            engine_type VARCHAR(50),
                            year_min INT NOT NULL,
                            year_max INT NOT NULL)
                    """);

                    // building dtc table
                    sql.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS tbldiagnostic_codes (
                            code_id INT AUTO_INCREMENT PRIMARY KEY,
                            code INT NOT NULL,
                            description TEXT NOT NULL,
                            resolution TEXT NOT NULL,
                            faulty_part VARCHAR(50) )
                    """);

                    // building mechanic table
                    sql.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS tblmechanic (
                            mechanic_id INT AUTO_INCREMENT PRIMARY KEY,
                            full_name VARCHAR(50) NOT NULL,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password VARCHAR(20) NOT NULL,
                            email VARCHAR(50) NOT NULL,
                            business_name VARCHAR(50),
                            years_experience INT NOT NULL,
                            phone_number VARCHAR(14) NOT NULL)
                    """);

                    // building vehicle owner table
                    sql.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS tblvehicle_owner (
                            owner_id INT AUTO_INCREMENT PRIMARY KEY,
                            vehicle_id INT NOT NULL,
                            full_name VARCHAR(50) NOT NULL,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password VARCHAR(20) NOT NULL,
                            FOREIGN KEY (vehicle_id) REFERENCES tblvehicles(vehicle_id)
                            )
                            """);

                    // Diagnostic Reports Table
                    sql.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS tbldiagnostic_reports (
                            report_id INT AUTO_INCREMENT PRIMARY KEY,
                            mechanic_id INT NOT NULL,
                            code_id INT NOT NULL,
                            vehicle_id INT NOT NULL,
                            feedback TEXT,
                            flag INT NOT NULL,
                            report_date DATETIME NOT NULL,
                            FOREIGN KEY (mechanic_id) REFERENCES tblmechanic(mechanic_id),
                            FOREIGN KEY (code_id) REFERENCES tbldiagnostic_codes(code_id),
                            FOREIGN KEY (vehicle_id) REFERENCES tblvehicles(vehicle_id)
                            )
                    """);

                    logger.info("Database tables created successfully.");

                } catch (SQLException e) {
                    logger.severe("Error creating tables: " + e.getMessage());
                }
            }
}

