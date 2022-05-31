package com.confessit;

import javafx.event.ActionEvent;
import javafx.event.EventType;
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

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

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

    @FXML
    private Button signUpButton;

    @FXML
    private Button forgotPassword;

    @FXML
    private Button loginButton;


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
            if (validateLogin(email_Login.getText(),password_Login.getText())) {

                // If email address and password entered by the user are correct,
                // Direct user to the main page
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main-Page.fxml")));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            }
        } else {
            // Set "Please enter email address and password" label as visible
            messageLabel_Login.setVisible(true);
        }
    }

    @FXML
    void forgotPasswordButtonPressed(MouseEvent event) {

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

    /***
     * To validate the information inputted by the user for log in purpose
     * @param email is the email inputted by the user
     * @param password is the password inputted by the user
     * @return boolean value whether the login is successful or not
     */
    private boolean validateLogin(String email, String password) {

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

                    return true;
                } else {
                    // Pop up a "Please enter correct email address or password." message
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter correct email address or password.");
                    alert.showAndWait();
                    return false;
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
        return false;
    }

    @FXML
    void moveToPasswordField(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            password_Login.requestFocus();
        }
    }

}
