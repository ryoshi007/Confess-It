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
            String sql = "INSERT INTO post (datetime, content, approval) VALUES (?,?,?,?)";
            PreparedStatement statement = connection.databaseLink.prepareStatement(sql);

            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            statement.setTimestamp(1, timestamp);
            statement.setString(2, content);
            statement.setBoolean(3, false);
            statement.setInt(4, retrieveNewTagID());
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
            String sql = "INSERT INTO post (datetime, content, picfilepath, approval, tagid) VALUES (?,?,?,?,?)";
            PreparedStatement statement = connection.databaseLink.prepareStatement(sql);
            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            statement.setTimestamp(1, timestamp);

            statement.setString(2, content);
            statement.setString(3, filePath);
            statement.setBoolean(4, false);
            statement.setInt(5, retrieveNewTagID());
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
     * An algorithm that will detect the similarity of the submission's content with existing one, if it exceeds
     * 80% of the similarity checker, the submission wil be rejected. Also, if the content is less than the length of 20,
     * the submission will be rejected. Lastly, if the content is a repetition for a letter or a word,
     * i.e self-similarity checker exceeds 60%, it will be rejected.
     * @param content the strings of the submission post
     * @return boolean value, true if content is non-spam, false otherwise.
     */
    public static boolean spamDetect(String content) {
        return false;
    }

    /***
     * Return the next tag id (latest tag id + 1) for the post
     * @return the next tag id
     * @throws SQLException
     */
    public int retrieveNewTagID() throws SQLException {

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT * FROM post ORDER BY tagid DESC LIMIT 1");

            if (queryResult.next()) {
                int retrievedTagID = queryResult.getInt("tagid");
                return retrievedTagID + 1;
            } else {
                return 1;
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
        return 1;
    }
}
