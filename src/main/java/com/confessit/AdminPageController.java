package com.confessit;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class AdminPageController {

    /***
     * To display the submitted posts
     */
    public void display() {
        ArrayList<Post> pendingPost = retrieveSubmittedPost();
        if (pendingPost.isEmpty()) {
            //Display no result
        } else {

        }
    }

    public ArrayList<Post> retrieveSubmittedPost() {
        ArrayList<Post> postList = new ArrayList<>();
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "SELECT * FROM post WHERE approval = 0";
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

                Post newPost = null;
                if (filePath == null) {
                    newPost = new Post(index, tagID, datetime, content, like, dislike, comment, approval);
                } else {
                    newPost = new Post(index, tagID, datetime, content, like, dislike, comment, approval);
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

    /***
     * To approve the submitted post by changing the approval status to 1 (True) and assign its tag id
     * @param index is the index of the submitted post
     */
    public void approve(int index) {
        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "UPDATE post SET approval = ?, likeNum = ?, dislikeNum = ?, tagid = ? WHERE queryIndex = ?";
            PreparedStatement statement = connection.databaseLink.prepareStatement(sql);

            statement.setInt(1, 1);
            statement.setInt(2, 0);
            statement.setInt(3, 0);
            statement.setInt(4, retrieveNewTagID());
            statement.setInt(5, index);

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
     * To delete the submitted post
     * @param index is the index of the submitted post
     */
    public void delete(int index) {
        Connection connectDB = null;
        Statement statement = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "DELETE FROM post WHERE queryIndex='" + index + "'";
            statement.executeUpdate(sql);
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