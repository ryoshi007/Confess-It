package com.confessit;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class CommentObject {

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
     * Set a username to commentUsername label and display it
     * Set a comment to userComment label and display it
     * @param username username of a user who comments a post
     * @param comment comment of the user
     */
    public void setComment(String username, String comment) {
        commentUsername.setText(username);
        userComment.setText(comment);
        userComment.setWrapText(true);
    }

}
