package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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

    @FXML
    private ChoiceBox<String> categoryBox;

    @FXML
    private Label resultLabel;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private VBox contentBox;

    @FXML
    private ScrollPane resultPane;

    private String[] choices = {"Keyword", "Date Time", "Date", "Post ID"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryBox.getItems().addAll(choices);
        categoryBox.setValue("Keyword");

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

    /***
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

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();

            String sql = "";
            if (columnName.equalsIgnoreCase("tagID")) {
                int convertedValue = Integer.valueOf(value);
                sql = "SELECT * FROM post WHERE tagID = '"+ convertedValue + "'";
            } else {
                sql = "SELECT * FROM post WHERE " +  columnName + " LIKE '%"+ value + "%'";
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

                Post newPost = null;
                if (filePath == null) {
                    newPost = new Post(index, tagID, datetime, content, like, dislike, comment, approval,
                            approvalTime, displayStatus, reply);
                } else {
                    newPost = new Post(index, tagID, datetime, content, filePath, like, dislike, comment, approval,
                            approvalTime, displayStatus, reply);
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

        if (categoryBox.getValue() == "Keyword") {
            displayResult(search("content", searchTerm));

        } else if (categoryBox.getValue() == "Date Time" && checkDate("dd-MM-yyyy HH", searchTerm)) {

            String[] initialSplit = searchTerm.split(" ");
            String[] split = initialSplit[0].split("-");

            String hour = (Integer.valueOf(initialSplit[1]) < 10 ? "0" : "") + initialSplit[1];
            String day = (Integer.valueOf(split[0]) < 10 ? "0" : "") + split[0];
            String month = (Integer.valueOf(split[1]) < 10 ? "0" : "") + split[1];
            String correctDateTime = split[2] + "-" + month + "-" + day + " " + hour + ":";
            displayResult(search("approvalTime", correctDateTime));

        } else if (categoryBox.getValue() == "Date" && checkDate("dd-MM-yyyy", searchTerm)) {

            String[] split = searchTerm.split("-");
            String day = (Integer.valueOf(split[0]) < 10 ? "0" : "") + split[0];
            String month = (Integer.valueOf(split[1]) < 10 ? "0" : "") + split[1];
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

    @FXML
    private void displayResult(ArrayList<Post> results) {
        if (results == null) {
            resultLabel.setText("O Confession Post Found");
        } else {
            scene = searchButton.getScene();
            scene.setCursor(Cursor.WAIT);

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
            scene.setCursor(Cursor.DEFAULT);
        }
    }

    private boolean checkDate(String format, String givenDate) {
        SimpleDateFormat dateFormat= new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(givenDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

}
