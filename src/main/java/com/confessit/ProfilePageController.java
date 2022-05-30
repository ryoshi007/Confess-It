package com.confessit;

import javafx.fxml.FXML;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;


public class ProfilePageController implements Initializable {

    @FXML
    private Button archieveButton;

    @FXML
    private Line archieveLine;

    @FXML
    private TextField birthdayField;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image profile = new Image("com/fxml-resources/default-profile-picture.png", false);
        profileImage.setFill(new ImagePattern(profile));
        archieveLine.setVisible(false);
        historyLine.setVisible(false);

        User user = UserHolder.getInstance().getUser();
        usernameField.setText(user.getUsername());
        descriptionField.setText(user.getDescription());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getPassword());
        birthdayField.setText(null);
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
    public void editUserDateOfBirth(User user, String dateOfBirth) {

        java.sql.Date sqlDate;

        if (!valDateOfBirth(dateOfBirth)) {
            // if the date entered by the user is invalid
            return;
        } else {
            // if it is valid, convert and store it in sql Date type
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy"); // 1-1-2001
            java.util.Date date = null;
            try {
                date = sdf1.parse(dateOfBirth);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            sqlDate = new java.sql.Date(date.getTime());
        }

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
     * Check whether date of birth entered by the user is valid
     * @param dateOfBirth date of birth entered by the user
     * @return true if date is valid, else false
     */
    private boolean valDateOfBirth(String dateOfBirth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(dateOfBirth);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    @FXML
    void checkArchieve(MouseEvent mouseEvent) {
        archieveLine.setVisible(true);
        historyLine.setVisible(false);
        passwordLine.setVisible(false);
    }

    @FXML
    void checkHistory(MouseEvent mouseEvent) {
        historyLine.setVisible(true);
        archieveLine.setVisible(false);
        passwordLine.setVisible(false);
    }

    @FXML
    void changePassword(MouseEvent mouseEvent) {
        passwordLine.setVisible(true);
        historyLine.setVisible(false);
        archieveLine.setVisible(false);
    }

    @FXML
    void editProfile(MouseEvent mouseEvent) {
        descriptionField.setEditable(true);
        descriptionField.getStyleClass().clear();
        descriptionField.getStyleClass().addAll("text-input", "text-area");
        descriptionField.setBlendMode(BlendMode.SRC_ATOP);

        birthdayField.setEditable(true);
        birthdayField.getStyleClass().clear();
        birthdayField.getStyleClass().addAll("text-input", "text-field");
        birthdayField.setCursor(Cursor.TEXT);

        saveButton.setVisible(true);
        discardButton.setVisible(true);
    }

    @FXML
    void saveChanges(MouseEvent event) {
        User user = UserHolder.getInstance().getUser();
        descriptionField.setText(descriptionField.getText());
        birthdayField.setText(birthdayField.getText());
        editUserDescription(user, descriptionField.getText());
//        editUserDateOfBirth(user, birhtdayField.getText());

        user.setDescription(descriptionField.getText());
//        user.setDateOfBirth(birhtdayField.getText());

        revertChanges();
        saveButton.setVisible(false);
        discardButton.setVisible(false);
    }


    @FXML
    void discardChanges(MouseEvent event) {
        revertChanges();

        User user = UserHolder.getInstance().getUser();
        descriptionField.setText(user.getDescription());
        birthdayField.setText(null);

        saveButton.setVisible(false);
        discardButton.setVisible(false);
    }

    void revertChanges() {
        descriptionField.setEditable(false);
        descriptionField.getStyleClass().clear();
        descriptionField.getStyleClass().addAll("text-input", "text-area", "text", "profile-text-area");
        descriptionField.setBlendMode(BlendMode.DARKEN);

        birthdayField.setEditable(false);
        birthdayField.getStyleClass().clear();
        birthdayField.getStyleClass().addAll("text-input", "text-field", "text", "profile-input-field");
        birthdayField.setCursor(Cursor.DEFAULT);
    }

}
