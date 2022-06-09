package com.confessit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class Json {

    /**
     * Add a reply post tag ID of a post to the database
     * @param tagID tag ID of a post
     * @param replyPostTagID tag ID of a reply post
     */
    public void insertReplyPostTagID(int tagID, int replyPostTagID) {
        Connection connectDB = null;
        Statement statement = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            statement.executeUpdate("UPDATE post SET replyPosts = JSON_ARRAY_APPEND(replyPosts, '$', '" + replyPostTagID + "') WHERE tagid = '" + tagID + "'");
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

    /**
     * Retrieve reply post tag IDs of a post
     * @param tagID tag ID of a post
     * @return A list containing tag ID of reply posts
     */
    public String retrieveReplyPostTagID(int tagID) {
        String storeTagID = "";
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT replyPosts AS replyPostTagID FROM post WHERE tagid ='" + tagID + "'");
            while (queryResult.next()) {
                storeTagID = queryResult.getString("replyPostTagID");
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
        return storeTagID;
    }

    /**
     * Delete a reply post tag ID of a post
     * @param tagID tag ID of a post
     * @param tagToDelete tag ID of a reply post
     */
    public void deleteReplyPostTagID(int tagID, int tagToDelete) {
        Connection connectDB = null;
        Statement statement = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            statement.executeUpdate("UPDATE post SET replyPosts = JSON_REMOVE(replyPosts, replace(JSON_SEARCH(replyPosts, 'one','" + tagToDelete + "'), '\"', '')) WHERE tagid = '" + tagID + "'");
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

    /**
     * Add user comment to a post
     * @param tagID tag ID of a post
     * @param username user's username
     * @param comment  user's comment
     */
    public void addUserComment(int tagID, String username, String comment) {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;
        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT comment FROM post WHERE tagid = '" + tagID + "'");
            if (queryResult.next()) {
                if (queryResult.getString("comment") == null) {
                    statement.executeUpdate("UPDATE post SET comment = '[]' where tagid = '" + tagID + "'");
                    statement.executeUpdate("UPDATE post SET comment = JSON_ARRAY_APPEND(comment, '$', '[" + username + "," + comment + "]') WHERE tagid = '" + tagID + "'");
                } else {
                    statement.executeUpdate("UPDATE post SET comment = JSON_ARRAY_APPEND(comment, '$', '[" + username + "," + comment + "]') WHERE tagid = '" + tagID + "'");
                }
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

    /**
     * Retrieve a user's username and comment in a post
     * @param tagID tag ID of a post
     * @return return a String that contains user's username and comment
     */
    public String retrieveUserComment(int tagID) {
        String storeUserComment = "";

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT comment AS COMMENT FROM post WHERE tagid ='" + tagID + "'");
            while (queryResult.next()) {
                storeUserComment = queryResult.getString("COMMENT");
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
        return storeUserComment;
    }

    /**
     * Delete a user's comment of a post
     * @param tagID tag ID of a post
     * @param username user's username
     * @param comment  user's comment
     */
    public void deleteUserComment(int tagID, String username, String comment) {

        Connection connectDB = null;
        Statement statement = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            statement.executeUpdate("UPDATE post SET comment = JSON_REMOVE(comment, replace(JSON_SEARCH(comment, 'one', '[" + username + "," + comment + "]'), '\"', '')) WHERE tagid = '" + tagID + "'");
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

    /**
     * Add a new tag ID to archive
     * @param tagID tag ID of a post
     * @param username username of a user
     */
    public void addToArchive(int tagID, String username) {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT archive FROM user WHERE username = '" + username + "'");
            if (queryResult.next()) {
                if (queryResult.getString("archive") == null) {
                    statement.executeUpdate("UPDATE user SET archive = '[]' where username = '" + username + "'");
                }
                statement.executeUpdate("UPDATE user SET archive = JSON_ARRAY_APPEND(archive, '$', '" + tagID + "') WHERE username = '" + username + "'");
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

    /**
     * Retrieve tag IDs from archive
     * @param username username of a user
     * @return A String containing a list of tag IDs
     */
    public String retrieveFromArchive(String username) {
        String storeUserComment = null;

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT archive FROM user WHERE username ='" + username + "'");
            if (queryResult.next()) {
                storeUserComment = queryResult.getString("archive");
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
        return storeUserComment;
    }

    /**
     * Delete tag ID of a post in the archive
     * @param username username of a user
     * @param tagID tag ID to be deleted
     */
    public void deleteArchive(String username, int tagID) {
        Connection connectDB = null;
        Statement statement = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            statement.executeUpdate("UPDATE user SET archive = JSON_REMOVE(archive, replace(JSON_SEARCH(archive, 'one','" + tagID + "'), '\"', '')) WHERE username = '" + username + "'");
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