package com.confessit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * A class that used to implement tree graph data structure
 * @param <T> Integer (tag ID)
 */
public class TreeImplementation<T> {

    /**
     * Root of the tree
     */
    private TreeNode<T> root;

    /**
     * Set the root of the tree
     * @param tagID post tag ID
     */
    private void setRoot(T tagID) {
        root = new TreeNode<>(tagID);
    }

    /**
     * Return the root that contains a bunch of children
     * @param tagID is the tag id of the post id that wanted to be searched
     * @return root
     */
    public TreeNode<T> searchChildren(T tagID) {
        setRoot(tagID);
        root = searchChildrenHelper(root);
        return root;
    }

    /**
     * Helper of searchChildren method
     * Get all reply post tag IDs of a post (Get all children and grandchildren of a root)
     * @param parentTagID reply post tag ID of a post
     * @return TreeNode
     */
    @SuppressWarnings("unchecked")
    private TreeNode<T> searchChildrenHelper(TreeNode<T> parentTagID) {
        Json json = new Json();
        String stringReplyPostTagID = json.retrieveReplyPostTagID((Integer) parentTagID.getTagID());
        if (stringReplyPostTagID != null) {
            String[] storeReplyPostTagID = stringReplyPostTagID.replace("[", "").replace("]", "").replace("\"", "").replace(" ", "").split(",");

            for (String s : storeReplyPostTagID) {
                if (!s.isBlank()) {
                    TreeNode<T> children = new TreeNode<>((T) (Integer.valueOf(s)));
                    children.setParent(parentTagID);
                    searchChildrenHelper(children);
                    parentTagID.addChild(children);
                }
            }
        }
        return parentTagID;
    }

    /**
     * Print tag IDs (Print root, parents and children)
     */
    public void printChildren() {
        System.out.println("Root: " + root.getTagID() + "\n");
        for (int i = 0; i < root.getChildrenSize(); i++) {
            System.out.println("PARENT: " + root.getChildByIndex(i));
            printChildrenHelper(root.getChildByIndex(i));
            System.out.println();
        }
    }

    /**
     * Helper of printChildren method
     * @param parentTagID tag ID of a post
     */
    private void printChildrenHelper(TreeNode<T> parentTagID) {
        if (parentTagID.isEmptyChild()) {
            return;
        }

        for (int i = 0; i < parentTagID.getChildrenSize(); i++) {
            System.out.println("Parent: " + parentTagID.getTagID());
            System.out.println("Children: " + parentTagID.getChildByIndex(i));
            printChildrenHelper(parentTagID.getChildByIndex(i));
        }
    }

    /**
     * Delete tag ID of a post and its reply post tag IDs
     * @param tagIDToDelete tag ID of a post
     */
    public void deleteChildren(int tagIDToDelete) {
        deleteChildHelper1(root, tagIDToDelete);
        if ((Integer) root.getTagID() == tagIDToDelete) {
            root = null;
        }
    }

    /**
     * Helper of deleteChildren method
     * Check if the parentTagID and tagIDToDelete are the same
     * If yes, call deleteChildrenHelper2 method and break the connection of that tag ID with other tag IDs in the tree
     * @param parentTagID tag ID of a post
     * @param tagIDToDelete tag ID of a post
     */
    private void deleteChildHelper1(TreeNode<T> parentTagID, int tagIDToDelete) {
        if ((int) parentTagID.getTagID() == tagIDToDelete) {
            deleteChildrenHelper2(tagIDToDelete);
            Json json = new Json();
            if (parentTagID.getParent() == null) {
                json.deleteReplyPostTagID((int) parentTagID.getTagID(), tagIDToDelete);
            } else {
                json.deleteReplyPostTagID((int) parentTagID.getParent().getTagID(), tagIDToDelete);
            }
            parentTagID.setParent(null);
            parentTagID.removeAllChildren();
            return;
        }

        if (parentTagID.isEmptyChild()) {
            return;
        }

        for (int i = 0; i < parentTagID.getChildrenSize(); i++) {
            deleteChildHelper1(parentTagID.getChildByIndex(i), tagIDToDelete);
        }
    }

    /**
     * Helper of deleteChildrenHelper2 method
     * Delete a post tag ID in database
     * @param tagIDToDelete tag ID of a post
     */
    private void deleteChildrenHelper2(int tagIDToDelete) {

        String stringStoreTagID = "";
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT replyPosts AS tagID FROM post WHERE tagid ='" + tagIDToDelete + "'");
            while(queryResult.next()) {
                stringStoreTagID = queryResult.getString("tagID");
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

        if (stringStoreTagID != null) {

            String[] storeTagID = stringStoreTagID.replace("[", "").replace("]", "").replace("\"", "").replace(" ", "").split(",");


            for (String s : storeTagID) {
                if (!s.isBlank()) {
                    deleteChildrenHelper2(Integer.parseInt(s));
                }
            }
        }

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            statement.executeUpdate("DELETE FROM post WHERE tagid = '" + tagIDToDelete + "'");
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

        // Delete tag ID that user has saved in archive as well
        ArrayList<String> usernameList = new ArrayList<>();

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT username FROM user WHERE archive LIKE '%" + tagIDToDelete + "%'");
            while (queryResult.next()) {
                usernameList.add(queryResult.getString("username"));
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

        Json json = new Json();
        for (String username : usernameList) {
            json.deleteArchive(username,tagIDToDelete);
        }
    }

    /**
     * Get the root of a tree
     * @return return a TreeNode
     */
    public TreeNode<T> getRoot() {
        return root;
    }
}
