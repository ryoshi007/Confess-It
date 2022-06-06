package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewPostObject {

    /**
     * Stage is used to represent a window in a JavaFX desktop application
     */
    private Stage stage;

    /**
     * Scene is the container for all content in a scene graph
     */
    private Scene scene;

    /**
     * Root provides a solution to the issue of defining a reusable component with FXML
     */
    private Parent root;

    /**
     * A label that used to display post content
     */
    @FXML
    private Label viewPostContent;

    /**
     * A label that used to display tag ID of a post
     */
    @FXML
    private Label viewPostTagID;

    @FXML
    private Label dislikeLabel;

    @FXML
    private Label likeLabel;

    @FXML
    private Label messageLabel;

    /**
     * A Post object that used to store current post
     */
    private Post currentPost;

    /**
     * Set a tag ID to viewPostTagID label and display it
     * Set a post content to viewPostContent label and display it
     * @param post A post object
     */
    public void setViewPost(Post post) {
        this.currentPost = post;
        viewPostTagID.setText(" #UM " + currentPost.getTagID());
        viewPostContent.setText("\n" + currentPost.getContent());
        viewPostContent.setWrapText(true);
        likeLabel.setText(String.valueOf(currentPost.getLike()));
        dislikeLabel.setText(String.valueOf(currentPost.getDislike()));

        //Get number of comment
        messageLabel.setText("0");
    }

    /**
     * Direct user to another post page
     * @param event Mouse click
     * @throws IOException Throw an exception if page does not exist
     */
    @FXML
    void viewPostButtonPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("View-Post-Page.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        ViewPostPageController viewPostPageController = loader.getController();
        stage.show();
        viewPostPageController.fillPost(currentPost);
    }
}
