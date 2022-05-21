package com.confessit;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LogInController {

    public boolean validateLogin(String loginEmail, String loginPassword) {

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT * FROM user WHERE email = '" + loginEmail + "' AND password ='" + loginPassword + "'");
            // if the query result is not empty
            if (queryResult.next()) {
                String retrievedEmail = queryResult.getString("email");
                String retrievedPassword = queryResult.getString("password");
                if (retrievedEmail.equals(loginEmail) && retrievedPassword.equals(loginPassword)) {
                    // if email and password matches
                    // create a User object that stores the current user's information
                    User currentUser = new User();
                    currentUser.setUsername(queryResult.getString("username"));
                    currentUser.setEmail(retrievedEmail);
                    currentUser.setPassword(retrievedPassword);
                    currentUser.setDateOfBirth(queryResult.getDate("dateofbirth"));
                    currentUser.setDescription(queryResult.getString("description"));
                    currentUser.setRole(queryResult.getString("role"));

                    return true;

                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (queryResult != null) {
                try {
                    queryResult.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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
        return false;
    }
}
