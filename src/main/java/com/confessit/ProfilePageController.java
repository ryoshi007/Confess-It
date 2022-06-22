package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * A controller for the profile page
 */
public class ProfilePageController implements Initializable {

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
     * A button to show the archived posts
     */
    @FXML
    private Button archiveButton;

    /**
     * A line that displays current selected option is Archive
     */
    @FXML
    private Line archiveLine;

    /**
     * A picker for choosing the date
     */
    @FXML
    private DatePicker birthdayField;

    /**
     * A text area that displays the description
     */
    @FXML
    private TextArea descriptionField;

    /**
     * A button to edit the profile
     */
    @FXML
    private Button editProfileButton;

    /**
     * A button to discard the edits on the profile
     */
    @FXML
    private Button discardButton;

    /**
     * A button to save the edits on the profile
     */
    @FXML
    private Button saveButton;

    /**
     * A button to change the password
     */
    @FXML
    private Button changePasswordButton;

    /**
     * A text field to display the email
     */
    @FXML
    private TextField emailField;

    /**
     * A line that shows current selection is Change Password
     */
    @FXML
    private Line passwordLine;

    /**
     * A password that displays the password in dots
     */
    @FXML
    private PasswordField passwordField;

    /**
     * A container to display the profile image in rounded frame
     */
    @FXML
    private Circle profileImage;

    /**
     * A text field that displays the username
     */
    @FXML
    private TextField usernameField;

    /**
     * An anchor pane that will change based on selection
     */
    @FXML
    private AnchorPane mainPane;

    /**
     * Menu bar of the profile page
     */
    @FXML
    private AnchorPane profileMenuBar;

    /**
     * An anchor pane for the profile page
     */
    @FXML
    private AnchorPane profilePage;

    /**
     * A line that displays the current page is Profile Page
     */
    @FXML
    private Line selectionLine;

