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
            // Check whether both password and confirm password are match
            if (((!password_SignUp.getText().isBlank())) && ((!confirmPassword_SignUp.getText().isBlank())) && password_SignUp.getText().equals(confirmPassword_SignUp.getText())) {
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
                        // Create an account
                        createUserAccount(email_SignUp.getText(), username_SignUp.getText(), password_SignUp.getText());

                        // Display account created successfully pop-up message
                        Alert alert = new Alert(Alert.AlertType.NONE);
                        alert.setGraphic(new ImageView(Objects.requireNonNull(this.getClass().getResource("GreenTick.gif")).toString()));
                        alert.setTitle("Success");
                        alert.setHeaderText("Your account has been created successfully.");
                        alert.setContentText("Thank you for registering with Confess It");
                        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                        alert.showAndWait();
                    }

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
            } else {
                // If both password and confirm passwords do not match or is empty,
                // Set "Please make sure both passwords are match" label as visible
                messageLabel_SignUp.setVisible(true);
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
}
