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

    /**
     * Increment number of likes by 1
     * Store the username of a user who likes a specific post
     * @param tagID tag ID of a post
     * @param username username of a user
     */
    public void addLike(int tagID, String username) {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT likeNum, likeUser FROM post WHERE tagid = '" + tagID + "'");
            if (queryResult.next()) {
                int likeNum = Integer.parseInt(queryResult.getString("likeNum")) + 1;
                if (queryResult.getString("likeUser") == null) {
                    statement.executeUpdate("UPDATE post SET likeUser = '[]' where tagid = '" + tagID + "'");
                }
                statement.executeUpdate("UPDATE post SET likeNum = '" + likeNum + "' WHERE tagid = '" + tagID + "'");
                statement.executeUpdate("UPDATE post SET likeUser = JSON_ARRAY_APPEND(likeUser, '$', '" + username + "') WHERE tagid = '" + tagID + "'");
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
     * Retrieve username of users who like a specific post
     * @param tagID tag ID of a post
     * @return A string containing usernames
     */
    public String retrieveLikeUser(int  tagID) {
        String storeLikeUser = null;

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT likeUser FROM post WHERE tagid ='" + tagID + "'");
            if (queryResult.next()) {
                storeLikeUser = queryResult.getString("likeUser");
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
        return storeLikeUser;
    }

    /**
     * Decrement number of like by 1
     * Delete the username of a user who don't like a specific post anymore
     * @param tagID tag ID of a post
     * @param username username of a user
     */
    public void deleteLike(int tagID, String username) {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT likeNum FROM post WHERE tagid = '" + tagID + "'");
            if (queryResult.next()) {
                int likeNum = Integer.parseInt(queryResult.getString("likeNum")) - 1;
                statement.executeUpdate("UPDATE post SET likeUser = JSON_REMOVE(likeUser, replace(JSON_SEARCH(likeUser, 'one','" + username + "'), '\"', '')) WHERE tagid = '" + tagID + "'");
                statement.executeUpdate("UPDATE post SET likeNum = '" + likeNum + "' WHERE tagid = '" + tagID + "'");
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
     * Increment number of dislike by 1
     * Store the username of a user who dislikes a specific post
     * @param tagID tag ID of a post
     * @param username username of a user
     */
    public void addDislike(int tagID, String username) {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT dislikeNum, dislikeUser FROM post WHERE tagid = '" + tagID + "'");
            if (queryResult.next()) {
                int dislikeNum = Integer.parseInt(queryResult.getString("dislikeNum")) + 1;
                if (queryResult.getString("dislikeUser") == null) {
                    statement.executeUpdate("UPDATE post SET dislikeUser = '[]' where tagid = '" + tagID + "'");
                }
                statement.executeUpdate("UPDATE post SET dislikeNum = '" + dislikeNum + "' WHERE tagid = '" + tagID + "'");
                statement.executeUpdate("UPDATE post SET dislikeUser = JSON_ARRAY_APPEND(dislikeUser, '$', '" + username + "') WHERE tagid = '" + tagID + "'");
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
     * Retrieve username of users who dislike a specific post
     * @param tagID tag ID of a post
     * @return A string containing usernames
     */
    public String retrieveDislikeUser(int  tagID) {
        String storeLikeUser = null;

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT dislikeUser FROM post WHERE tagid ='" + tagID + "'");
            if (queryResult.next()) {
                storeLikeUser = queryResult.getString("dislikeUser");
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
        return storeLikeUser;
    }

    /**
     * Decrement number of dislikes by 1
     * Delete the username of a user who don't dislike a specific post anymore
     * @param tagID tag ID of a post
     * @param username username of a user
     */
    public void deleteDislike(int tagID, String username) {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT dislikeNum FROM post WHERE tagid = '" + tagID + "'");
            if (queryResult.next()) {
                int dislikeNum = Integer.parseInt(queryResult.getString("dislikeNum")) - 1;
                statement.executeUpdate("UPDATE post SET dislikeUser = JSON_REMOVE(dislikeUser, replace(JSON_SEARCH(dislikeUser, 'one','" + username + "'), '\"', '')) WHERE tagid = '" + tagID + "'");
                statement.executeUpdate("UPDATE post SET dislikeNum = '" + dislikeNum + "' WHERE tagid = '" + tagID + "'");
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