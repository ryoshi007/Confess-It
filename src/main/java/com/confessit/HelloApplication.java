package com.confessit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * A controller to start the application
 */
public class HelloApplication extends Application {
    /**
     *
     * @param stage is used to represent a window in a JavaFX desktop application
     */
    @Override
    public void start(Stage stage) throws IOException {

        Runnable pendingQueue = new PendingQueue();
        Thread thread = new Thread(pendingQueue);
        thread.setDaemon(true);
        thread.start();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styling.css")).toExternalForm());
        stage.setTitle("Confess It!");

        Image icon = new Image("com/fxml-resources/Logo_Bigger.png");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Launch the application
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch();
    }

}