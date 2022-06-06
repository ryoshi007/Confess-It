package com.confessit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminListController extends CreateAccount implements Initializable {

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
     * A checkbox that displays "I agree to not reveal my identity as an administrator"
     */
    @FXML
    private CheckBox adminAgreePolicy_SignUp;

    /**
     * A text field that used to get the confirm-password entered by the user
     */
    @FXML
    private PasswordField adminConfirmPassword_SignUp;

    /**
     * A text field that used to get the email address entered by the user
     */
    @FXML
    private TextField adminEmail_SignUp;

    /**
     * A text field that used to get the password entered by the user
     */
    @FXML
    private PasswordField adminPassword_SignUp;

    /**
     * A text field that used to get the username entered by the user
     */
    @FXML
    private TextField adminUsername_SignUp;

    /**
     * A label that used to display "Please make sure both passwords are match" message
     */
    @FXML
    private Label adminMessageLabel_SignUp;

    /**
     * A sign-up button
     */
    @FXML
    private Button adminSignUpButton;

    /**
     * A table view that used to display admins
     */
    @FXML
    private TableView<User> adminListTable;

    /**
     * A column table that used to display admin username in table view
     */
    @FXML
    private TableColumn<User, String> usernameCol;

    /**
     * A column table that used to display admin email address in table view
     */
    @FXML
    private TableColumn<User, String> emailAddressCol;

    /**
     * An observable list that used to store admin instances
     */
    ObservableList<User> adminList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        storeAdminInformation();
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailAddressCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        adminListTable.setItems(adminList);
        adminListTable.setEditable(false);
    }

    /**
     * A method that used to get admins from the database and store it in an observable list named adminList
     */
    private void storeAdminInformation() {

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT * FROM user WHERE role = '" + 1 + "'");
            // if the query result is not empty
            while (queryResult.next()) {
                String retrievedUsername = queryResult.getString("username");
                String retrievedEmail = queryResult.getString("email");
                User admin = new User();
                admin.setUsername(retrievedUsername);
                admin.setEmail(retrievedEmail);
                adminList.add(admin);
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
     * Create an admin account after clicking if all the credentials entered by the user are valid
     * Store the admin account information into database
     * @param event Mouse click
     */
    @FXML
    void adminSignUpButtonPressed(ActionEvent event) {
        // Hide the "Please make sure both passwords match" label
        adminMessageLabel_SignUp.setVisible(false);

        // Check whether the username is entered,
        if (!adminUsername_SignUp.getText().isBlank()) {
            // Check whether the email address is entered
            // Check whether the email address entered is valid
            if (!adminEmail_SignUp.getText().isBlank() && verifyCorrectEmail(adminEmail_SignUp.getText())) {
                // Check whether both password and confirm password are valid
                if (((!adminPassword_SignUp.getText().isBlank())) && verifyStrongPassword(adminPassword_SignUp.getText())) {
                    // Check whether both password and confirm password are match
                    if (!adminConfirmPassword_SignUp.getText().isBlank() && adminPassword_SignUp.getText().equals(adminConfirmPassword_SignUp.getText())) {
                        // Check whether the adminAgreePolicy is selected
                        if (adminAgreePolicy_SignUp.isSelected()) {

                            // Check whether the email address is already in use
                            Connection connectDB = null;
                            Statement statement = null;
                            ResultSet queryResult = null;

                            try {
                                DatabaseConnection connection = new DatabaseConnection();
                                connectDB = connection.getConnection();
                                statement = connectDB.createStatement();

                                queryResult = statement.executeQuery("SELECT email FROM user WHERE email = '" + adminEmail_SignUp.getText() + "'");
                                if (queryResult.next()) {
                                    // If the email address is already in use
                                    // Display a pop-up message
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("Invalid Email Address");
                                    alert.setHeaderText("The email address is already in use.");
                                    alert.setContentText("Please enter another email address.");
                                    alert.showAndWait();
                                } else {
                                    // If the email address is not in use
                                    // Send a verification code to user's Google account
                                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                    alert1.setTitle("Confess It Admin Account Registration");
                                    alert1.setHeaderText("Creating a new admin account...");
                                    alert1.setContentText("An email containing a verification code will be sent to your gmail.");
                                    alert1.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                                    Optional<ButtonType> button = alert1.showAndWait();
                                    if (button.isPresent() && button.get() == ButtonType.OK) {
                                        // If user clicks OK button
                                        // Send a confirmation code to user's Google account
                                        Email email = new Email();
                                        email.sendVerificationEmail(adminEmail_SignUp.getText(), "admin");

                                        // Create a dialog box and check the verification code entered by the user
                                        TextInputDialog textInputDialog = new TextInputDialog();
                                        textInputDialog.setTitle("Verification Code Has Been Successfully Sent To Your Gmail");
                                        textInputDialog.setHeaderText("Please check your gmail and enter the verification code to continue admin account registration");
                                        textInputDialog.setContentText("Verification code:");

                                        Optional<String> code = textInputDialog.showAndWait();
                                        if (code.isPresent() && code.get().equals(Integer.toString(email.getVerificationCode()))) {
                                            // If the verification code sent matches with the verification entered by the user,
                                            // Create an admin account
                                            createAdminAccount(adminEmail_SignUp.getText(), adminUsername_SignUp.getText(), adminPassword_SignUp.getText(), 1);

                                            // Display admin account created successfully pop-up message
                                            Alert alert = new Alert(Alert.AlertType.NONE);
                                            ImageView imagePane = new ImageView("com/fxml-resources/GreenTick.gif");
                                            imagePane.setFitWidth(150);
                                            imagePane.setPreserveRatio(true);
                                            alert.setGraphic(imagePane);
                                            alert.setTitle("Success");
                                            alert.setHeaderText("Your admin account has been successfully created.");
                                            alert.setContentText("Thank you for registering with Confess It");
                                            alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                                            alert.showAndWait();

                                            User admin = new User();
                                            admin.setUsername(adminUsername_SignUp.getText());
                                            admin.setEmail(adminEmail_SignUp.getText());
                                            adminList.add(admin);

                                            adminUsername_SignUp.clear();
                                            adminEmail_SignUp.clear();
                                            adminPassword_SignUp.clear();
                                            adminConfirmPassword_SignUp.clear();
                                            adminAgreePolicy_SignUp.setSelected(false);
                                        } else {
                                            // If the verification code entered does not match,
                                            // Display an error pop-up message
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("Error");
                                            alert.setHeaderText("The verification code entered does not match.");
                                            alert.setContentText("Please try again.");
                                            alert.showAndWait();
                                        }
                                    } else if (button.isPresent() && button.get() == ButtonType.CANCEL) {
                                        // If user clicks CANCEL button
                                        Alert alert = new Alert(Alert.AlertType.NONE);
                                        ImageView imagePane = new ImageView("com/fxml-resources/RedCross.gif");
                                        imagePane.setFitWidth(150);
                                        imagePane.setPreserveRatio(true);
                                        alert.setGraphic(imagePane);
                                        alert.setTitle("Error");
                                        alert.setHeaderText("Admin account registration unsuccessful.");
                                        alert.setContentText("Your admin account is not created successfully.");
                                        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                                        alert.showAndWait();
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } catch (MessagingException e) {
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
                            // If adminAgreePolicy check box is not selected
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Warning");
                            alert.setHeaderText("To create an admin account you must agree to the admin policy.");
                            alert.setContentText("Please ensure you agree to the admin policy.");
                            alert.showAndWait();
                        }
                    } else {
                        // If both password and confirm passwords are not match,
                        // Set "Please make sure both passwords are match" label as visible
                        adminMessageLabel_SignUp.setVisible(true);
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

    @FXML
    void moveToAdminConfirmPasswordField(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            adminConfirmPassword_SignUp.requestFocus();
        }
    }

    @FXML
    void moveToAdminEmailField(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            adminEmail_SignUp.requestFocus();
        }
    }

    @FXML
    void moveToAdminPasswordField(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            adminPassword_SignUp.requestFocus();
        }
    }

    @FXML
    void moveToAdminAgreePolicy_SignUp(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            adminAgreePolicy_SignUp.requestFocus();
        }
    }

    @FXML
    void moveToAdminSignUpButtonPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            adminSignUpButton.requestFocus();
        }
    }

    /***
     * Create an admin account (role = 1)
     * Insert the information entered by the user into database
     * @param email email address entered by the user
     * @param username username entered by the user
     * @param password password entered by the user
     */
    public void createAdminAccount(String email, String username, String password, int role) {
        String securePassword = null;

        try {
            SecurePassword secure = new SecurePassword();
            securePassword = secure.generateSecurePassword(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();

            statement = connectDB.createStatement();
            statement.executeUpdate("INSERT INTO user (username, email, password, role) VALUES ('" + username + "','" + email + "','" + securePassword + "','" + role + "')");

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
