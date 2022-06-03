package com.confessit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController extends CreateAccount {

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
     * A password field fot getting the confirm password entered by the user
     */
    @FXML
    private PasswordField confirmPassword_SignUp;

    /**
     * A text field fot getting the email address entered by the user
     */
    @FXML
    private TextField email_SignUp;

    /**
     * A password field fot getting the password entered by the user
     */
    @FXML
    private PasswordField password_SignUp;

    /**
     * A text field fot getting the username entered by the user
     */
    @FXML
    private TextField username_SignUp;

    /**
     * A label for displaying "Please make sure both passwords are match" message
     */
    @FXML
    private Label messageLabel_SignUp;

    /**
     * Direct user to the login page
     * @param event Mouse click
     * @throws IOException If the login_page.fxml is not found
     */
    @FXML
    void goToLoginButtonPressed(MouseEvent event) throws IOException {
        // Forward user to the login page
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login_page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Create an account if all the credentials entered by the user are valid
     * Store the user account information into database
     * @param event Mouse click
     */
    @FXML
    void signUpButtonPressed(ActionEvent event) {
        // Hide the "Please make sure both passwords match" label
        messageLabel_SignUp.setVisible(false);

        // Check whether the username is entered,
        if (!username_SignUp.getText().isBlank()) {
            // Check whether the email address is entered
            // Check whether the email address entered is valid
            if (!email_SignUp.getText().isBlank() && verifyCorrectEmail(email_SignUp.getText())) {
                // Check whether both password and confirm password are valid
                if (((!password_SignUp.getText().isBlank())) && verifyStrongPassword(password_SignUp.getText())) {
                    // Check whether both password and confirm password are match
                    if (!confirmPassword_SignUp.getText().isBlank() && password_SignUp.getText().equals(confirmPassword_SignUp.getText())) {
                        // If both passwords match
                        // Check whether the email address is already in use

                        Connection connectDB = null;
                        Statement statement = null;
                        ResultSet queryResult = null;

                        try {
                            DatabaseConnection connection = new DatabaseConnection();
                            connectDB = connection.getConnection();
                            statement = connectDB.createStatement();

                            queryResult = statement.executeQuery("SELECT email FROM user WHERE email = '" + email_SignUp.getText() + "'");
                            if (queryResult.next()) {
                                // If the email address is already in use
                                // Display a pop-up message
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Invalid Email Address");
                                alert.setHeaderText("The email address is in use.");
                                alert.setContentText("Please enter another email address.");
                                alert.showAndWait();
                            } else {
                                // If the email address is not in use
                                // Send a verification code to user's Google account
                                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                alert1.setTitle("Confess It User Account Registration");
                                alert1.setHeaderText("Creating a new account...");
                                alert1.setContentText("An email containing a verification code will be sent to your email address.");
                                alert1.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                                Optional<ButtonType> button = alert1.showAndWait();
                                if (button.isPresent() && button.get() == ButtonType.OK) {
                                    // Send a confirmation code to user's Google account
                                    Email email = new Email();
                                    email.sendVerificationEmail(email_SignUp.getText(), "user");

                                    // Create a dialog box and check the verification code entered by the user
                                    TextInputDialog textInputDialog = new TextInputDialog();
                                    textInputDialog.setTitle("Verification Code Has Been Successfully Sent To Your Gmail");
                                    textInputDialog.setHeaderText("Please check your gmail and enter the verification code to continue account registration");
                                    textInputDialog.setContentText("Verification code:");

                                    Optional<String> code = textInputDialog.showAndWait();
                                    if (code.isPresent() && code.get().equals(Integer.toString(email.getVerificationCode()))) {
                                        // If the verification code entered matches,
                                        // Create a user account
                                        createUserAccount(email_SignUp.getText(), username_SignUp.getText(), password_SignUp.getText());

                                        // Display account created successfully pop-up message
                                        Alert alert = new Alert(Alert.AlertType.NONE);
                                        ImageView imagePane = new ImageView("com/fxml-resources/GreenTick.gif");
                                        imagePane.setFitWidth(150);
                                        imagePane.setPreserveRatio(true);
                                        alert.setGraphic(imagePane);
                                        alert.setTitle("Success");
                                        alert.setHeaderText("Your account has been successfully created.");
                                        alert.setContentText("Thank you for registering with Confess It");
                                        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                                        alert.showAndWait();

                                        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login_page.fxml")));
                                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                        scene = new Scene(root);
                                        stage.setScene(scene);
                                        stage.show();

                                    } else {
                                        // If the verification code entered does not match,
                                        // Display an error pop-up message
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setHeaderText("The verification code entered does not match.");
                                        alert.setContentText("Please try again.");
                                        alert.showAndWait();
                                    }
                                } else if (button.isPresent() && button.get() == ButtonType.CANCEL){
                                    Alert alert = new Alert(Alert.AlertType.NONE);
                                    ImageView imagePane = new ImageView("com/fxml-resources/RedCross.gif");
                                    imagePane.setFitWidth(150);
                                    imagePane.setPreserveRatio(true);
                                    alert.setGraphic(imagePane);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Account registration unsuccessful");
                                    alert.setContentText("Your Confess It account is not created successfully");
                                    alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                                    alert.showAndWait();
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();

                        } catch (IOException | MessagingException e) {
                            throw new RuntimeException(e);
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
                    } else {
                        // If both password and confirm passwords are not match,
                        // Set "Please make sure both passwords are match" label as visible
                        messageLabel_SignUp.setVisible(true);
                    }
                } else {
                    // If both password and confirm passwords are empty or are invalid
                    // Pop up a warning window
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid password");
                    alert.setHeaderText("The password entered is either empty or invalid.");
                    alert.setContentText("Please ensure your password has minimum length of 8, contains at least 1 lowercase, 1 uppercase, 1 special character and 1 digit.");
                    alert.showAndWait();
                }
            } else {
                // If username entered is empty,
                // Pop up a warning window
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid email address");
                alert.setHeaderText("The email address entered is either empty or invalid.");
                alert.setContentText("Please enter a valid email address.");
                alert.showAndWait();
            }
        } else {
            // If username entered is empty,
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid username");
            alert.setHeaderText("The username entered is empty.");
            alert.setContentText("Please enter a valid username.");
            alert.showAndWait();
        }
    }

    /***
     * Create a user account (role = 0)
     * Insert the information entered by the user into database
     * @param email is the email address entered by the user
     * @param username is the username entered by the user
     * @param password is the password entered by the user
     */
    public void createUserAccount(String email, String username, String password) {
        super.createAccount(email, username, password, 0);
    }

    @FXML
    void moveToConfirmPasswordField(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            confirmPassword_SignUp.requestFocus();
        }
    }

    @FXML
    void moveToEmailField(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            email_SignUp.requestFocus();
        }
    }

    @FXML
    void moveToPasswordField(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            password_SignUp.requestFocus();
        }
    }

    /***
     * Validate the email inputted by the user is in correct format
     * @param emailInput the email inputted by the user
     * @return a boolean value to verify the email has the correct format
     */
    private boolean verifyCorrectEmail(String emailInput) {
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(emailInput);
        return matcher.find();
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
