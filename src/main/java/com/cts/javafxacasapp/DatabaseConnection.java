package com.cts.javafxacasapp;

/* DatabaseConnection.java
 * This class creates a database object to load drivers and perform queries.
 * Upon creation of an instance of this class a connection to the database is
 * established using a data source. From an instance of this class objects of
 * the Connection, Statement and ResultSet Interface classes can be obtained.
 * Provides the API for accessing and processing data stored in a
 * data source.

 *Added Initialize Tables feature to create tables with the app
 * Added Populate Tables feature to add sample data to tables for testing purposes
 */

import java.sql.*;
import java.util.logging.Logger;


//connection to the MySQL Database
public class DatabaseConnection  {
    public PreparedStatement pst;
    public Connection conn;


    protected Statement stat = null;
    protected PreparedStatement ps = null;
    protected ResultSet rst = null;


    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());


    DatabaseConnection() {
        createConnection(); createTables(); populateTables();
    }

    //--------------------------------------------------------------------------
    /*
     *Connection information for the MYSQL database on server
     *@exception Exception:if no connection was found.
     */
    public void createConnection() {
        try {
            String JDBC_URL = "jdbc:mysql://localhost:3306/javafxACASapp";

            conn = DriverManager.getConnection(JDBC_URL, "root", "mysql");

            stat = conn.createStatement();

            System.out.println("Database connected successfully");

        } catch(SQLException e) {
            logger.severe("Database connectivity failed");
            e.printStackTrace();
        }
    }
    // attempting to create tables for database automatically upon startup
    public void createTables() {
        try {
            Statement sql = conn.createStatement();

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
                            code VARCHAR(5) NOT NULL,
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
                            phone_number VARCHAR(14) NOT NULL,
                            rating INT(1),
                            feedback TEXT)
                    """);

            // building vehicle owner table
            sql.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS tblvehicle_owner (
                            owner_id INT AUTO_INCREMENT PRIMARY KEY,
                            vehicle_id INT NOT NULL,
                            email VARCHAR(50) NOT NULL,
                            full_name VARCHAR(50) NOT NULL,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password VARCHAR(20) NOT NULL,
                            rating INT(1),
                            feedback TEXT,
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

    public void populateTables() {


        try {

            Statement sql = conn.createStatement();

            // ensuring that tables are empty (assuming that if one is then all will be)

            ResultSet rs = sql.executeQuery("SELECT 1 FROM tblvehicles LIMIT 1");


            if (rs.next()) { //rs.next will move the cursor onto the 1st row if it exists
                System.out.println("Table contains data. Skipping insertion.");
                return;
            }


            // populating vehicle table

            sql.executeUpdate("""
            INSERT INTO tblvehicles (vehicle_make, vehicle_model, engine_type, year) VALUES
            ('Toyota','Corolla','1.8L I4',2010),
            ('Toyota','Hilux','2.7L I4',2012),
            ('Toyota','RAV4','2.0L I4',2011),
            ('Toyota','Land Cruiser Prado','4.0L V6',2013),
            ('Nissan','Tiida','1.6L I4',2011),
            ('Nissan','Sylphy','1.8L I4',2009),
            ('Nissan','Navara','2.5L Diesel',2014),
            ('Nissan','X-Trail','2.0L I4',2012),
            ('Honda','Civic','1.8L I4',2010),
            ('Honda','Accord','2.4L I4',2007),
            ('Honda','CR-V','2.4L I4',2012),
            ('Honda','Fit','1.5L I4',2013),
            ('Mazda','3','2.0L I4',2011),
            ('Mazda','CX-5','2.0L I4',2014),
            ('Mazda','BT-50','3.0L Diesel',2010),
            ('Suzuki','Swift','1.5L I4',2008),
            ('Suzuki','Vitara','2.0L I4',2010),
            ('Hyundai','Elantra','1.8L I4',2015),
            ('Kia','Sportage','2.0L I4',2014),
            ('Ford','Ranger','3.0L V6',2006)
        """);

            // adding sample admin accounts
            sql.executeUpdate("""
            INSERT INTO tbladministrator (full_name, username, password) VALUES
            ('Test Admin','test_admin','12345678'),
            ('Abigail Garner','abbey4623','12345678'),
            ('Naeem Hosein','naeemhosein','12345678')
        """);

            // adding sample mechanic accounts
            sql.executeUpdate("""
            INSERT INTO tblmechanic 
            (full_name, username, password, email, business_name, years_experience, phone_number) VALUES
            ('Test Mechanic','test_mechanic','12345678','this is a test','Test Business',5,'123-4567'),
            ('Bob Builder','bobby123','bobbyfixes@123','bobthebuilder@gmail.com','AutoPro Solutions',8,'334-9873'),
            ('Jane Sookram','sjane33','password','quickfixrepairs@live.com','QuickFix Repairs',3,'488-9345')
        """);


            // creating 3 sample vehicle owners/ customers
            sql.executeUpdate("""
            INSERT INTO tblvehicle_owner
            (vehicle_id, full_name, username, password, email) VALUES
            (1, 'Test Owner','test_owner','12345678', 'this is a test'),
            (5,'Alice Sandiford','lizzy22','alices@2004', 'alicestandiford@gmail.com'),
            (10,'Charlie Peterson','starboypeterson','password', 'charlie2.0@hotmail.com')
        """);


            // inserting dtc code info into table (20 to start)
            sql.executeUpdate("""
            INSERT INTO tbldiagnostic_codes (code, description, resolution, faulty_part) VALUES
            ('P0101','Mass air flow sensor range issue','Clean or replace MAF sensor','MAF sensor'),
            ('P0108','MAP sensor high input','Replace MAP sensor','MAP sensor'),
            ('P0112','IAT sensor low input','Check wiring or replace','IAT sensor'),
            ('P0113','IAT sensor high input','Replace IAT sensor','IAT sensor'),
            ('P0117','ECT sensor low input','Check wiring','ECT sensor'),
            ('P0118','ECT sensor high input','Replace sensor','ECT sensor'),
            ('P0121','TPS range issue','Inspect TPS','Throttle position sensor'),
            ('P0122','TPS low input','Replace TPS','Throttle position sensor'),
            ('P0125','Coolant temp issue','Replace thermostat','Thermostat'),
            ('P0131','O2 sensor low voltage','Replace O2 sensor','Oxygen sensor'),
            ('P0138','O2 sensor high voltage','Replace O2 sensor','Oxygen sensor'),
            ('P0140','O2 no activity','Replace sensor','Oxygen sensor'),
            ('P0158','O2 sensor high voltage','Replace sensor','Oxygen sensor'),
            ('P0171','System too lean','Check vacuum leaks',NULL),
            ('P0175','System too rich','Check injectors',NULL),
            ('P0300','Engine misfire','Inspect plugs/coils',NULL),
            ('P0301','Cylinder 1 misfire','Replace spark plug','Spark plug'),
            ('P0302','Cylinder 2 misfire','Replace spark plug','Spark plug'),
            ('P0303','Cylinder 3 misfire','Replace spark plug','Spark plug'),
            ('P0304','Cylinder 4 misfire','Replace spark plug','Spark plug')
        """);

            //adding 50 parts to parts table
            sql.executeUpdate("""
                    INSERT INTO tblparts
                    (part_name, part_type, compatible_make, compatible_model, engine_type, year_min, year_max) VALUES
                    ('Denso 197-6020','MAF sensor','Toyota','Corolla','1.8L I4',2009,2013),
                    ('Hitachi MAF0031','MAF sensor','Nissan','Tiida','1.6L I4',2007,2012),
                    ('Delphi AF10043','MAF sensor','Chevrolet','Silverado 1500','5.3L V8',2007,2013),
                    ('Bosch 0281006059','MAP sensor','Hyundai','Elantra','1.8L I4',2011,2016),
                    ('Denso 079800-4250','MAP sensor','Honda','Civic','1.8L I4',2006,2011),
                    ('NGK BKR5E','Spark Plug','ANY','ANY','ANY',1996,2026),
                    ('NGK IFR6T11','Spark Plug','Toyota','Hilux','2.7L I4',2005,2015),
                    ('NGK LFR6AIX-11','Spark Plug','Nissan','X-Trail','2.0L I4',2008,2014),
                    ('Bosch 0280130092','IAT sensor','Mazda','3','2.0L I4',2006,2013),
                    ('Denso 129700-4510','IAT sensor','Suzuki','Swift','1.5L I4',2005,2010),
                    ('Denso 129700-4511','ECT sensor','Toyota','RAV4','2.0L I4',2006,2012),
                    ('Standard Motor TX73','ECT sensor','Nissan','Sylphy','1.8L I4',2005,2010),
                    ('Bosch 0280122001','Throttle Position Sensor','Honda','Accord','2.4L I4',2003,2007),
                    ('Denso 89452-35020','Throttle Position Sensor','Toyota','Prado','4.0L V6',2006,2012),
                    ('Denso 234-9009','Oxygen Sensor','Subaru','Forester','2.5L H4',2005,2010),
                    ('NGK 21353','Oxygen Sensor','Hyundai','Tucson','2.0L I4',2010,2015),
                    ('NGK 24502','Oxygen Sensor','Toyota','Hilux','2.7L I4',2005,2015),
                    ('Gates Thermostat 33928','Thermostat','Nissan','Navara','2.5L I4 Diesel',2008,2014),
                    ('Stant Thermostat 14279','Thermostat','Honda','CR-V','2.4L I4',2007,2012),
                    ('Bosch Wiper Blade 22OE','Wiper Blade','ANY','ANY','ANY',1990,2026),
                    ('Interstate MT-24F Battery','Battery','ANY','ANY','ANY',1996,2026),
                    ('Bosch Oil Filter 3330','Oil Filter','ANY','ANY','ANY',1996,2026),
                    ('Bosch Fuel Pump 67658','Fuel Pump','Ford','Ranger','3.0L V6',2000,2006),
                    ('NGK Ignition Coil 49126','Ignition Coil','Kia','Sportage','2.0L I4',2010,2015),
                    ('NGK Ignition Coil 48894','Ignition Coil','Mazda','CX-5','2.0L I4',2012,2016),
                    ('ACDelco Ignition Coil D581','Ignition Coil','Chevrolet','Tahoe','5.3L V8',2000,2006),
                    ('Bosch Universal OBD-II Scanner','Diagnostic Tool','ANY','ANY','ANY',1996,2026),
                    ('Gates Serpentine Belt K060841','Belt','ANY','ANY','ANY',1990,2026),
                    ('Denso Cabin Air Filter 453-1014','Cabin Air Filter','Honda','Civic','1.8L I4',2006,2011),
                    ('NGK Spark Plug LZFR6AP11','Spark Plug','Chrysler','300','3.5L V6',2005,2010),
                    ('NGK Spark Plug IFR5A11','Spark Plug','Suzuki','Vitara','2.0L I4',2005,2010),
                    ('Bosch MAP Sensor 0261230240','MAP sensor','Kia','Rio','1.6L I4',2012,2016),
                    ('Denso MAF Sensor 197-6021','MAF sensor','Hyundai','Accent','1.6L I4',2012,2016),
                    ('NGK Oxygen Sensor 21348','Oxygen Sensor','Nissan','Note','1.5L I4',2005,2010),
                    ('Bosch Thermostat 4009','Thermostat','Mazda','BT-50','3.0L Diesel',2007,2012),
                    ('NGK Spark Plug IFR6T11','Spark Plug','Toyota','Yaris','1.3L I4',2006,2011),
                    ('Bosch IAT Sensor 0280130093','IAT sensor','Ford','Focus','2.0L I4',2000,2004),
                    ('Denso ECT Sensor 129700-4512','ECT sensor','Kia','Cerato','1.6L I4',2010,2015),
                    ('Bosch TPS Sensor 0280122002','Throttle Position Sensor','Suzuki','Jimny','1.3L I4',2005,2010),
                    ('NGK Spark Plug IFR6T11','Spark Plug','Honda','Fit','1.5L I4',2007,2013),
                    ('NGK Spark Plug LFR6AIX-11','Spark Plug','Nissan','Sylphy','1.8L I4',2005,2010),
                    ('NGK Spark Plug IFR5A11','Spark Plug','Toyota','Land Cruiser Prado','4.0L V6',2006,2012),
                    ('Bosch Oxygen Sensor 0258006028','Oxygen Sensor','Kia','Sportage','2.0L I4',2010,2015),
                    ('NGK Oxygen Sensor 21353','Oxygen Sensor','Mazda','3','2.0L I4',2006,2013),
                    ('Bosch Thermostat 3339','Thermostat','Hyundai','Tucson','2.0L I4',2010,2015),
                    ('NGK Spark Plug IFR6T11','Spark Plug','Ford','F-150','5.4L V8',2004,2008),
                    ('Bosch MAP Sensor 0261230241','MAP sensor','Chevrolet','Cruze','1.8L I4',2011,2015),
                    ('Denso MAF Sensor 197-6022','MAF sensor','Suzuki','Vitara','2.0L I4',2005,2015);
        """);


            logger.info("Tables populated successfully: " +
                    "20 vehicles successfully inserted." +
                    "3 sample admin accounts successfully inserted." +
                    "3 sample mechanics successfully inserted." +
                    "3 sample vehicle owners successfully inserted." +
                    "20 DTC codes successfully inserted." +
                    "50 vehicle parts successfully inserted."
            );

        } catch (SQLException e) {
            logger.severe("Error inserting sample data: " + e.getMessage());
        }
    }

}