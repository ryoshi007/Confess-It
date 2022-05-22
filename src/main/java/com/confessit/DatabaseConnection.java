package com.confessit;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This class is used to connect to the database
 */
public class DatabaseConnection {

    /**
     * Connection is the session between java application and database
     */
    public Connection databaseLink;

    /**
     * This method is used to get connection from the database
     *
     * @return A database link
     */
    public Connection getConnection() {
        String databaseUser = "root";
        String databasePassword = "Yjsh2027";
        String url = "jdbc:mysql://localhost:3306/confessit";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;
    }
}