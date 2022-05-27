package com.confessit;

import javafx.fxml.FXML;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import java.sql.*;
import java.util.*;

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

    /***
     * A method consists of serial steps to determine whether the content is meaningful and not a spam.
     * The first if statement checks whether the length of string exceed 25 or not.
     * The second if statement checks for the entropy score of the content.
     * Basically if the entropy score is lower than 2.75, the content is classified as a spam.
     * The third if statement checks whether there is a similar content that has been posted before.
     * @param content the strings of the submission post
     * @return boolean value, true if content is non-spam and meaning, false otherwise.
     */
    public boolean detectSpam(String content) {
        if (content.length() < 25) {
            return false;
        }
        if (calculateEntropy(content) < 2.75) {
            return false;
        }
        return !isSimilar(content);
    }

    /***
     * Retrieve contents of each approved post in an array list of string
     * @return string array that consists of contents of each approved post
     */
    private ArrayList<String> retrieveContent() {
        ArrayList<String> contentList = new ArrayList<>();
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "SELECT * FROM post WHERE approval = 1 ORDER BY tagid";
            queryResult = statement.executeQuery(sql);

            while(queryResult.next()) {
                String content = queryResult.getString("content");
                contentList.add(content);
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
        return contentList;
    }

    /***
     * This is a method that will check the repetition of each string in a sentence and give score based on it.
     * The method applies the concept of Shannon's Entropy - a measure of uncertainty associated with random variables.
     * The more randomness the sentence is, the higher the score will be returned.
     * Example = "aaaaaaaaaaaaaaaaaaaaaa" will return 0, but a legit sentence will give score higher than 2.5
     * @return the entropy score of the sentence
     */
    private double calculateEntropy(String content) {
        String[] split = content.split(" ");
        List<String> values = List.of(split);
        Map<String, Integer> map = new HashMap<>();

        // count the occurrences of each value
        for (String sequence : values) {
            if (!map.containsKey(sequence)) {
                map.put(sequence, 0);
            }
            map.put(sequence, map.get(sequence) + 1);
        }

        // calculate the entropy
        double result = 0.0;
        for (String sequence : map.keySet()) {
            double frequency = (double) map.get(sequence) / values.size();
            result -= frequency * (Math.log(frequency) / Math.log(2));
        }

        return result;
    }

    /***
     * This is a method that will compare the submitted content with the contents from approved posts.
     * It utilises FuzzySearch library from me.xdrop.fuzzywuzzy repository.
     * weightedRatio will compare two strings and return score above 90 if both strings have highly-similar contents.
     * @param content is the string of the submitted post
     * @return true if the similarity score is above 90, else false
     */
    private boolean isSimilar(String content) {
        ArrayList<String> contentsFromOthers = retrieveContent();
        int score;
        for (String contentsFromOther : contentsFromOthers) {
            score = FuzzySearch.weightedRatio(contentsFromOther, content);
            if (score > 90) {
                return true;
            }
        }
        return false;
    }

}
