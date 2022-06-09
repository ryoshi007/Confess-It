package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

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
     * A text field that used to let user enter comment
     */
    @FXML
    private TextField commentTextField;

    /**
     * A Post object that used to store current post
     */
    private Post currentPost;

    /**
     * A button that used to save a post in archive after clicking it
     */
    @FXML
    private Button saveToArchiveButton;

    /**
     * An array list that used to store sub-posts of a post
     */
    private ArrayList<Post> subPostList = new ArrayList<>();

    /**
     * An array list that used to comment's username
     */
    private ArrayList<String> storeUsername = new ArrayList<>();

    /**
     * An array list that used to store user comment
     */
    private ArrayList<String> storeComment = new ArrayList<>();

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
        postTagID.setText("#UM " + currentPost.getTagID());
        postedDate.setText((currentPost.getDatetime().toString().substring(0,19)));
        postContent.setText(currentPost.getContent());
        postContent.setWrapText(true);
        if (currentPost.getPicturePath() != null) {
            Image image = new Image(new File("src/main/resources/com/postImages/" + currentPost.getPicturePath() + ".png").toURI().toString());
            postImagePane.setImage(image);
        }

        Json json = new Json();

        // Check whether user saves this post in archive before
        String archive = json.retrieveFromArchive(UserHolder.getInstance().getUser().getUsername());
        if (archive == null || !archive.contains(String.valueOf(currentPost.getTagID()))) {
            saveToArchiveButton.setVisible(true);
        }

        // Get comments from database
        String usernameAndCommentRemovePunctuation = json.retrieveUserComment(currentPost.getTagID());
        if (!usernameAndCommentRemovePunctuation.equals("[]")) {
            usernameAndCommentRemovePunctuation = usernameAndCommentRemovePunctuation.replace("[\"[","").replace("]\"]","");
            String[] splitComment = usernameAndCommentRemovePunctuation.split("\\]\", \"\\[");
            for (String s : splitComment) {
                storeUsername.add(s.substring(0, s.indexOf(","))); // Store username
                storeComment.add(s.substring(s.indexOf(",") + 1)); // Store comment
            }

            // Display comments
            for (int i = 0; i < storeComment.size(); i++) {
                fillComment(storeUsername.get(i), storeComment.get(i));
            }
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
     * Display comment of a post with comment's user username
     * @param username user username
     * @param comment user comment
     */
    public void fillComment(String username, String comment) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Comment-Object.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            CommentObject commentObject = fxmlLoader.getController();
            commentObject.setComment(username,comment,currentPost.getTagID(),currentPost);
            vBox.getChildren().add(anchorPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void backToPreviousPage(MouseEvent event) throws IOException {
        if (UserHolder.getInstance().getCurrentPage() == "MainPage") {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main-Page.fxml")));
        } else if (UserHolder.getInstance().getCurrentPage() == "SearchPage") {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Search-Page.fxml")));
        } else if (UserHolder.getInstance().getCurrentPage() == "ProfilePage") {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Profile-Page.fxml")));
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Add a new comment to the vbox and display it after clicking this button
     * @param event Mouse click
     */
    @FXML
    void sendCommentButtonPressed(MouseEvent event) {
        if (!commentTextField.getText().isBlank()) {
            Json json = new Json();
            json.addUserComment(currentPost.getTagID(),UserHolder.getInstance().getUser().getUsername(),commentTextField.getText());
            fillComment(UserHolder.getInstance().getUser().getUsername(),commentTextField.getText());
            commentTextField.clear();
        } else {
            // If user clicks SEND without entering any comment
            Alert alert1 = new Alert(Alert.AlertType.WARNING);
            alert1.setTitle("WARNING");
            alert1.setHeaderText("Comment text field is blank.");
            alert1.setContentText("Please enter a comment.");
            alert1.showAndWait();
        }
    }


    @FXML
    void saveToArchiveButtonPressed(MouseEvent event) {
        Json json = new Json();
        json.addToArchive(currentPost.getTagID(),UserHolder.getInstance().getUser().getUsername());
        saveToArchiveButton.setVisible(false);
    }
}
