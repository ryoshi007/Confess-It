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
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
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


public class ProfilePageController implements Initializable {

    @FXML
    private Button archiveButton;

    @FXML
    private Line archiveLine;

    @FXML
    private DatePicker birthdayField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private Button editProfileButton;

    @FXML
    private Button discardButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button changePasswordButton;

    @FXML
    private TextField emailField;

    @FXML
    private Button historyButton;

    @FXML
    private Line historyLine;

    @FXML
    private Line passwordLine;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Circle profileImage;

    @FXML
    private TextField usernameField;

    @FXML
    private AnchorPane mainPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserHolder.getInstance().setCurrentPage("ProfilePage");

        Image profile = new Image("com/fxml-resources/default-profile-picture.png", false);
        mainPane.getChildren().clear();
        profileImage.setFill(new ImagePattern(profile));
        archiveLine.setVisible(false);
        historyLine.setVisible(false);

        birthdayField.setDisable(true);
        birthdayField.setStyle("-fx-opacity: 1");
        birthdayField.getEditor().setStyle("-fx-opacity: 1");

        User user = UserHolder.getInstance().getUser();
        usernameField.setText(user.getUsername());
        descriptionField.setText(user.getDescription());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getPassword());

        Date date = user.getDateOfBirth();
        if (date != null) {
            LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            birthdayField.setValue(localDate);
        }

        displayArchivePage();
    }

    /**
     * Change user username and update it in database
     * @param user a User object
     * @param username new username entered by the user
     */
    public void editUsername(User user, String username) {
        if (user.getUsername().equals(username)) {
            System.out.println("Please enter a different username");
        } else {
            Connection connectDB = null;

            try {
                DatabaseConnection connection = new DatabaseConnection();
                connectDB = connection.getConnection();
                String sql = "UPDATE user SET username = ? WHERE email = ?";
                PreparedStatement statement = connectDB.prepareStatement(sql);

                statement.setString(1, username);
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
    }

    /**
     * Change user account password and update it in database
     * @param user a User object
     * @param passwordInput new password entered by the user
     */
    public void changeAccountPassword(User user, String passwordInput) {
        SecurePassword secure = new SecurePassword();

        //To check the original password is the same as the inputted password
        //If yes, return
        try {
            if (secure.validatePassword(passwordInput,user.getPassword())) {
                System.out.println("Please use a different password");
                return;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        //Encrypt the inputted password
        String securePassword = null;
        try {
            securePassword = secure.generateSecurePassword(passwordInput);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }


        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "UPDATE user SET password = ? WHERE email = ?";
            PreparedStatement statement = connectDB.prepareStatement(sql);

            statement.setString(1, securePassword);
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

    @FXML
    void checkArchive(ActionEvent event) {
        displayArchivePage();
    }

    @FXML
    private void displayArchivePage() {
        mainPane.getChildren().clear();

        archiveLine.setVisible(true);
        historyLine.setVisible(false);
        passwordLine.setVisible(false);

        ScrollPane scrollPane = new ScrollPane();
        VBox displayBox = new VBox();
        displayBox.setSpacing(10);

        Json json = new Json();
        String userArchive = json.retrieveFromArchive(UserHolder.getInstance().getUser().getUsername());
        ArrayList<String> archiveList = new ArrayList<>();

        if (!userArchive.equals("[]")) {
            userArchive = userArchive.replace("[","").replace("]","").replace("\"", "");

            if (userArchive.contains(",")) {
                String[] splitArchive = userArchive.split(",");
                for (String id : splitArchive) {
                    archiveList.add(id.strip());
                }
            } else {
                archiveList.add(userArchive.strip());
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
        scrollPane.setContent(displayBox);
        mainPane.getChildren().add(scrollPane);

        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, -1.0);
        scrollPane.setContent(displayBox);
    }

    @FXML
    void checkHistory(MouseEvent mouseEvent) {
        mainPane.getChildren().clear();

        historyLine.setVisible(true);
        archiveLine.setVisible(false);
        passwordLine.setVisible(false);
    }

    @FXML
    void changePassword(MouseEvent mouseEvent) throws IOException {
        mainPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Change-Password-Object.fxml"));
        AnchorPane changePssPane = loader.load();
        mainPane.getChildren().setAll(changePssPane.getChildren());

        passwordLine.setVisible(true);
        historyLine.setVisible(false);
        archiveLine.setVisible(false);
    }

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
        }
        return postList;
    }

}
