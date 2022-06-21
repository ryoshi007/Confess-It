package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * A controller for login_page
 */
public class LogInController implements Initializable {

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
     * A password field fot getting the password entered by the user
     */
    @FXML
    private PasswordField password_Login;

    /**
     * A text field for getting the email address entered by the user
     */
    @FXML
    private TextField email_Login;

    /**
     * A label for displaying "Please enter email address and password" message
     */
    @FXML
    private Label messageLabel_Login;

    /**
     * A sign-up button
     */
    @FXML
    private Button signUpButton;

    /**
     * A forgot password button
     */
    @FXML
    private Button forgotPassword;

    /**
     * A login button
     */
    @FXML
    private Button loginButton;

    /**
     * Set login message
     * @param url url
     * @param rb resource bundle
     */
    public void initialize(URL url, ResourceBundle rb) {
        messageLabel_Login.setVisible(false);
    }

    /**
     * Direct user to the main page if the email address and password entered by the user are correct
     * @param event Mouse click
     * @throws IOException If the main_page.fxml is not found
     */
    @FXML
    void loginButtonPressed(ActionEvent event) throws IOException {

        if (!email_Login.getText().isBlank() && !password_Login.getText().isBlank()) {
            // If email address and password entered is not empty,
            // Check the email address and password entered by the user
            int checkRole = validateLogin(email_Login.getText(),password_Login.getText());
            if (checkRole != -1) {
                // If email address and password entered by the user are correct
                // Check whether this account is a user account or an admin account (user = 0, admin = 1)
                if (checkRole == 0) {
                    UserHolder.getInstance().setAdmin(false);
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main-Page.fxml")));
                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    UserHolder.getInstance().setAdmin(true);
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin-page.fxml")));
                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }

            }
        } else {
            // Set "Please enter email address and password" label as visible
            messageLabel_Login.setVisible(true);
        }
    }

