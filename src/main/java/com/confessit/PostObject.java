package com.confessit;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.FileNotFoundException;
import java.sql.SQLOutput;
import java.util.concurrent.Callable;
import java.util.function.DoublePredicate;

public class PostObject {

    @FXML
    private Label postDate;

    @FXML
    private GridPane postGrid;

    @FXML
    private Button approveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private FlowPane adjustPane;

    @FXML
    public void setPost(Post pendingPost) throws FileNotFoundException {
        postGrid.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        postDate.setText("   Posted at " + pendingPost.getDatetime().toString());
        CustomTextArea contentField = new CustomTextArea(pendingPost.getContent());
        contentField.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent;");
        postGrid.add(contentField, 0, 2);

        ImageView imagePane = new ImageView("com/fxml-resources/GreenTick.gif");
        imagePane.setFitWidth(150);
        imagePane.setPreserveRatio(true);
        postGrid.add(imagePane, 0, 3);

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
}

