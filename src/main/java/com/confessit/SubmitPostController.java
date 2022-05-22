package com.confessit;

import javafx.fxml.FXML;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SubmitPostController {

    /***
     * Submit a post without picture for approval
     * @param content is the content from the submitted post
     */
    public void submitPost(String content) {
        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "INSERT INTO post (datetime, content, approval) VALUES (?,?,?)";
            PreparedStatement statement = connection.databaseLink.prepareStatement(sql);

            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            statement.setTimestamp(1, timestamp);
            statement.setString(2, content);
            statement.setBoolean(3, false);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (connectDB != null) {
                try {
                    connectDB.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     * Submit a post with picture for approval
     * @param content is the content from the submitted post
     * @param filePath is the file path that directs to the pictures
     */
    public void submitPost(String content, String filePath) {
        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "INSERT INTO post (datetime, content, picfilepath, approval) VALUES (?,?,?,?)";
            PreparedStatement statement = connection.databaseLink.prepareStatement(sql);

            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            statement.setTimestamp(1, timestamp);

            statement.setString(2, content);
            statement.setString(3, filePath);
            statement.setBoolean(4, false);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
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
