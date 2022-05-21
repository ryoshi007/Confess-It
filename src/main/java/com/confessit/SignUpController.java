package com.confessit;

import com.mysql.cj.Query;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignUpController {

    public void createUserAccount(String email, String username, String password) {


        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();

            queryResult = statement.executeQuery("SELECT email FROM user WHERE email= '" + email + "'");
            if (queryResult.next()) {
                // If the email address is in use,
                System.out.println("The account is in use");
            } else {
                // If the email address is not in user
                statement = connectDB.createStatement();
                statement.executeUpdate("INSERT INTO user (username, email, password) VALUES ('" + username + "','" + email + "','" + password + "')");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connectDB != null) {
                try {
                    connectDB.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
