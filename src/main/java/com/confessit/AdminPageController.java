package com.confessit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class AdminPageController implements Initializable {

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
    private Button vacationButton;

    @FXML
    private VBox contentBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contentBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        contentBox.setAlignment(Pos.TOP_CENTER);

        contentBox.getChildren().add(new FlowPane(100, 100));
        for (Post post: retrieveSubmittedPost()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Post-Object.fxml"));
            try {
                Parent postPane = loader.load();
                Region n = (Region) postPane;
                PostObject postObjectController = loader.getController();
                postObjectController.setPost(post);
                contentBox.getChildren().add(postPane);
                n.prefHeightProperty().bind(postObjectController.getAdjustPane().heightProperty());
                n.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        contentBox.getChildren().add(new FlowPane(100, 100));
    }

    /***
     * Retrieve all submitted post
     * @return an array list consists of submitted post
     */
    public ArrayList<Post> retrieveSubmittedPost() {
        ArrayList<Post> postList = new ArrayList<>();
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "SELECT * FROM post WHERE approval = 0";
            queryResult = statement.executeQuery(sql);

            while(queryResult.next()) {
                int index = queryResult.getInt("queryIndex");
                int tagID = queryResult.getInt("tagid");
                Date datetime = queryResult.getTimestamp("datetime");
                String content = queryResult.getString("content");
                String filePath = queryResult.getString("picfilepath");
                int like = queryResult.getInt("likeNum");
                int dislike = queryResult.getInt("dislikeNum");
                String comment = queryResult.getString("comment");
                boolean approval = queryResult.getBoolean("approval");
                Date approvalTime = queryResult.getTimestamp("approvalTime");
                boolean displayStatus = queryResult.getBoolean("displayStatus");
                String reply = queryResult.getString("replyPosts");
                int replyToPostID = queryResult.getInt("replyTo");

                Post newPost = null;
                if (filePath == null) {
                    newPost = new Post(index, tagID, datetime, content, like, dislike, comment, approval,
                            approvalTime, displayStatus, reply, replyToPostID);
                } else {
                    newPost = new Post(index, tagID, datetime, content, filePath, like, dislike, comment, approval,
                            approvalTime, displayStatus, reply, replyToPostID);
                }
                postList.add(newPost);
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
        return postList;
    }

    /***
     * Automatically approve or disapprove a submitted post based on the sentiment analysis
     */
    @FXML
    void vacationMode(MouseEvent event) {
        System.out.println("HERE");
        scene = vacationButton.getScene();
        scene.setCursor(Cursor.WAIT);

        ArrayList<Post> submittedPost = retrieveSubmittedPost();
//        System.out.println(mark);
//        for (Post post : submittedPost) {
//            System.out.println("Here");
//            if (nlp.estimateSentiment(post.getContent()) >= 1.5) {
//                approve(post.getIndex());
//            }
//        }

        scene.setCursor(Cursor.DEFAULT);

//        Task<Void> executeTask = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                ArrayList<Post> submittedPost = retrieveSubmittedPost();
//                double mark = nlp.estimateSentiment(submittedPost.get(0).getContent());
//                System.out.println(mark);
//                for (Post post : submittedPost) {
//                    System.out.println("Here");
//                    if (nlp.estimateSentiment(post.getContent()) >= 1.5) {
//                        approve(post.getIndex());
//                    }
//                }
//                return null;
//            }
//        };
//
//        Thread th = new Thread(executeTask);
//        th.setDaemon(true);
//        th.start();
//
//        executeTask.setOnSucceeded(e -> {
//            scene.setCursor(Cursor.DEFAULT);
//            System.out.println("Yes");
//        });
//
//        executeTask.setOnFailed(e -> {
//            Throwable problem = executeTask.getException();
//
//        });
//
//        executeTask.setOnCancelled(e -> {
//
//        });
    }

}
