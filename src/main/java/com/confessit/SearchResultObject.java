package com.confessit;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

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

    @FXML
    private FlowPane adjustPane;

    @FXML
    private Label postDateLabel;

    @FXML
    private GridPane postGrid;

    @FXML
    private Label tagIDLabel;

    @FXML
    private Button viewButton;


    @FXML
    public void setPostContent(Post pendingPost) throws FileNotFoundException {
        postGrid.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        tagIDLabel.setText("   #UM" + pendingPost.getTagID());
        postDateLabel.setText("   Posted at " + pendingPost.getDatetime().toString());
        CustomTextArea contentField = new CustomTextArea(pendingPost.getContent());
        contentField.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent;");
        postGrid.add(contentField, 0, 3);

        ImageView imagePane = new ImageView("com/fxml-resources/GreenTick.gif");
        imagePane.setFitWidth(150);
        imagePane.setPreserveRatio(true);
        postGrid.add(imagePane, 0, 4);

        viewButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            view(pendingPost.getTagID());
        });

//        if (pictureFilePath != null) {
//            ImageView image = new ImageView(pictureFilePath);
//            postGrid.add(image, 0, 3);
//        }
    }

    public FlowPane getAdjustPane() {
        return this.adjustPane;
    }

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

    void view(int tagID) {
        System.out.println("Haven't implement yet");
    }
}
