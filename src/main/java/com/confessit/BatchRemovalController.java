package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    public void setTagIDTreeView(TreeImplementation<Integer> tree) {
        tagIDTreeView.setEditable(false);
        TreeNode<Integer> root = tree.getRoot();
        TreeItem<Integer> rootItem = new TreeItem<>(root.getTagID());

        ArrayList<TreeItem<Integer>> itemList = new ArrayList<>();


        for (int i = 0; i < root.getChildrenSize(); i++) {
            itemList.add(new TreeItem<>(root.getChildByIndex(i).getTagID()));
            getAllChildrenTagID(root.getChildByIndex(i),itemList.get(i));
        }

        rootItem.getChildren().setAll(itemList);
        tagIDTreeView.setRoot(rootItem);
    }

    private void getAllChildrenTagID (TreeNode<Integer> root, TreeItem<Integer> treeItemRoot) {
        if (root.isEmptyChild()) {
            return;
        }

        for (int i = 0; i < root.getChildrenSize(); i++) {
            TreeItem<Integer> temp = new TreeItem<>(root.getChildByIndex(i).getTagID());
            treeItemRoot.getChildren().add(temp);
            getAllChildrenTagID(root.getChildByIndex(i),temp);
        }
    }

    @FXML
    void adminDeleteTag(ActionEvent event) {

    }

    @FXML
    void adminSearchPost(ActionEvent event) {

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;
        boolean check = false;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT * FROM post WHERE tagid = '" + adminSearchField.getText() + "'");
            // if the query result is not empty
            if (queryResult.next()) {
                check = true;
            } else {
                // Pop up a message
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid post tag ID");
                alert.setContentText("Please enter an existing post tag ID.");
                alert.showAndWait();
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

        if (check) {
            TreeImplementation<Integer> treeImplementation = new TreeImplementation<>();
            treeImplementation.searchChildren(Integer.valueOf(adminSearchField.getText()));
            setTagIDTreeView(treeImplementation);
        }
    }
}
