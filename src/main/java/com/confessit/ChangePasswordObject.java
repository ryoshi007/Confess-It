package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javax.mail.MessagingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.*;

public class ChangePasswordObject implements Initializable {
    /**
     * A label that used to display "Confirm Password"
     */
    @FXML
    private Label confirmPassword;

    /**
     * A password field that used to accept new password entered by the user and check whether the password is the same as the password
     * entered in confirmPassword field
     */
    @FXML
    private PasswordField confirmPasswordField;

    /**
     * A label that used to display "Current Password"
     */
    @FXML
    private Label currentPassword;

    /**
     * A password field that used to display current password
     */
    @FXML
    private PasswordField currentPasswordField;

    /**
     * A label that used to display "New Password"
     */
    @FXML
    private Label newPassword;

    /**
     * A password field that used to accept new password entered by the user
     */
    @FXML
    private PasswordField newPasswordField;

    /**
     * A button that used to let user change password
     */
    @FXML
    private Button changePssButton;

    /**
     * A button that used to discard the changes
     */
    @FXML
    private Button changePssDiscardButton;

    /**
     * A button that used to save user new password after clicking it
     */
    @FXML
    private Button changePssSaveButton;

    private User currentUser = UserHolder.getInstance().getUser();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentPasswordField.setEditable(false);
        newPassword.setVisible(false);
        newPasswordField.setVisible(false);
        confirmPassword.setVisible(false);
        confirmPasswordField.setVisible(false);
    }

    @FXML
    void changePssButtonPressed(ActionEvent event) {
        newPassword.setVisible(true);
        newPasswordField.setVisible(true);
        confirmPassword.setVisible(true);
        confirmPasswordField.setVisible(true);
        changePssButton.setVisible(false);
        changePssSaveButton.setVisible(true);
        changePssDiscardButton.setVisible(true);
    }

    @FXML
    void changePssDiscardButtonPressed(ActionEvent event) {
        newPassword.setVisible(false);
        newPasswordField.clear();
        newPasswordField.setVisible(false);
        confirmPassword.setVisible(false);
        confirmPasswordField.clear();
        confirmPasswordField.setVisible(false);
        changePssButton.setVisible(true);
        changePssSaveButton.setVisible(false);
        changePssDiscardButton.setVisible(false);
    }

    @FXML
    void changePssSaveButtonPressed(ActionEvent event) throws MessagingException {
        // Check whether the new password entered by the user is the same as old password
        if (!newPasswordField.getText().isBlank()) {

            int checkPss = -1;
            Connection connectDB = null;
            Statement statement = null;
            ResultSet queryResult = null;

            try {
                DatabaseConnection connection = new DatabaseConnection();
                connectDB = connection.getConnection();
                statement = connectDB.createStatement();
                queryResult = statement.executeQuery("SELECT password FROM user WHERE email = '" + currentUser.getEmail() + "'");

                if (queryResult.next()) {
                    // If the query result is not empty
                    SecurePassword securePassword = new SecurePassword();
                    if (securePassword.validatePassword(newPasswordField.getText(), queryResult.getString("password"))) {
                        // If the password entered is the same as the old password
                        checkPss = 1;
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();

            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
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
            // Check whether the new password entered by the user is the same as the old password
            if (checkPss != 1) {
                // Check whether the new password entered by the user is valid
                if (verifyStrongPassword(newPasswordField.getText())) {
                    // Check whether both new password and confirm password are match
                    if (newPasswordField.getText().equals(confirmPasswordField.getText())) {

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Change Account Password");
                        alert.setHeaderText("An email containing a verification code will be sent to your gmail.");
                        alert.setContentText("Please check your gmail and use the verification code to change your account password.");
                        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                        Optional<ButtonType> button = alert.showAndWait();
                        if (button.isPresent() && button.get() == ButtonType.OK) {
                            // If user click OK button
                            // Send an email to the user
                            Email email = new Email();
                            email.sendVerificationEmail(currentUser.getEmail(), "forgetPassword");

                            // Create a dialog box and check the verification code entered by the user
                            TextInputDialog textInputDialog = new TextInputDialog();
                            textInputDialog.setTitle("Verification Code Has Been Successfully Sent To Your Gmail");
                            textInputDialog.setHeaderText("Please check your gmail and enter the verification code to change your account password");
                            textInputDialog.setContentText("Verification code:");
                            Optional<String> code = textInputDialog.showAndWait();

                            if (code.isPresent() && code.get().equals(Integer.toString((email.getVerificationCode())))) {
                                // If the verification code entered by the user is correct
                                // Change the password

                                String securePassword = null;

                                try {
                                    SecurePassword secure = new SecurePassword();
                                    securePassword = secure.generateSecurePassword((String) (newPasswordField.getText()));
                                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                                    throw new RuntimeException(e);
                                }

                                Connection connection = null;
                                PreparedStatement psUpdatePass = null;

                                try {
                                    DatabaseConnection db = new DatabaseConnection();
                                    connection = db.getConnection();
                                    psUpdatePass = connection.prepareStatement("UPDATE user SET password = ? WHERE email = ?");
                                    psUpdatePass.setString(1, securePassword);
                                    psUpdatePass.setString(2, currentUser.getEmail());
                                    psUpdatePass.executeUpdate();

                                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                    alert1.setTitle("Successful");
                                    alert1.setHeaderText("Your account password has been successfully changed.");
                                    alert1.setContentText("Please don't forget your password :)");
                                    alert1.showAndWait();

                                    newPassword.setVisible(false);
                                    newPasswordField.setVisible(false);
                                    confirmPassword.setVisible(false);
                                    confirmPasswordField.setVisible(false);
                                    changePssButton.setVisible(true);
                                    changePssSaveButton.setVisible(false);
                                    changePssDiscardButton.setVisible(false);

                                } catch (SQLException e) {
                                    e.printStackTrace();

                                } finally {
                                    if (psUpdatePass != null) {
                                        try {
                                            psUpdatePass.close();

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (connection != null) {
                                        try {
                                            connection.close();

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } else {
                                // If the verification code entered does not match,
                                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                                alert1.setTitle("Error");
                                alert1.setHeaderText("The confirmation code entered is empty or does not match.");
                                alert1.setContentText("Please try again.");
                                alert1.showAndWait();
                            }
                        } else {
                            // If user clicks CANCEL button
                            Alert alert1 = new Alert(Alert.AlertType.ERROR);
                            alert1.setTitle("Error");
                            alert1.setHeaderText("Change account password unsuccessful.");
                            alert1.setContentText("Please try again.");
                            alert1.showAndWait();
                        }
                    } else {
                        // If both new password and confirm password are different
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.setTitle("Different Password");
                        alert1.setHeaderText("Both new password and confirm password are not match.");
                        alert1.setContentText("Please ensure that you enter the same password.");
                        alert1.showAndWait();
                    }
                } else {
                    // If both new password and confirm passwords are invalid
                    // Pop up a warning window
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid password");
                    alert.setHeaderText("The new password is invalid.");
                    alert.setContentText("Please ensure your password has minimum length of 8, contains at least 1 lowercase, 1 uppercase, 1 special character and 1 digit.");
                    alert.showAndWait();
                }
            } else {
                // If the new password entered by the user is the same as the old password
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Invalid Password");
                alert1.setHeaderText("New password and old password are the same.");
                alert1.setContentText("Please enter a different password.");
                alert1.showAndWait();
            }
        } else {
            // If new password text is blank
            Alert alert1 = new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Invalid Password");
            alert1.setHeaderText("New Password text field is blank");
            alert1.setContentText("Please enter a valid password.");
            alert1.showAndWait();
        }
    }

    @FXML
    void moveToChangePssConfirmPasswordField(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            confirmPasswordField.requestFocus();
        }
    }

    /***
     * Verify the password inputted by the user is strong
     * @param passwordInput the password inputted by the user
     * @return a boolean value to verify the password is strong
     */
    private boolean verifyStrongPassword(String passwordInput) {
        boolean hasLower = false, hasUpper = false, hasDigit = false, specialChar = false, minLength = false;
        Set<Character> set = new HashSet<>(Arrays.asList('!', '@', '#', '$', '%', '^', '&', '*', '(', ')',
                '-', '+'));
        for (char i : passwordInput.toCharArray()) {
            if (Character.isLowerCase(i)) hasLower = true;
            if (Character.isUpperCase(i)) hasUpper = true;
            if (Character.isDigit(i)) hasDigit = true;
            if (set.contains(i)) specialChar = true;
        }

        if (passwordInput.toCharArray().length >= 8) {
            minLength = true;
        }
        return (hasLower && hasUpper && hasDigit && specialChar && minLength);
    }
}
