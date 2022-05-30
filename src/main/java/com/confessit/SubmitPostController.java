package com.confessit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

public class SubmitPostController implements Initializable {
    private String filePath;

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
    private Button backButton;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button deleteImageButton;

    @FXML
    private TextArea contentField;

    @FXML
    private ImageView imagePane;

    @FXML
    private TextField postIdField;

    @FXML
    private Button submitButton;

    @FXML
    private Label empty_warning;

    @FXML
    private Label false_postid_warning;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        empty_warning.setVisible(false);
        false_postid_warning.setVisible(false);
        submitButton.setStyle("-fx-font-size:20");
        chooseImageButton.setStyle("-fx-font-size:20");
        deleteImageButton.setVisible(false);
    }

    /***
     * Submit a post without picture for approval
     * @param content is the content from the submitted post
     */
    public void submitPost(String content) {
        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "INSERT INTO post (datetime, content, approval, replyPosts, comment) VALUES (?,?,?,?,?)";
            PreparedStatement statement = connection.databaseLink.prepareStatement(sql);

            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            statement.setTimestamp(1, timestamp);
            statement.setString(2, content);
            statement.setBoolean(3, false);
            statement.setString(4,"[]");
            statement.setString(5,"[]");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (connectDB != null) {
                try {
                    connectDB.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     * Submit a post with picture for approval
     * @param content is the content from the submitted post
     * @param filePath is the file path that directs to the pictures
     */
    public void submitPost(String content, String filePath) {
        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "INSERT INTO post (datetime, content, picfilepath, approval, replyPosts, comment) VALUES (?,?,?,?,?,?)";
            PreparedStatement statement = connection.databaseLink.prepareStatement(sql);
            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            statement.setTimestamp(1, timestamp);

            statement.setString(2, content);
            statement.setString(3, filePath);
            statement.setBoolean(4, false);
            statement.setString(5,"[]");
            statement.setString(6,"[]");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (connectDB != null) {
                try {
                    connectDB.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     * A method consists of serial steps to determine whether the content is meaningful and not a spam.
     * The first if statement checks whether the length of string reach the minimum 20 or not.
     * The second if statement checks for the entropy score of the content.
     * Basically if the entropy score is lower than 2.55, the content is classified as a spam.
     * The third if statement checks whether there is a similar content that has been posted before.
     * @param content the strings of the submission post
     * @return boolean value, true if content is non-spam and meaning, false otherwise.
     */
    public boolean detectSpam(String content) {
        if (content.length() <= 20) {
            return false;
        }
        if (calculateEntropy(content) < 2.55) {
            return false;
        }
        return !isSimilar(content);
    }

    /***
     * Retrieve contents of each approved post in an array list of string
     * @return string array that consists of contents of each approved post
     */
    private ArrayList<String> retrieveContent() {
        ArrayList<String> contentList = new ArrayList<>();
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "SELECT * FROM post WHERE approval = 1 ORDER BY tagid";
            queryResult = statement.executeQuery(sql);

            while(queryResult.next()) {
                String content = queryResult.getString("content");
                contentList.add(content);
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
        return contentList;
    }

    /***
     * This is a method that will check the repetition of each string in a sentence and give score based on it.
     * The method applies the concept of Shannon's Entropy - a measure of uncertainty associated with random variables.
     * The more randomness the sentence is, the higher the score will be returned.
     * Example = "aaaaaaaaaaaaaaaaaaaaaa" will return 0, but a legit sentence will give score higher than 2.5
     * @return the entropy score of the sentence
     */
    private double calculateEntropy(String content) {
        String[] split = content.split(" ");
        List<String> values = List.of(split);
        Map<String, Integer> map = new HashMap<>();

        // count the occurrences of each value
        for (String sequence : values) {
            if (!map.containsKey(sequence)) {
                map.put(sequence, 0);
            }
            map.put(sequence, map.get(sequence) + 1);
        }

        // calculate the entropy
        double result = 0.0;
        for (String sequence : map.keySet()) {
            double frequency = (double) map.get(sequence) / values.size();
            result -= frequency * (Math.log(frequency) / Math.log(2));
        }

        return result;
    }

    /***
     * This is a method that will compare the submitted content with the contents from approved posts.
     * It utilises FuzzySearch library from me.xdrop.fuzzywuzzy repository.
     * weightedRatio will compare two strings and return score above 90 if both strings have highly-similar contents.
     * @param content is the string of the submitted post
     * @return true if the similarity score is above 90, else false
     */
    private boolean isSimilar(String content) {
        ArrayList<String> contentsFromOthers = retrieveContent();
        int score;
        for (String contentsFromOther : contentsFromOthers) {
            score = FuzzySearch.weightedRatio(contentsFromOther, content);
            if (score > 90) {
                return true;
            }
        }
        return false;
    }

    @FXML
    void backToMainPage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main-Page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void chooseImage() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Upload an image");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPEG", "*.jpeg")
            );
            File selectedFile = fileChooser.showOpenDialog(stage);
            String mimetype = Files.probeContentType(selectedFile.toPath());
            if (mimetype != null && mimetype.split("/")[0].equals("image")) {
                Image image = new Image(selectedFile.toURI().toString(), 1024, 720, false, false);
                imagePane.setImage(image);
                imagePane.setFitHeight(150);
                imagePane.setFitWidth(200);

                deleteImageButton.setVisible(true);
                filePath = selectedFile.toURI().toString();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Wrong file type");
                alert.setContentText("Please select file type that are specifically for image.\n\nExample: png, jpeg, jpg and so on.");
                alert.showAndWait();
            }

        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Image is not uploaded");
            alert.setContentText("Please upload image!");
            alert.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void submitPost() {
        empty_warning.setVisible(false);
        false_postid_warning.setVisible(false);
        String postID = "", content = "";

        if (contentField.getText().isBlank()) {
            if (!postIdField.getText().isBlank()) {
                postID = postIdField.getText(2, postIdField.getLength());
                if (!isNumeric(postID)) {
                    false_postid_warning.setVisible(true);
                }
            }
            empty_warning.setVisible(true);

        } else {

            if (!postIdField.getText().isBlank()) {
                postID = postIdField.getText(2, postIdField.getLength());
                if (!isNumeric(postID)) {
                    false_postid_warning.setVisible(true);
                } else {
                    content = "Reply UM" + postID + "\n\n" + contentField.getText();
                }
            } else {
                content = contentField.getText();
            }

            if (!detectSpam(contentField.getText())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error in Submission");
                alert.setHeaderText("Your submission has been rejected!");
                alert.setContentText("Due to in violation of our community standards, your submission is not approved "+
                        "by our system. Please alter your confession content so that it is not a spam, " +
                        "has a minimum length of 20 and not a copy of previous confession posts.");
                alert.showAndWait();

            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success in Submission");
                alert.setHeaderText("Your submission has been accepted!");
                alert.setContentText("Please give a few hours to the admins to approve your post. We can't wait " +
                        "to see your confession post on the main page. Thank you!");
                alert.showAndWait();

                String picFilePath = filePath;

                //submit method (Currently thinking for reply post)
                if (picFilePath == null || picFilePath.isBlank()) {
                    submitPost(content);
                } else {
                    submitPost(content, picFilePath);
                }

                postIdField.clear();
                contentField.clear();
                imagePane.setImage(null);
                deleteImageButton.setVisible(false);
                filePath = null;
            }
        }
    }

    private boolean isNumeric(String postID) {
        boolean isNumeric = true;
        char[] arrChar = postID.toCharArray();
        for (char c: arrChar) {
            if (Character.isDigit(c) == false) {
                isNumeric = false;
            }
        }
        return isNumeric;
    }

    @FXML
    void deleteImage() {
        imagePane.setImage(null);
        filePath = null;
        deleteImageButton.setVisible(false);
    }

    @FXML
    void moveToContentField(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            contentField.requestFocus();
        }
    }

}
