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
 * A controller for the menu bar
 */
public class AdminMenuBarController {
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
     * A button that directs admin to add admin page
     */
    @FXML
    private Button addUserButton;

    /**
     * A button that directs admin to remove post page
     */
    @FXML
    private Button batchRemovalButton;

    /**
     * A button that allows admin to log out from the account
     */
    @FXML
    private Button logOutButton;

    /**
     * A container for displaying the buttons
     */
    @FXML
    private HBox menubar;

    /**
     * A button that directs admin to approve post
     */
    @FXML
    private Button pendingPageButton;

    /**
     * A button that directs admin to profile page
     */
    @FXML
    private Button profilePageButton;

    /**
     * Direct admin to add admin page
     * @param event Mouse click
     * @throws IOException when there is error running the code
     */
    @FXML
    void goToAdminListPage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Admin-List-Page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Direct admin to remove posts page
     * @param event Mouse Click
     * @throws IOException when there is error running the code
     */
    @FXML
    void goToBatchRemovalPage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Batch-Removal-Page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Direct admin to log in page
     * @param event Mouse Click
     * @throws IOException when there is error running the code
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
     * Direct admin to approve post page
     * @param event Mouse Click
     * @throws IOException when there is error running the code
     */
    @FXML
    void goToPendingPanel(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin-page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Direct admin to profile page
     * @param event Mouse Click
     * @throws IOException when there is error running the code
     */
    @FXML
    void goToProfilePage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Profile-Page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}