    /**
     * A forgot password button that used to reset user account password after clicking it
     * @param event Mouse click
     * @throws MessagingException Error
     */
    @FXML
    void forgotPasswordButtonPressed(MouseEvent event) throws MessagingException {

        // Check whether user enters email address at email address text field
        if (!email_Login.getText().isBlank()) {

            int checkEmail = -1;
            Connection connectDB = null;
            Statement statement = null;
            ResultSet queryResult = null;

            try {
                DatabaseConnection connection = new DatabaseConnection();
                connectDB = connection.getConnection();
                statement = connectDB.createStatement();
                queryResult = statement.executeQuery("SELECT * FROM user WHERE email = '" + email_Login.getText() + "'");
                // If the query result is not empty
                if (queryResult.next()) {
                    checkEmail = 1;
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

            // Check whether the email address entered by the user is valid
            if (checkEmail != -1) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Reset Account Password");
                alert.setHeaderText("An email containing a verification code will be sent to your gmail.");
                alert.setContentText("Please check your gmail and use the verification code to reset your account password.");
                alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                Optional<ButtonType> button = alert.showAndWait();
                if (button.isPresent() && button.get() == ButtonType.OK) {
                    // If user click OK button
                    // Send an email to the user
                    Email email = new Email();
                    email.sendVerificationEmail(email_Login.getText(), "forgetPassword");

                    // Create a dialog box and check the verification code entered by the user
                    TextInputDialog textInputDialog = new TextInputDialog();
                    textInputDialog.setTitle("Verification Code Has Been Successfully Sent To Your Gmail");
                    textInputDialog.setHeaderText("Please check your gmail and enter the verification code to reset your account password");
                    textInputDialog.setContentText("Verification code:");
                    Optional<String> code = textInputDialog.showAndWait();

                    if (code.isPresent() && code.get().equals(Integer.toString((email.getVerificationCode())))) {
                        // If the verification code sent matches with the verification entered by the user,
                        // Let the user change his/her account password

                        TextInputDialog textInputDialog2 = new TextInputDialog();
                        textInputDialog2.setTitle("Reset Your Account Password");
                        textInputDialog2.setHeaderText("Please make sure you enter a valid password.");
                        textInputDialog2.setContentText("Please enter a new password.");
                        Optional<String> password = textInputDialog2.showAndWait();

                        if (password.isPresent()) {
                            // If user click OK button
                            if (verifyStrongPassword(password.get())) {
                                // If user enters a valid password

                                String securePassword = null;

                                try {
                                    SecurePassword secure = new SecurePassword();
                                    securePassword = secure.generateSecurePassword((String)(password.get()));
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
                                    psUpdatePass.setString(2, email_Login.getText());
                                    psUpdatePass.executeUpdate();

                                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                    alert1.setTitle("Successful");
                                    alert1.setHeaderText("Your account password has been successfully changed.");
                                    alert1.setContentText("Please try to login.");
                                    alert1.showAndWait();

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
                                // If user enters an invalid password
                                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                                alert1.setTitle("Error");
                                alert1.setHeaderText("Reset account password unsuccessful");
                                alert1.setContentText("Please ensure your password has minimum length of 8, contains at least 1 lowercase, 1 uppercase, 1 special character and 1 digit.");
                                alert1.showAndWait();
                            }
                        } else {
                            // If user clicks cancel
                            Alert alert1 = new Alert(Alert.AlertType.ERROR);
                            alert1.setTitle("Error");
                            alert1.setHeaderText("Reset account password unsuccessful.");
                            alert1.setContentText("Please try again.");
                            alert1.showAndWait();
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
                    alert1.setHeaderText("Reset account password unsuccessful.");
                    alert1.setContentText("Please try again.");
                    alert1.showAndWait();
                }
            } else {
                // If the email address entered by the user is invalid
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Invalid Email Address");
                alert1.setHeaderText("Email address entered is invalid.");
                alert1.setContentText("Please enter a valid email address.");
                alert1.showAndWait();
            }
        } else {
            // If email address text field is blank
            Alert alert1 = new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Invalid Email Address");
            alert1.setHeaderText("Email address text field is blank");
            alert1.setContentText("Please enter your email address at email address text field");
            alert1.showAndWait();
        }
    }

    /**
     * Direct user to the user sign up page
     * @param event Mouse click
     * @throws IOException If the signup_page.fxml is not found
     */
    @FXML
    void goToSignUpButtonPressed(MouseEvent event) throws IOException {
        // Direct user to the sign-up page
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signup_page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * To validate the information inputted by the user for log in purpose
     * @param email is the email inputted by the user
     * @param password is the password inputted by the user
     * @return boolean value whether the login is successful or not
     */
    private int validateLogin(String email, String password) {

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT * FROM user WHERE email = '" + email + "'");
            // if the query result is not empty
            if (queryResult.next()) {
                String retrievedEmail = queryResult.getString("email");
                String retrievedPassword = queryResult.getString("password");

                String retrievedUsername = queryResult.getString("username");

                Date dateOfBirth = queryResult.getDate("dateofbirth");
                String retrievedDescription = queryResult.getString("description");
                int retrievedRole = queryResult.getInt("role");


                SecurePassword secure = new SecurePassword();
                if (retrievedEmail.equals(email) && secure.validatePassword(password, retrievedPassword)) {
                    // If email and password match

                    User user = new User(retrievedUsername, retrievedEmail, retrievedPassword, dateOfBirth,
                            retrievedDescription, retrievedRole);
                    UserHolder holder = UserHolder.getInstance();
                    holder.setUser(user);

                    return retrievedRole;
                } else {
                    // Pop up a "Please enter correct email address or password." message
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter correct email address or password.");
                    alert.showAndWait();
                    return -1;
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
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
        return -1;
    }

    /**
     * Move to password field after user presses ENTER
     * @param event is when the Enter key is pressed
     */
    @FXML
    void moveToPasswordField(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            password_Login.requestFocus();
        }
    }

    /**
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
