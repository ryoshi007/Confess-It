package com.confessit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for the user's menu bar
 */
public class MenuBarControlller {

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
     * A button that directs user to the home page
     */
    @FXML
    private Button homepageButton;

    /**
     * A button that directs user to log in page
     */
    @FXML
    private Button logOutButton;

    /**
     * A button that directs user to the profile page
     */
    @FXML
    private Button profilePageButton;

    /**
     * A button that directs user to the search page
     */
    @FXML
    private Button searchButton;

    /**
     * A button that directs user to submit confessions page
     */
    @FXML
    private Button writeConfessionButton;

    /**
     * Container to display the menu bar buttons
     */
    @FXML
    private HBox menubar;

    /**
     * Direct user to the home page
     * @param event Mouse Click
     */
    @FXML
    void goToHomepage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main-Page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Direct user to sign up page
     * @param event Mouse Click
     */
    @FXML
    void goToLogOut(MouseEvent event) throws IOException {
        UserHolder.getInstance().clearInfo();

        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login_page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Direct user to the profile page
     * @param event Mouse Click
     */
    @FXML
    void goToProfilePage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Profile-Page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Direct user to the search page
     * @param event Mouse Click
     */
    @FXML
    void goToSearchPage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Search-Page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Direct user to submit confession post page
     * @param event Mouse Click
     */
    @FXML
    void goToSubmitPage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Submit-Post.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
