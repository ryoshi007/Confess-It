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

import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import weka.core.pmml.jaxbbindings.False;
import weka.core.pmml.jaxbbindings.True;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Controller for submit post
 */
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

    /**
     * A button to go back to home page
     */
    @FXML
    private Button backButton;

    /**
     * A button to choose the image
     */
    @FXML
    private Button chooseImageButton;

    /**
     * A button to delete the image
     */
    @FXML
    private Button deleteImageButton;

    /**
     * A text area that allows the user to write content
     */
    @FXML
    private TextArea contentField;

    /**
     * An ImageView to display the chosen image
     */
    @FXML
    private ImageView imagePane;

    /**
     * A text field that allows the user to write post id that he wants to reply
     */
    @FXML
    private TextField postIdField;

    /**
     * A button to submit the post
     */
    @FXML
    private Button submitButton;

    /**
     * A label that will give warning if user does not write any content
     */
    @FXML
    private Label empty_warning;

    /**
     * A label that will give warning if user write the post id in the wrong format
     */
    @FXML
    private Label false_postid_warning;

    /**
     * A button that allows user to find if the post id that he wants to reply exists or not
     */
    @FXML
    private Button findButton;

    /**
     * Load the initial look of the page
     * @param url url
     * @param resourceBundle resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        empty_warning.setVisible(false);
        false_postid_warning.setVisible(false);
        submitButton.setStyle("-fx-font-size:20");
        chooseImageButton.setStyle("-fx-font-size:20");
        deleteImageButton.setVisible(false);
    }

    /**
     * Submit a post without picture for approval
     * @param content is the content from the submitted post
     * @return the queryIndex in the form of String
     * @throws SQLException is when there is error with the sql statement
     */
    public String submitPost(String content) throws SQLException {
        Connection connectDB = null;
        String postQueryIndex = String.valueOf(retrieveNewQueryIndex());

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
        return postQueryIndex;
    }

    /**
     * Submit a post with picture for approval
     * @param content is the content from the submitted post
     * @param imageName is the name of the picture
     * @return the queryIndex in the form of String
     * @throws SQLException is when there is error with sql statement
     * @throws IOException is when there is error when executing the code
     */
    public String submitPost(String content, String imageName) throws SQLException, IOException {
        Connection connectDB = null;

        String postQueryIndex = String.valueOf(retrieveNewQueryIndex());
        File fileoutput = new File("src/main/resources/com/postImages/" + postQueryIndex + ".png");
        BufferedImage BI = SwingFXUtils.fromFXImage(imagePane.getImage(), null);
        ImageIO.write(BI, "png", fileoutput);

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "INSERT INTO post (datetime, content, picfilepath, approval, replyPosts, comment) VALUES (?,?,?,?,?,?)";
            PreparedStatement statement = connection.databaseLink.prepareStatement(sql);
            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            statement.setTimestamp(1, timestamp);

            statement.setString(2, content);

            imageName = postQueryIndex;
            statement.setString(3, imageName);
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
        return postQueryIndex;
    }

    /**
     * A method consists of serial steps to determine whether the content is meaningful and not a spam.
     * Firstly, if statement checks whether the length of string reach the minimum 20 or not.
     * Secondly, if statement checks for the entropy score of the content.
     * Basically if the entropy score is lower than 2.55, the content is classified as a spam.
     * Thirdly, if the statement contains typical spam content, it will be rejected.
     * Fourthly, if statement checks whether there is a similar content that has been posted before.
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

        String MODEL = "src/main/resources/com/models/sms.dat";
        SpamClassifier wt = new SpamClassifier();
        wt.loadModel(MODEL);

        if (wt.predict(content).equals("spam")) {
            return false;
        }

        return !isSimilar(content);
    }

    /**
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

    /**
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

    /**
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

    /**
     * Allow user to back to the home page
     * @param event Mouse Click
     * @throws IOException when there is error running the code
     */
    @FXML
    void backToMainPage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main-Page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Allow user to choose the image
     */
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

    /**
     * Check for the validity of the post after submit button is clicked
     * @throws SQLException is when there is error with the sql statement
     * @throws IOException when there is error running the code
     */
    @FXML
    void submit() throws SQLException, IOException {
        empty_warning.setVisible(false);
        false_postid_warning.setVisible(false);
        String postID = "", content = "";

        //Content Field is blank
        if (contentField.getText().isBlank()) {
            if (!postIdField.getText().isBlank()) {
                try {
                    postID = postIdField.getText(2, postIdField.getLength());
                    if (postID.isBlank()) {
                        false_postid_warning.setVisible(true);
                    }else if (!isNumeric(postID)) {
                        false_postid_warning.setVisible(true);
                    } else if (!postIdField.getText().contains("UM")) {
                        false_postid_warning.setVisible(true);
                    }
                } catch (IllegalArgumentException e) {
                    false_postid_warning.setVisible(true);
                }
            }
            empty_warning.setVisible(true);
        } else {
            if (!postIdField.getText().isBlank()) {
                try {
                    postID = postIdField.getText(2, postIdField.getLength());
                    if (postID.isBlank()) {
                        false_postid_warning.setVisible(true);
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Invalid Post ID");
                        alert.setHeaderText("Please follow the correct format!");
                        alert.setContentText("Please check again that your input has the correct format!");
                        alert.showAndWait();

                    } else if (!isNumeric(postID)) {
                        false_postid_warning.setVisible(true);
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Invalid Post ID");
                        alert.setHeaderText("Please follow the correct format!");
                        alert.setContentText("Please check again that your input has the correct format!");
                        alert.showAndWait();

                    } else if (!postIdField.getText().contains("UM")) {
                        false_postid_warning.setVisible(true);
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Invalid Post ID");
                        alert.setHeaderText("Please follow the correct format!");
                        alert.setContentText("Please check again that your input has the correct format!");
                        alert.showAndWait();

                    } else if (!checkIfPostIDExist(postID)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Invalid Post ID");
                        alert.setHeaderText("The post with such post id is not exist!");
                        alert.setContentText("Please check again the post id that you want to reply to!");
                        alert.showAndWait();
                    } else {
                        checkOnSubmissionAndSubmitIt();
                    }
                } catch (IllegalArgumentException e) {
                    false_postid_warning.setVisible(true);
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Post ID");
                    alert.setHeaderText("Please follow the correct format!");
                    alert.setContentText("Please check again that your input has the correct format!");
                    alert.showAndWait();
                }
            } else {
                checkOnSubmissionAndSubmitIt();
            }
        }
    }

    /**
     * Check if the submission is passed or not
     * @throws SQLException is when there is error with the sql statement
     * @throws IOException when there is error running the code
     */
    @FXML
    void checkOnSubmissionAndSubmitIt() throws SQLException, IOException {
        String content = contentField.getText();
        String postID = "";

        if (!postIdField.getText().isBlank()) {
            postID = postIdField.getText(2, postIdField.getLength());
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

            String date = LocalDate.now().toString();
            String time = LocalTime.now().toString().substring(0,8);
            String dateTime = "Submission Date:\t" + date + "\nSubmission Time:\t" + time;
            alert.setContentText(dateTime + "\n\nPlease give a few hours to the admins to approve your post. We can't wait " +
                    "to see your confession post on the main page. Thank you!");
            alert.showAndWait();

            String picFilePath = filePath;
            String postQueryIndex = "";

            if (picFilePath == null || picFilePath.isBlank()) {
                postQueryIndex = submitPost(content);
            } else {
                postQueryIndex = submitPost(content, picFilePath);
            }

            if (!postID.isBlank()) {
                insertReplyPostID(postQueryIndex, postID);
            }

            postIdField.clear();
            contentField.clear();
            imagePane.setImage(null);
            deleteImageButton.setVisible(false);
            filePath = null;
        }
    }

    /**
     * Check if the given tag id is numeric or not
     * @param postID is the input from the postID field
     * @return the postID is integer or not
     */
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

    /**
     * Allow user to delete the displayed image
     */
    @FXML
    void deleteImage() {
        imagePane.setImage(null);
        filePath = null;
        deleteImageButton.setVisible(false);
    }

    /**
     * Allow user to move to the content field is Enter button is pressed
     * @param event is when Enter key is pressed
     */
    @FXML
    void moveToContentField(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            contentField.requestFocus();
        }
    }

    /**
     * Retrieve the latest query index from the database for identification purpose of the newly submitted post
     * @return the latest query index in the form of integer
     */
    private int retrieveNewQueryIndex() throws SQLException {

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            statement.executeUpdate("SET information_schema_stats_expiry = 0");
            queryResult = statement.executeQuery("show table status like 'post'");

            if (queryResult.next()) {
                int retrievedQueryIndex = queryResult.getInt("Auto_increment");
                return retrievedQueryIndex;
            } else {
                return 1;
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
        return 1;
    }

    /**
     * Insert the postId that the post replies to the database
     * @param queryIndex is the queryIndex of the submitted post in the database
     * @param replyPostID is the post id that the post wants to reply
     */
    private void insertReplyPostID(String queryIndex, String replyPostID) {
        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "UPDATE post SET replyTo = " + replyPostID + " WHERE queryIndex = " + queryIndex;
            PreparedStatement statement = connection.databaseLink.prepareStatement(sql);
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

    /**
     * Find if the post that user wants to reply exists or not
     * @param event Mouse Click
     */
    @FXML
    void findPostExist(MouseEvent event) {
        false_postid_warning.setVisible(false);
        String postID = "";

        if (!postIdField.getText().isBlank()) {
            try {
                postID = postIdField.getText(2, postIdField.getLength());
                if (postID.isBlank()) {
                    false_postid_warning.setVisible(true);
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Post ID");
                    alert.setHeaderText("Please follow the correct format!");
                    alert.setContentText("Please check again that your input has the correct format!");
                    alert.showAndWait();

                }else if (!isNumeric(postID)) {
                    false_postid_warning.setVisible(true);
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Post ID");
                    alert.setHeaderText("Please follow the correct format!");
                    alert.setContentText("Please check again that your input has the correct format!");
                    alert.showAndWait();

                } else if (!postIdField.getText().contains("UM")) {
                    false_postid_warning.setVisible(true);
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Post ID");
                    alert.setHeaderText("Please follow the correct format!");
                    alert.setContentText("Please check again that your input has the correct format!");
                    alert.showAndWait();

                } else if (!checkIfPostIDExist(postID)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Post ID");
                    alert.setHeaderText("The post with such post id is not exist!");
                    alert.setContentText("Please check again the post id that you want to reply to!");
                    alert.showAndWait();

                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Valid Post ID");
                    alert.setHeaderText("The post id is valid!");
                    alert.setContentText("You can proceed to submit your confession post by clicking the Submit button");
                    alert.showAndWait();
                }

            } catch (IllegalArgumentException e) {
                false_postid_warning.setVisible(true);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Post ID");
                alert.setHeaderText("Please follow the correct format!");
                alert.setContentText("Please check again that your input has the correct format!");
                alert.showAndWait();
            }
        }
    }

    /**
     * Check if the post ID that user wants to reply exist in the database
     * @param postID is the postId input from the user
     * @return the boolean value depends on the existence of the post in the database
     */
    private boolean checkIfPostIDExist(String postID) {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        boolean isExist = false;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "SELECT * FROM post WHERE find_in_set(" + postID + ", tagid)";

            try {
                queryResult = statement.executeQuery(sql);
                if (queryResult.next()) {
                    if (queryResult.getBoolean("displayStatus") == true) {
                        isExist = true;
                    } else {
                        isExist = false;
                    }
                }
            } catch (SQLSyntaxErrorException e) {
                isExist = false;
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
        return isExist;
    }
}
