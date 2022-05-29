package com.confessit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styling.css")).toExternalForm());
        stage.setTitle("Confess It!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}