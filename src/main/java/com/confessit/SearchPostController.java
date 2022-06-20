package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controller for search post
 */
public class SearchPostController implements Initializable {

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
     * Choice box that contains the options for user to choose for
     */
    @FXML
    private ChoiceBox<String> categoryBox;

    /**
     * Label that shows the number of posts found
     */
    @FXML
    private Label resultLabel;

    /**
     * A button that searches the post
     */
    @FXML
    private Button searchButton;

    /**
     * A text field that allows user to input the search term
     */
    @FXML
    private TextField searchField;

    /**
     * A VBox that displays post's content
     */
    @FXML
    private VBox contentBox;

    /**
     * A ScrollPane that displayed the search results
     */
    @FXML
    private ScrollPane resultPane;

    /**
     * Choices given to the user when searching for the post
     */
    private String[] choices = {"Keyword", "Date Time", "Date", "Post ID"};

    /**
     * Load the category box
     * @param url url
     * @param resourceBundle resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserHolder currentInfo = UserHolder.getInstance();
        currentInfo.setCurrentPage("SearchPage");

        if (!currentInfo.getCorrectSearchInput().isBlank()) {
            String searchInput = currentInfo.getCorrectSearchInput();
            String searchCategory = currentInfo.getSearchCategory();

            categoryBox.setValue(searchCategory);
            searchField.setText(currentInfo.getUserSearchInput());

            if (searchCategory.equals("Keyword")) {
                displayResult(search("content", searchInput));
            } else if (searchCategory.equals("Date Time") || searchCategory.equals("Date")) {
                displayResult(search("approvalTime", searchInput));
            } else {
                displayResult(search("tagID", searchInput));
            }
        }

        categoryBox.getItems().addAll(choices);

        if (categoryBox.getValue() == null) {
            categoryBox.setValue("Keyword");
        }

        categoryBox.setOnAction(event ->{
            if (categoryBox.getValue() == "Keyword") {
                searchField.setPromptText("Keywords");
            } else if (categoryBox.getValue() == "Date Time") {
                searchField.setPromptText("Example:   1-1-2022 13");
            } else if (categoryBox.getValue() == "Date") {
                searchField.setPromptText("Example:   1-1-2022");
            } else if (categoryBox.getValue() == "Post ID") {
                searchField.setPromptText("Example:   UM2022");
            }
        });

        contentBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        contentBox.setAlignment(Pos.TOP_CENTER);

    }

    /**
     * Search post based on selected category and given input
     * @param columnName the category selected by the user
     * @param value the user-inputted value
     * @return an array list of posts that consists of the value, specified by the column name
     */
    public ArrayList<Post> search(String columnName, String value) {
        ArrayList<Post> postList = new ArrayList<>();
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        UserHolder.getInstance().setCorrectSearchInput(value);
        UserHolder.getInstance().setSearchCategory(categoryBox.getValue());

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();

            String sql = "";
            if (columnName.equalsIgnoreCase("tagID")) {
                int convertedValue = Integer.valueOf(value);
                sql = "SELECT * FROM post WHERE tagID = '"+ convertedValue + "' and displayStatus = 1" ;
            } else {
                sql = "SELECT * FROM post WHERE " +  columnName + " LIKE '%"+ value + "%' and displayStatus = 1";
            }

            queryResult = statement.executeQuery(sql);

            while(queryResult.next()) {
                int index = queryResult.getInt("queryIndex");
                int tagID = queryResult.getInt("tagid");
                Date datetime = queryResult.getTimestamp("datetime");
                String content = queryResult.getString("content");
                String filePath = queryResult.getString("picfilepath");
                int like = queryResult.getInt("likeNum");
                int dislike = queryResult.getInt("dislikeNum");
                String comment= queryResult.getString("comment");
                boolean approval = queryResult.getBoolean("approval");
                Date approvalTime = queryResult.getTimestamp("approvalTime");
                boolean displayStatus = queryResult.getBoolean("displayStatus");
                String reply= queryResult.getString("replyPosts");
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

    /**
     * Search the post based on search term and chosen category
     * @param event is Mouse Click
     */
    @FXML
    void searchPost(ActionEvent event) {
        resultPane.setContent(null);
        VBox newContentBox = new VBox();
        newContentBox.setPrefWidth(1265);
        newContentBox.setSpacing(45);
        newContentBox.setAlignment(Pos.CENTER);
        resultPane.setContent(newContentBox);
        this.contentBox = newContentBox;

        String searchTerm = searchField.getText();
        UserHolder.getInstance().setUserSearchInput(searchTerm);

        if (categoryBox.getValue() == "Keyword") {
            displayResult(search("content", searchTerm));

        } else if (categoryBox.getValue() == "Date Time" && checkDate("dd-MM-yyyy HH", searchTerm)) {

            String[] initialSplit = searchTerm.split(" ");
            String[] split = initialSplit[0].split("-");

            String day = "", month = "", hour = "";

            if (initialSplit[1].length() < 2 && Integer.valueOf(initialSplit[1]) < 10) {
                hour = "0" + initialSplit[1];
            } else {
                hour = initialSplit[1];
            }

            if (split[0].length() < 2 && Integer.valueOf(split[0]) < 10) {
                day = "0" + split[0];
            } else {
                day = split[0];
            }

            if (split[1].length() < 2 && Integer.valueOf(split[1]) < 10) {
                month = "0" + split[1];
            } else {
                month = split[1];
            }

            String correctDateTime = split[2] + "-" + month + "-" + day + " " + hour + ":";
            displayResult(search("approvalTime", correctDateTime));

        } else if (categoryBox.getValue() == "Date" && checkDate("dd-MM-yyyy", searchTerm)) {

            String[] split = searchTerm.split("-");
            String day = "", month = "";

            if (split[0].length() < 2 && Integer.valueOf(split[0]) < 10) {
                day = "0" + split[0];
            } else {
                day = split[0];
            }

            if (split[1].length() < 2 && Integer.valueOf(split[1]) < 10) {
                month = "0" + split[1];
            } else {
                month = split[1];
            }

            String correctDate = split[2] + "-" + month + "-" + day;
            displayResult(search("approvalTime", correctDate));

        } else if (categoryBox.getValue() == "Post ID" && searchTerm.matches("UM[0-9]{1,}")) {
            searchTerm = searchTerm.replace("UM", "");
            displayResult(search("tagID", searchTerm));

        } else {
            resultLabel.setText("Wrong format! Cannot find anything :(");
            searchField.clear();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error in Searching");
            alert.setHeaderText("Invalid Search Term");
            alert.setContentText("Please ensure your search term has the correct structure!");
            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
        }
    }

    /**
     * Display the search results on the ScrollPane
     * @param results is an array list of posts that matched with the search term
     */
    @FXML
    private void displayResult(ArrayList<Post> results) {
        if (results == null) {
            resultLabel.setText("O Confession Post Found");
        } else {
            resultLabel.setText(results.size() + " Confession Post(s) Found");

            contentBox.getChildren().add(new FlowPane(100, 100));
            for (Post post: results) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("SearchResult.fxml"));
                try {
                    Parent postPane = loader.load();
                    Region n = (Region) postPane;
                    SearchResultObject searchResultObjectController = loader.getController();
                    searchResultObjectController.setPostContent(post);
                    contentBox.getChildren().add(postPane);
                    n.prefHeightProperty().bind(searchResultObjectController.getAdjustPane().heightProperty());
                    n.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            contentBox.getChildren().add(new FlowPane(100, 100));
        }
    }

    /**
     * Check the date given by user is valid or not
     * @param format is the format that will be matched against the user's input
     * @param givenDate is the data inputted by the user
     * @return whether the given data is valid or not
     */
    private boolean checkDate(String format, String givenDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(givenDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

}
