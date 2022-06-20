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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * A controller for View-Post-Page
 */
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
    private HBox displayBox;

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
     * A button that used to add a post to archive after clicking it
     */
    @FXML
    private Button addToArchiveButton;

    /**
     * A button that used to remove a post from archive after clicking it
     */
    @FXML
    private Button removeFromArchiveButton;

    /**
     * A button that used to like a post
     */
    @FXML
    private Button likeButton;

    /**
     * A button that used to dislike a post
     */
    @FXML
    private Button dislikeButton;

    /**
     * A label that used to display number of comments
     */
    @FXML
    private Label numOfComments;

    /**
     * A label that used to display number of dislikes
     */
    @FXML
    private Label numOfDislikes;

    /**
     * A label that used to display number of likes
     */
    @FXML
    private Label numOfLikes;

    /**
     * A scroll panel that used to display related posts
     */
    @FXML
    private ScrollPane relatedPostPane;

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

        if (currentPost.getReplyToPostID() != 0) {
            String content = "\nReply to #UM" + currentPost.getReplyToPostID() + "\n\n" + currentPost.getContent();
            postContent.setText(content);
        } else {
            postContent.setText("\n" + currentPost.getContent());
        }

        postContent.setWrapText(true);
        numOfLikes.setText(String.valueOf(currentPost.getLike()));
        numOfDislikes.setText(String.valueOf(currentPost.getDislike()));

        if (currentPost.getPicturePath() != null) {
            Image image = new Image(new File("src/main/resources/com/postImages/" + currentPost.getPicturePath() + ".png").toURI().toString());
            postImagePane.setImage(image);
        }

        Json json = new Json();

        // Check whether user saves this post in archive before
        String archive = json.retrieveFromArchive(UserHolder.getInstance().getUser().getUsername());
        if (archive == null) {
            addToArchiveButton.setVisible(true);
            removeFromArchiveButton.setVisible(false);
        } else {
            archive = archive.replace("[", "").replace("]", "");
            int check = -1;
            if (!archive.isBlank()) {
                archive = archive.replace("\"", "");
                String[] splitArchive = archive.split(",");

                for (String id : splitArchive) {
                    if (id.equals(String.valueOf(currentPost.getTagID()))) {
                        addToArchiveButton.setVisible(false);
                        removeFromArchiveButton.setVisible(true);
                        check = 1;
                    }
                }

                if (check == -1) {
                    addToArchiveButton.setVisible(true);
                    removeFromArchiveButton.setVisible(false);
                }
            } else {
                addToArchiveButton.setVisible(true);
                removeFromArchiveButton.setVisible(false);
            }
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

        if (currentPost.getReplyToPostID() != 0) {
            fillRelatedPost(currentPost.getReplyToPostID(),"Replying Post");
        }

        String responsePost = currentPost.getReply().replace("[", "").replace("]", "");
        if (!responsePost.isBlank()) {
            responsePost = responsePost.replace("\"", "");
            String[] splitResponse = responsePost.split(",");

            for (String id: splitResponse) {
                Post response = findPostByTagID(id.strip());
                if (response.isDisplayed()) {
                    fillRelatedPost(Integer.valueOf(id.strip()), "Response From Other Post");
                }
            }

        }

        int nextPostId = findNextOrPreviousPostByTagID(currentPost.getTagID(), true);
        int prevPostID = findNextOrPreviousPostByTagID(currentPost.getTagID(), false);

        if (nextPostId != 0) {
            fillRelatedPost(nextPostId, "Next Post");
        }
        if (prevPostID != 0) {
            fillRelatedPost(prevPostID, "Previous Post");
        }
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
            numOfComments.setText(String.valueOf(storeComment.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fill related post into the scroll pane
     * @param relatedTagID tag ID of related posts
     * @param typeOfPost post type
     */
    @FXML
    public void fillRelatedPost(int relatedTagID, String typeOfPost) {

        StackPane container = new StackPane();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Related-Post.fxml"));
        try {
            Parent currentPane = loader.load();
            ViewPostObject viewPostObjectController = loader.getController();
            viewPostObjectController.setViewPost(findPostByTagID(String.valueOf(relatedTagID)));
            viewPostObjectController.setPostType(typeOfPost);
            container.getChildren().add(currentPane);
            displayBox.getChildren().add(container);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A button that directs user to the previous page depends on different case
     * Three cases (MainPage, SearchPage and ProfilePage)
     * @param event Mouse click
     * @throws IOException Error
     */
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
    void sendCommentButtonPressed(ActionEvent event) {
        if (!commentTextField.getText().isBlank()) {
            Json json = new Json();
            json.addUserComment(currentPost.getTagID(),UserHolder.getInstance().getUser().getUsername(),commentTextField.getText());
            storeUsername.add(UserHolder.getInstance().getUser().getUsername());
            storeComment.add(commentTextField.getText());
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

    /**
     * Add a new post to user's archive
     * @param event Mouse click
     */
    @FXML
    void addToArchiveButtonPressed(MouseEvent event) {
        Json json = new Json();
        json.addToArchive(currentPost.getTagID(),UserHolder.getInstance().getUser().getUsername());
        addToArchiveButton.setVisible(false);
        removeFromArchiveButton.setVisible(true);
    }

    /**
     * Remove a post from user's archive
     * @param event Mouse click
     */
    @FXML
    void removeFromArchiveButtonPressed(MouseEvent event) {
        Json json = new Json();
        json.deleteArchive(UserHolder.getInstance().getUser().getUsername(),currentPost.getTagID());
        removeFromArchiveButton.setVisible(false);
        addToArchiveButton.setVisible(true);
    }

    /**
     * Add a like to the post after clicking it if user hasn't liked that post
     * Delete a like from the post after clicking it if user have liked that post before
     * @param event Mouse click
     */
    @FXML
    void likeButtonPressed(MouseEvent event) {
        Json json = new Json();
        String like = json.retrieveLikeUser(currentPost.getTagID());
        if (like == null || !like.contains(UserHolder.getInstance().getUser().getUsername())) {
            json.addLike(currentPost.getTagID(),UserHolder.getInstance().getUser().getUsername());
            numOfLikes.setText(String.valueOf(Integer.parseInt(numOfLikes.getText()) + 1));
        } else {
            json.deleteLike(currentPost.getTagID(),UserHolder.getInstance().getUser().getUsername());
            numOfLikes.setText(String.valueOf(Integer.parseInt(numOfLikes.getText()) - 1));
        }
    }

    /**
     * Add a dislike to the post after clicking it if user hasn't disliked that post
     * Delete a dislike from the post after clicking it if user have disliked that post before
     * @param event Mouse click
     */
    @FXML
    void dislikeButtonPressed(MouseEvent event) {
        Json json = new Json();
        String dislike = json.retrieveDislikeUser(currentPost.getTagID());
        if (dislike == null || !dislike.contains(UserHolder.getInstance().getUser().getUsername())) {
            json.addDislike(currentPost.getTagID(),UserHolder.getInstance().getUser().getUsername());
            numOfDislikes.setText(String.valueOf(Integer.parseInt(numOfDislikes.getText()) + 1));
        } else {
            json.deleteDislike(currentPost.getTagID(),UserHolder.getInstance().getUser().getUsername());
            numOfDislikes.setText(String.valueOf(Integer.parseInt(numOfDislikes.getText()) - 1));
        }
    }

    /**
     * A method that used to get a post information from database by using its tag ID
     * Store the post information into a post object and return it
     * @param targetTagID tag ID of a post
     * @return A post
     */
    private Post findPostByTagID(String targetTagID) {
        Post post = null;
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "SELECT * FROM post WHERE tagid = " + targetTagID;
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

                if (filePath == null) {
                    post = new Post(index, tagID, datetime, content, like, dislike, comment, approval,
                            approvalTime, displayStatus, reply, replyToPostID);
                } else {
                    post = new Post(index, tagID, datetime, content, filePath, like, dislike, comment, approval,
                            approvalTime, displayStatus, reply, replyToPostID);
                }
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
        return post;
    }

    private int findNextOrPreviousPostByTagID(int targetTagID, boolean isNext) {
        int postID = 0;
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "";

            if (isNext) {
                sql = "SELECT * from post where tagid > " + targetTagID + " and displayStatus = 1 order by tagid asc limit 1";
            } else {
                sql = "SELECT * from post where tagid < " + targetTagID + " and displayStatus = 1 order by tagid desc limit 1";
            }

            queryResult = statement.executeQuery(sql);

            try {
                if(queryResult.next()) {
                    postID = queryResult.getInt("tagid");
                }
            } catch (SQLSyntaxErrorException e) {
                postID = 0;
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
        return postID;
    }

}
