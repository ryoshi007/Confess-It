package com.confessit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Pagination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Controller for the homepage / main page when users log in
 */
public class MainPageController implements Initializable {

    /**
     * Container for display the confession post
     */
    private Stack<Post> displayStack = new Stack<>();

    /**
     * Store the confession post
     */
    private List<ArrayList<Post>> storedPosts = new ArrayList<ArrayList<Post>>();

    /**
     * Allow user to switch page
     */
    @FXML
    private Pagination pagePane;

    /**
     * Label that display when there is no confession post
     */
    @FXML
    private HBox emptyLabel;

    /**
     * Load all the displayed posts
     * @param url url
     * @param resourceBundle resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserHolder.getInstance().setCurrentPage("MainPage");

        emptyLabel.setVisible(false);
        pagePane.setVisible(true);

        int currentPageNumber = UserHolder.getInstance().getCurrentPageNumber();
        if (currentPageNumber != -1) {
            pagePane.setCurrentPageIndex(currentPageNumber);
        }

        int numberOfPosts = getNumberOfDisplayedPost();

        if (numberOfPosts <= 6) {
            pagePane.setPageCount(1);
        } else {
            int totalPageCount = 0, tempCount = numberOfPosts;
            while (tempCount >= 6) {
                totalPageCount++;
                tempCount -= 6;
            }
            if (tempCount != 0) {
                totalPageCount++;
            }
            pagePane.setPageCount(totalPageCount);
        }

        retrieveRecentPost();

        while (!displayStack.isEmpty()) {
            ArrayList<Post> preparePost = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                if (displayStack.isEmpty()) {
                    break;
                }
                preparePost.add(displayStack.pop());
            }
            storedPosts.add(preparePost);
        }

        if (!storedPosts.isEmpty()) {
            pagePane.setPageFactory(new Callback<Integer, Node>() {
                @Override
                public Node call(Integer integer) {
                    UserHolder.getInstance().setCurrentPageNumber(pagePane.getCurrentPageIndex());
                    return createPage(storedPosts.get(pagePane.getCurrentPageIndex()));
                }

            });
        } else {
            emptyLabel.setVisible(true);
            pagePane.setVisible(false);
        }
    }

    /**
     * Retrieve recent posts that will be displayed on the main page
     *
     */
    private void retrieveRecentPost() {
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "SELECT * FROM post WHERE displayStatus = 1 ORDER BY tagid";
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
                displayStack.add(newPost);
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
    }

    /**
     * Get the number of displayed post
     * @return the number of the post that is displayed
     */
    private int getNumberOfDisplayedPost() {
        int numberOfDisplayedPost = 0;
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "SELECT displayStatus, COUNT(CASE WHEN displayStatus = TRUE then 1 end) as displayed_post from post";
            queryResult = statement.executeQuery(sql);

            if (queryResult.next()) {
                numberOfDisplayedPost = queryResult.getInt("displayed_post");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
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
        return numberOfDisplayedPost;
    }

    /**
     * Create each page for the home page
     * @param post is an array list of displayed posts
     * @return the StackPane which consists of all displayed page
     */
    private StackPane createPage(ArrayList<Post> post) {
        StackPane page = new StackPane();
        GridPane grid = new GridPane();

        grid.setHgap(20);
        grid.setVgap(50);
        grid.setPadding(new Insets(0, 0, 0, 0));

        int i = 0;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {

                if (i >= post.size()) {
                    break;
                }

                StackPane container = new StackPane();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("View-Post-Object.fxml"));

                try {
                    Parent currentViewPane = loader.load();
                    ViewPostObject viewPostObjectController = loader.getController();
                    viewPostObjectController.setViewPost(post.get(i));
                    container.getChildren().add(currentViewPane);

                    GridPane.setRowIndex(container, row);
                    GridPane.setColumnIndex(container, col);
                    GridPane.setHgrow(container, Priority.ALWAYS);
                    GridPane.setVgrow(container, Priority.ALWAYS);

                    grid.getChildren().add(container);
                    i++;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        page.getChildren().add(grid);
        return page;
    }

}
