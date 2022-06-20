package com.confessit;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A controller for displaying the search results
 */
public class SearchResultObject {
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
     * A FlowPane that displays the post's content
     */
    @FXML
    private FlowPane adjustPane;

    /**
     * A label that displays the approved date for the post
     */
    @FXML
    private Label postDateLabel;

    /**
     * A GridPane for displaying the post's content
     */
    @FXML
    private GridPane postGrid;

    /**
     * A label that displays the tag id of the post
     */
    @FXML
    private Label tagIDLabel;

    /**
     * A button to view the post
     */
    @FXML
    private Button viewButton;

    /**
     * Set the post content on the GridPane
     * @param pendingPost is the post object
     */
    @FXML
    public void setPostContent(Post pendingPost) throws FileNotFoundException {
        postGrid.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        tagIDLabel.setText("   #UM" + pendingPost.getTagID());
        postDateLabel.setText("   Posted at " + pendingPost.getApprovalTime().toString());
        CustomTextArea contentField = new CustomTextArea(pendingPost.getContent());
        contentField.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent;");
        postGrid.add(contentField, 0, 3);

        if (pendingPost.getPicturePath() != null) {
            Image image = new Image(new File("src/main/resources/com/postImages/" + pendingPost.getPicturePath() + ".png").toURI().toString());
            ImageView imagePane = new ImageView(image);
            imagePane.setFitWidth(150);
            imagePane.setPreserveRatio(true);
            postGrid.add(imagePane, 0, 4);
        }

        viewButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("View-Post-Page.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                stage.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ViewPostPageController viewPostPageController = loader.getController();
            stage.show();
            viewPostPageController.fillPost(pendingPost);
        });
    }

    /**
     * Get the container for displaying the post
     * @return the FlowPane of the post
     */
    public FlowPane getAdjustPane() {
        return this.adjustPane;
    }

    /**
     * Calculate the text field that needs to display all text in the post
     */
    class CustomTextArea extends TextArea {
        CustomTextArea(String content) {
            setWrapText(true);
            setFocusTraversable(false);
            setMouseTransparent(true);
            setText(content);
            setFont(Font.font("Lato", 20));
            setBlendMode(BlendMode.DARKEN);

            sceneProperty().addListener((observableNewScene, oldScene, newScene) -> {
                if (newScene != null) {
                    applyCss();
                    layout();
                    Node text = lookup(".text");
                    prefHeightProperty().bind(Bindings.createDoubleBinding(() -> getFont().getSize() + text.getBoundsInLocal().getHeight(), text.boundsInLocalProperty()));
                    text.boundsInLocalProperty().addListener((observableBoundsAfter, boundsBefore, boundsAfter) -> {
                        Platform.runLater(() -> requestLayout());
                    });
                    adjustPane.setPrefHeight(prefHeightProperty().getValue() % 10);
                }
            });
        }
    }
}
