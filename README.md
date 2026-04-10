

# **Welcome to ACAS\!**

ACAS is a JavaFX-based desktop application designed to assist mechanics and users in diagnosing vehicle issues and recommending compatible components. The system allows users to generate diagnostic reports, manage vehicle and parts data, and view diagnostic trouble codes (DTCs) with suggested resolutions.

This document contains all the instructions you need to set up your machine before running our app. For questions based on system functions, please check out the user manuals on our mechanic and customer dashboards or navigate to the /docs folder of this project.

# **Testing Environment**

This app was created and tested on a Windows 11 machine using JDK 23, IntelliJ IDEA Community Edition 2024.3.2.1 and MySQL Workbench 8.0 CE.

# **Setting up ACAS on your machine**

## Step 1: Install necessary components:

Install JDK (23 recommended)  
Install IDE (IntelliJ IDEA Community Edition 2024.3.2.1 recommended)  
Install MySQL WorkBench 8.0 CE (Ensure that the MySQL program path is added to your system Environment Variables)

## Step 2: Setup new MySQL server “javafxacasapp”

* Open MySQL and set up a new connection with the properties below:  
  Connection Name:	javafxacasapp  
  Connection Method:	TCP/IP  
  Hostname:		127.0.0.1  
  Port:			3306   
  Username:		root  
  Default Schema   
   Password: 		mysql   
* In MySQL Workbench, run the command:” CREATE DATABASE javafxacasapp;” to create a schema (Database) for the application. When the project is run in Step 4 the  application will automatically create and populate the necessary tables.

        

## Step 3: Open this project in your IDE as a Maven project and add sql connector as a dependency

* Open ACAS project in IntelliJ as a maven project   
* Navigate to File \> Project Structure\>  Modules \> Dependencies \>Module SDK  
* Tap Add Dependency or the “+”  
* Navigate to the lib folder within the ACAS Project Folder and select the mysql-connector-j-9-2.0.jar file  
* Click OK \> Click Apply  \> Click OK

## Step 4: Open the application by running the Main Class “JavafxACASapp.java”

Select run in your IDE within the JavafxACASapp.java file. For IntelliJ tap the Run/Play button when you open this project file.

If the play button fails, tuse the Maven Tool Window on the right side of IntelliJ.  
Navigate to Plugins \> javafx \> javafx:run and double-click it.

# **Error handling**

Please note that if you fail to add the sql connector as a dependency when you run the application you will see an SQLEXCEPTION error: “No suitable driver found for JDBC mysql://local host: 3306/javafxacasapp”. Please follow the steps in Step 3 to add the connector and then proceed to run the application.

