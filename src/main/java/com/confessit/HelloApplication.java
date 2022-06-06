package com.confessit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        ArrayList<String> list = new ArrayList<>();
////        list.add("I wish the world would be a better place, free from war and hunger. Peace!");
////        list.add("I dislike school. I believe there are some adjustments that could be done on the current system, but it would take a lot of works!");
////        list.add("Fuck that bitch for stealing my money! If she did the same thing again, I will definitely kill her!");
//        list.add("I support with the person who thinks that the education system needs to have a reformation. But I knew it would be impossible to achieve that :D");
//
//        SentimentPipeline nlpPipeline = new SentimentPipeline();
//        for (String content: list) {
//            nlpPipeline.estimateSentiment(content);
//        }


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

    public static void main(String[] args) {
        launch();
    }

}