package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.*;
import java.util.ArrayList;

/**
 * A controller for Batch-Removal-Page
 */
public class BatchRemovalController {

    /**
     * A button that used to delete a post
     */
    @FXML
    private Button adminDeleteButton;

    /**
     * A button that used to search a post tag ID
     */
    @FXML
    private Button adminSearchButton;

    /**
     * A text field that used to let admin enter a tag ID
     */
    @FXML
    private TextField adminSearchField;

    /**
     * A tree view that used to display a list of tag IDs
     */
    @FXML
    private TreeView<Integer> tagIDTreeView;

    /**
     * A label that used to display the tag ID to be deleted
     */
    @FXML
    private Label tagIDToBeDeleted;

    /**
     * An instance of Tree Implementation
     */
    private TreeImplementation<Integer> tree = new TreeImplementation<>();

    /**
     * Display a list of tag ID in tree view
     * @param tree An instance of TreeImplementation
     */
    public void setTagIDTreeView(TreeImplementation<Integer> tree) {
        if (tree.getRoot() != null) {
            TreeNode<Integer> root = tree.getRoot();
            TreeItem<Integer> rootItem = new TreeItem<>(root.getTagID());

            ArrayList<TreeItem<Integer>> itemList = new ArrayList<>();


            for (int i = 0; i < root.getChildrenSize(); i++) {
                itemList.add(new TreeItem<>(root.getChildByIndex(i).getTagID()));
                getAllChildrenTagID(root.getChildByIndex(i), itemList.get(i));
            }

            rootItem.getChildren().setAll(itemList);
            rootItem.setExpanded(true);
            tagIDTreeView.setRoot(rootItem);
        } else {
            tagIDTreeView.setRoot(null);
        }
    }

    /**
     * Get all the sub-post tag IDs of a post
     * @param root An instance of TreeNode
     * @param treeItemRoot An instance of TreeItem
     */
    private void getAllChildrenTagID (TreeNode<Integer> root, TreeItem<Integer> treeItemRoot) {
        if (root == null) {
            return;
        }

        for (int i = 0; i < root.getChildrenSize(); i++) {
            TreeItem<Integer> temp = new TreeItem<>(root.getChildByIndex(i).getTagID());
            treeItemRoot.getChildren().add(temp);
            getAllChildrenTagID(root.getChildByIndex(i),temp);
        }
    }

    /**
     * Select a tag ID from tree view
     * @param event Mouse click
     */
    @FXML
    void selectTagToBeDeleted(MouseEvent event) {
        TreeItem<Integer> item = tagIDTreeView.getSelectionModel().getSelectedItem();

        if (item != null) {
            tagIDToBeDeleted.setText(String.valueOf(item.getValue()));
        }
    }

    /**
     * Delete a selected tag ID from tree view
     * @param event Mouse click
     */
    @FXML
    void adminDeleteTag(ActionEvent event) {
        if (tagIDToBeDeleted.getText() != null && !tagIDToBeDeleted.getText().isBlank() && !tagIDToBeDeleted.getText().equals("xxxx")) {
            tree.deleteChildren(Integer.parseInt(tagIDToBeDeleted.getText()));

            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Success");
            alert.setHeaderText("Delete successful");
            alert.setContentText("Selected post with its sub-posts have been deleted.");
            alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
            alert.showAndWait();

            if (checkPostTagID(Integer.parseInt(adminSearchField.getText().replace("UM","")))) {
                tree.searchChildren(Integer.parseInt(adminSearchField.getText().replace("UM","")));
            }
            setTagIDTreeView(tree);
        } else {
            // Pop up a message
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid post tag ID");
            alert.setContentText("Please choose a post tag ID to delete.");
            alert.showAndWait();
        }
    }

    /**
     * Search a post tag ID from database
     * @param event Mouse click
     */
    @FXML
    void adminSearchPost(ActionEvent event) {
            if (adminSearchField.getText().contains("UM")) {
                if (checkPostTagID(Integer.parseInt(adminSearchField.getText().replace("UM", "")))) {
                    tree.searchChildren(Integer.valueOf(adminSearchField.getText().replace("UM", "")));
                    setTagIDTreeView(tree);
                } else {
                    // Pop up a message
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid post tag ID");
                    alert.setContentText("Please enter an existing post tag ID.");
                    alert.showAndWait();
                }
            } else {
            // Pop up a message
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid post tag ID");
            alert.setContentText("Please enter correct format (UMxxxx).");
            alert.showAndWait();
        }
    }

    /**
     * Check whether the searched post tag ID is exist
     * @param tagID tag ID of a post
     * @return true if post tag ID exist and false if post tag ID is not exist in the database
     */
    private boolean checkPostTagID(int tagID) {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;
        boolean check = false;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT * FROM post WHERE tagid = '" + adminSearchField.getText().replace("UM","") + "' AND approval = '" + 1 + "'");
            // if the query result is not empty
            if (queryResult.next()) {
                check = true;
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
        return check;
    }
}