    /**
     * Set up the menu bar based on the user's role and display user's information
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserHolder.getInstance().setCurrentPage("ProfilePage");

        Image profile = new Image("com/fxml-resources/default-profile-picture.png", false);
        mainPane.getChildren().clear();
        profileImage.setFill(new ImagePattern(profile));
        archiveLine.setVisible(false);

        birthdayField.setDisable(true);
        birthdayField.setStyle("-fx-opacity: 1");
        birthdayField.getEditor().setStyle("-fx-opacity: 1");

        User user = UserHolder.getInstance().getUser();
        usernameField.setText(user.getUsername());
        descriptionField.setText(user.getDescription());
        emailField.setText(user.getEmail());
        passwordField.setText("secret");

        Date date = user.getDateOfBirth();
        if (date != null) {
            LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            birthdayField.setValue(localDate);
        }

        displayArchivePage();

        if (UserHolder.getInstance().isAdmin()) {
            profileMenuBar.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-Bar.fxml"));
            try {
                AnchorPane adminMenuBar = loader.load();
                adminMenuBar.setLayoutX(321);
                adminMenuBar.setLayoutY(-23);

                archiveLine.setVisible(false);
                archiveButton.setVisible(false);
                changePasswordButton.setLayoutX(520);
                loader = new FXMLLoader(getClass().getResource("Change-Password-Object.fxml"));
                AnchorPane changePssPane = loader.load();
                mainPane.getChildren().setAll(changePssPane.getChildren());
                passwordLine.setLayoutX(590);
                passwordLine.setVisible(true);
                selectionLine.setLayoutX(828);
                selectionLine.setLayoutY(61);
                profilePage.getChildren().add(adminMenuBar);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            displayArchivePage();
        }
    }

    /**
     * Edit user description and store it in database
     * @param user a User object
     * @param description user description entered by the user
     */
    public void editUserDescription(User user, String description) {
        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "UPDATE user SET description = ? WHERE email = ?";
            PreparedStatement statement = connectDB.prepareStatement(sql);

            statement.setString(1, description);
            statement.setString(2, user.getEmail());
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
     * Edit user date of birth and update it in database
     * @param user a User object
     * @param dateOfBirth user date of birth entered by the user
     */
    public void editUserDateOfBirth(User user, Date dateOfBirth) {

        java.sql.Date sqlDate = new java.sql.Date(dateOfBirth.getTime());

        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "UPDATE user SET dateofbirth = ? WHERE email = ?";
            PreparedStatement statement = connectDB.prepareStatement(sql);

            statement.setDate(1, sqlDate);
            statement.setString(2, user.getEmail());
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
     * Show the archive posts when the button is clicked
     * @param event Mouse Click
     */
    @FXML
    void checkArchive(ActionEvent event) {
        displayArchivePage();
    }

    /**
     * Display the archived post on the anchor pane
     */
    @FXML
    private void displayArchivePage() {
        mainPane.getChildren().clear();

        archiveLine.setVisible(true);
        passwordLine.setVisible(false);

        ScrollPane scrollPane = new ScrollPane();
        VBox displayBox = new VBox();
        displayBox.setSpacing(10);

        Json json = new Json();
        String userArchive = json.retrieveFromArchive(UserHolder.getInstance().getUser().getUsername());
        ArrayList<String> archiveList = new ArrayList<>();

        if (userArchive != null) {
            if (!userArchive.equals("[]")) {
                userArchive = userArchive.replace("[", "").replace("]", "").replace("\"", "");

                if (userArchive.contains(",")) {
                    String[] splitArchive = userArchive.split(",");
                    for (String id : splitArchive) {
                        archiveList.add(id.strip());
                    }
                } else {
                    archiveList.add(userArchive.strip());
                }
            }
        }

        ArrayList<Post> archivePost = obtainArchive(archiveList);
        for (Post post: archivePost) {
            StackPane container = new StackPane();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Post-on-Profile.fxml"));

            try {
                Parent currentPane = loader.load();
                ViewPostObject viewPostObjectController = loader.getController();
                viewPostObjectController.setViewPost(post);
                container.getChildren().add(currentPane);
                displayBox.getChildren().add(container);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        mainPane.getChildren().add(scrollPane);

        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, -1.0);
        scrollPane.setContent(displayBox);
    }

    /**
     * Display Change-Password-Object after user clicks this button
     * User is allowed to change password in this page
     * @param mouseEvent Mouse click
     * @throws IOException Error
     */
    @FXML
    void changePassword(MouseEvent mouseEvent) throws IOException {
        mainPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Change-Password-Object.fxml"));
        AnchorPane changePssPane = loader.load();
        mainPane.getChildren().setAll(changePssPane.getChildren());

        passwordLine.setVisible(true);
        archiveLine.setVisible(false);
    }

    /**
     * Allow the user to change the profile when the button is clicked
     * @param mouseEvent Mouse Click
     */
    @FXML
    void editProfile(MouseEvent mouseEvent) {

        descriptionField.setEditable(true);
        descriptionField.getStyleClass().clear();
        descriptionField.getStyleClass().addAll("text-input", "text-area");
        descriptionField.setBlendMode(BlendMode.SRC_ATOP);

        birthdayField.getStyleClass().clear();
        birthdayField.getStyleClass().addAll("combo-box-base", "date-picker");
        birthdayField.setCursor(Cursor.HAND);
        birthdayField.setDisable(false);
        birthdayField.setBlendMode(BlendMode.SRC_ATOP);

        saveButton.setVisible(true);
        discardButton.setVisible(true);
    }

    /**
     * Save the changes made by user to database
     * @param event Mouse Click
     */
    @FXML
    void saveChanges(MouseEvent event) {
        User user = UserHolder.getInstance().getUser();
        LocalDate localDate = birthdayField.getValue();

        if (localDate != null) {
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date date = Date.from(instant);

            if (date.after(new Date())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Date of Birth!");
                alert.setContentText("Please a valid date! You can't born in the future!");
                alert.showAndWait();

            } else {
                descriptionField.setText(descriptionField.getText());

                editUserDescription(user, descriptionField.getText());
                editUserDateOfBirth(user, date);

                user.setDescription(descriptionField.getText());
                user.setDateOfBirth(date);

                revertChanges();
                saveButton.setVisible(false);
                discardButton.setVisible(false);
            }

        } else {

            descriptionField.setText(descriptionField.getText());
            editUserDescription(user, descriptionField.getText());
            user.setDescription(descriptionField.getText());

            revertChanges();
            saveButton.setVisible(false);
            discardButton.setVisible(false);
        }

    }


    /**
     * Discard the changes made by the user
     * @param event Mouse Click
     */
    @FXML
    void discardChanges(MouseEvent event) {
        revertChanges();

        User user = UserHolder.getInstance().getUser();
        descriptionField.setText(user.getDescription());

        Date date = user.getDateOfBirth();
        if (date != null) {
            LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            birthdayField.setValue(localDate);
        }

        saveButton.setVisible(false);
        discardButton.setVisible(false);
    }

    /**
     * Reset the page look back to original
     */
    void revertChanges() {
        descriptionField.setEditable(false);
        descriptionField.getStyleClass().clear();
        descriptionField.getStyleClass().addAll("text-input", "text-area", "text", "profile-text-area");
        descriptionField.setBlendMode(BlendMode.DARKEN);

        birthdayField.setDisable(true);
        birthdayField.setStyle("-fx-opacity: 1");
        birthdayField.getEditor().setStyle("-fx-opacity: 1");
        birthdayField.setBlendMode(BlendMode.DARKEN);

        birthdayField.setEditable(false);
        birthdayField.getStyleClass().clear();
        birthdayField.getStyleClass().addAll("combo-box-base", "date-picker", "text");
        birthdayField.setCursor(Cursor.DEFAULT);
    }

    /**
     * Obtain the archived posts
     * @param idList is a list of tag ids for the archived post
     * @return an array list consists of archived posts
     */
    private ArrayList<Post> obtainArchive(ArrayList<String> idList) {
        ArrayList<Post> postList = new ArrayList<>();
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        for (String id: idList) {
            try {
                DatabaseConnection connection = new DatabaseConnection();
                connectDB = connection.getConnection();
                statement = connectDB.createStatement();
                String sql = "SELECT * FROM post WHERE tagid = " + id;
                queryResult = statement.executeQuery(sql);

                while (queryResult.next()) {
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
        }
        return postList;
    }

    /**
     * To delete the account
     * @param event Mouse Click
     * @throws IOException when the app cannot run the code
     */
    @FXML
    void deleteAccountButtonPressed(ActionEvent event) throws IOException {

        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.setTitle("Delete Account");
        alert1.setHeaderText("This action cannot be undone. Are you sure you want to delete your account?");
        alert1.setContentText("Click OK to delete.");
        alert1.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> button = alert1.showAndWait();
        if (button.isPresent() && button.get() == ButtonType.OK) {

            Connection connectDB = null;
            PreparedStatement statement = null;

            try {
                DatabaseConnection connection = new DatabaseConnection();
                connectDB = connection.getConnection();
                statement = connectDB.prepareStatement("DELETE FROM user WHERE email = '" + UserHolder.getInstance().getUser().getEmail() + "'");
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();

            } finally {
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

            // Forward user to the login page
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login_page.fxml")));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }


    }

}
