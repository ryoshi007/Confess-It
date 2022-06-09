package com.confessit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class CommentObject {

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
     * A label that used to display the username of a user who comments a post
     */
    @FXML
    private Label commentUsername;

    /**
     * A label that used to display a comment by a user
     */
    @FXML
    private Label userComment;

    /**
     * A button that used to delete a comment
     */
    @FXML
    private Button deleteCommentButton;

    /**
     * A int that used to store tag ID of a post
     */
    private int tagID;

    /**
     * A String that used to store user username
     */
    private String username;

    /**
     * A String that used to store user comment
     */
    private String comment;

    /**
     * A Post that used to store current post
     */
    private Post currentPost;

    /**
     * Set a username to commentUsername label and display it
     * Set a comment to userComment label and display it
     * @param username username of a user who comments a post
     * @param comment comment of the user
     */
    public void setComment(String username, String comment, int tagID, Post currentPost) {
        this.username = username;
        this.comment = comment;
        this.tagID = tagID;
        this.currentPost = currentPost;

        commentUsername.setText(username);
        userComment.setText(comment);
        userComment.setWrapText(true);
        if (UserHolder.getInstance().getUser().getUsername().equals(username)) {
            deleteCommentButton.setVisible(true);
        }
    }

    /**
     * Delete a specific comment by removing it from vbox in View-Post-Page after clicking this button
     * @param event Mouse click
     */
    @FXML
    void deleteCommentButtonPressed(MouseEvent event) {
        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.setTitle("Delete Comment");
        alert1.setHeaderText("This action cannot be undone. Are you sure you want to delete this comment?");
        alert1.setContentText("Click OK to delete.");
        alert1.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> button = alert1.showAndWait();
        if (button.isPresent() && button.get() == ButtonType.OK) {
            Json json = new Json();
            json.deleteUserComment(this.tagID,this.username,this.comment);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("View-Post-Page.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                stage.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ViewPostPageController viewPostPageController = loader.getController();
            stage.show();
            viewPostPageController.fillPost(currentPost);
        }
    }

}
