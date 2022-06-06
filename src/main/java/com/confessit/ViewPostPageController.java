package com.confessit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ViewPostPageController {

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
     * A vbox that used for vertical scrolling
     */
    @FXML
    private VBox vBox;

    /**
     * A Hbox that used for horizontal scrolling
     */
    @FXML
    private HBox hBox;

    /**
     * A label that used to display post content
     */
    @FXML
    private TextArea postContent;

    /**
     * An image view to display the image of a post
     */
    @FXML
    private ImageView postImagePane;

    /**
     * A label that used to display posted date of a post
     */
    @FXML
    private Label postedDate;

    /**
     * A label that used to display tag ID of a post
     */
    @FXML
    private Label postTagID;

    /**
     * A Post object that used to store current post
     */
    private Post currentPost;

    /**
     * An array list that used to store sub-posts of a post
     */
    private ArrayList<Post> subPostList = new ArrayList<>();

    /**
     * Set a tag ID to postTagID label and display it
     * Set a date to postedDate label and display it
     * Set a content to postContent label and display it
     * If there is an image for that post , set an image to postImagePane image view and display it
     * @param post Post object
     */
    @FXML
    public void fillPost(Post post) {

        this.currentPost = post;

        // Fill the post content and its information into view-post-page
        postTagID.setText("# " + currentPost.getTagID());
        postedDate.setText((currentPost.getDatetime().toString().substring(0,19)));
        postContent.setText(currentPost.getContent());
        postContent.setWrapText(true);
        if (currentPost.getPicturePath() != null) {
            Image image = new Image(new File("src/main/resources/com/postImages/" + currentPost.getPicturePath() + ".png").toURI().toString());
            postImagePane.setImage(image);
        }

//        // Get sub-posts of a post
//        Connection connectDB = null;
//        Statement statement = null;
//        ResultSet queryResult = null;
//
//        try {
//            DatabaseConnection connection = new DatabaseConnection();
//            connectDB = connection.getConnection();
//            statement = connectDB.createStatement();
//            String sql = "SELECT * FROM post WHERE tagid = '" + 200 + "'";
//            queryResult = statement.executeQuery(sql);
//
//            while (queryResult.next()) {
//                int index = queryResult.getInt("queryIndex");
//                int tag = queryResult.getInt("tagid");
//                Date datetime = queryResult.getTimestamp("datetime");
//                String content = queryResult.getString("content");
//                String filePath = queryResult.getString("picfilepath");
//                int like = queryResult.getInt("likeNum");
//                int dislike = queryResult.getInt("dislikeNum");
//                String comment = queryResult.getString("comment");
//                boolean approval = queryResult.getBoolean("approval");
//                Date approvalTime = queryResult.getTimestamp("approvalTime");
//                boolean displayStatus = queryResult.getBoolean("displayStatus");
//                String reply = queryResult.getString("replyPosts");
//
//                Post subPost = new Post(index,tag,datetime,content,filePath,like,dislike,comment,approval,approvalTime,displayStatus,reply);
//                subPostList.add(subPost);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//
//        } finally {
//            if (queryResult != null) {
//                try {
//                    queryResult.close();
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (statement != null) {
//                try {
//                    statement.close();
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (connectDB != null) {
//                try {
//                    connectDB.close();
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        for (Post subPost1 : subPostList) {
//            fillSubPost(subPost1);
//            fillComment(subPost1);
//        }
//    }
//
//    /**
//     * Display sub-posts of a post
//     * @param subPost sub-posts of a post
//     */
//    public void fillSubPost(Post subPost) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View-Post-Object.fxml"));
//            AnchorPane anchorPane = fxmlLoader.load();
//            ViewPostObject viewPostObject = fxmlLoader.getController();
//            viewPostObject.setViewPost(subPost);
//            hBox.getChildren().add(anchorPane);
//            hBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Display comments of a post
     * @param subPost comments of a post
     */
    public void fillComment(Post subPost) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Comment-Object.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            CommentObject commentObject = fxmlLoader.getController();
            commentObject.setComment("test","WOW seems not bad");
            vBox.getChildren().add(anchorPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
