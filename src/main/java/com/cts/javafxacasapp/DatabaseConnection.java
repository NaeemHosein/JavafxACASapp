package com.cts.javafxacasapp;

/* DatabaseConnection.java
 * This class creates a database object to load drivers and perform queries.
 * Upon creation of an instance of this class a connection to the database is
 * established using a data source. From an instance of this class objects of
 * the Connection, Statement and ResultSet Interface classes can be obtained.
 * Provides the API for accessing and processing data stored in a
 * data source.

 * @Author (author)
 * @Version (a version number or a date)
 * @Date (date)
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
        createConnection();
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
}