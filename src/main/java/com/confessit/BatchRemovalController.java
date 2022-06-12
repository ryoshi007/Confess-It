package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.*;
import java.util.ArrayList;

public class BatchRemovalController {

    @FXML
    private Button adminDeleteButton;

    @FXML
    private Button adminSearchButton;

    @FXML
    private TextField adminSearchField;

    @FXML
    private TreeView<Integer> tagIDTreeView;

    @FXML
    private Label tagIDToBeDeleted;

    private TreeImplementation<Integer> tree = new TreeImplementation<>();

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

    @FXML
    void selectTagToBeDeleted(MouseEvent event) {
        TreeItem<Integer> item = tagIDTreeView.getSelectionModel().getSelectedItem();

        if (item != null) {
            tagIDToBeDeleted.setText(String.valueOf(item.getValue()));
        }
    }


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

    private boolean checkPostTagID(int tagID) {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;
        boolean check = false;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT * FROM post WHERE tagid = '" + adminSearchField.getText().replace("UM","") + "' AND displayStatus = '" + 1 + "'");
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
