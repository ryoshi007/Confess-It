package com.confessit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class SearchPost {

    /***
     * Search post based on selected category and given input
     * @param columnName the category selected by the user
     * @param value the user-inputted value
     * @return an array list of posts that consists of the value, specified by the column name
     */
    public ArrayList<Post> search(String columnName, String value) {
        ArrayList<Post> postList = new ArrayList<>();
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();

            String sql = "";
            if (columnName.equalsIgnoreCase("tagID")) {
                int convertedValue = Integer.valueOf(value);
                sql = "SELECT * FROM post WHERE tagID = '"+ convertedValue + "'";
            } else {
                sql = "SELECT * FROM post WHERE " +  columnName + " LIKE '%"+ value + "%'";
            }

            queryResult = statement.executeQuery(sql);

            while(queryResult.next()) {
                int index = queryResult.getInt("queryIndex");
                int tagID = queryResult.getInt("tagid");
                Date datetime = queryResult.getTimestamp("datetime");
                String content = queryResult.getString("content");
                String filePath = queryResult.getString("picfilepath");
                int like = queryResult.getInt("likeNum");
                int dislike = queryResult.getInt("dislikeNum");

                //Implement a method that can convert comment String into Json type
                String commentString = queryResult.getString("comment");
                Json comment = null;

                boolean approval = queryResult.getBoolean("approval");
                Date approvalTime = queryResult.getTimestamp("approvalTime");
                boolean displayStatus = queryResult.getBoolean("displayStatus");

                String replyString = queryResult.getString("replyPosts");
                Json reply = null;

                Post newPost = null;
                if (filePath == null) {
                    newPost = new Post(index, tagID, datetime, content, like, dislike, comment, approval,
                            approvalTime, displayStatus, reply);
                } else {
                    newPost = new Post(index, tagID, datetime, content, like, dislike, comment, approval,
                            approvalTime, displayStatus, reply);
                }
                postList.add(newPost);
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
        return postList;
    }

}
