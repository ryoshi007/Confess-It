package com.confessit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        cli();
        launch();
    }

    public static void cli() {
        System.out.println("Welcome to Confess It!");
        System.out.println("Please select an option:\n");
        System.out.println("1. Log In");
        System.out.println("2. Sign Up");
        System.out.print("\nInput: ");

        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        scanner.nextLine();
        boolean isAuthenticated = false;

        if (input == 1) {
            //Function related to Log In
            System.out.print("Email: ");
            String inputEmail = scanner.nextLine();
            System.out.print("Password: ");
            String inputPassword = scanner.nextLine();

            User currentUser = new User();

            //Fetch username and password from the database, if true let the user in
            LogInController logInController = new LogInController();
            isAuthenticated = logInController.validateLogin(inputEmail,inputPassword);

            if (isAuthenticated) {
                System.out.println("Login successful");
                currentUser = logInController.fetchUserInformation(inputEmail);
            }

            //Fetch the data from database to ensure everything is correct

            if (isAuthenticated) {
                System.out.println("\nInterface\n--------------------------------------------------");
                System.out.println("1. Submit a post");
                System.out.println("2. Search a post");
                System.out.println("3. Check pending posts");
                System.out.println("----------------------------------------------------");
                System.out.println("Today's Confession\n");

                //Fetch 3 random confession from the database

                MainPageController mainPage = new MainPageController();
                ArrayList<Post> recentPosts = mainPage.retrieveRecentPost(4);

                for (int i = 4; i - 4 < recentPosts.size(); i++) {
                    System.out.println(i + ". " + recentPosts.get(i - 4) + "\n");
                }

                System.out.print("Input: ");
                input = scanner.nextInt();

                switch (input) {
                    case 1:
                        //Function related to Add Post
                        SubmitPostController submitController = new SubmitPostController();
                        System.out.print("Content: ");
                        scanner.nextLine();
                        String content = scanner.nextLine();
                        submitController.submitPost(content);
                        //Fetch the data from database to ensure everything is correct
                        break;

                    case 2:
                        //Function related to Search Post;
                        System.out.println("Options:");
                        System.out.println("1. Keyword");
                        System.out.println("2. Date Time");
                        System.out.println("3. Date");
                        System.out.println("4. Post ID");
                        System.out.print("\nInput: ");

                        SearchPost searchPost = new SearchPost();
                        ArrayList<Post> results = null;
                        input = scanner.nextInt();
                        scanner.nextLine();
                        switch (input) {
                            case 1:
                                System.out.print("Keyword: ");
                                String keyword = scanner.nextLine();
                                results = searchPost.search("content", keyword);
                                break;
                            case 2:
                                System.out.print("Date (yyyy-mm-dd): ");
                                String inputDate = scanner.nextLine();
                                System.out.print("Time (0-23): ");
                                String inputTime = scanner.nextLine();
                                results = searchPost.search("datetime", inputDate + " " + inputTime);
                                break;
                            case 3:
                                System.out.print("Date (yyyy-mm-dd): ");
                                String date = scanner.nextLine();
                                results = searchPost.search("datetime", date);
                                break;
                            default:
                                System.out.print("Tag ID: ");
                                String tagid = scanner.nextLine();
                                results = searchPost.search("tagid", tagid);
                                break;
                        }

                        System.out.println();
                        for (int i = 1; i <= results.size(); i++) {
                            System.out.println(i + ". " + results.get(i - 1) + "\n");
                        }
                        break;

                    case 3:
                        AdminPageController adminPage = new AdminPageController();

                        //A retrieval of submitted post, for testing purpose
                        ArrayList<Post> pendingPost = adminPage.retrieveSubmittedPost();
                        for (int i = 1; i <= pendingPost.size(); i++) {
                            System.out.println(i + ". " + pendingPost.get(i - 1));
                            System.out.println("\n-----------------------------------");
                        }

                        System.out.println("\nOption: ");
                        System.out.println("a. Approve");
                        System.out.println("b. Delete");
                        System.out.print("\nInput: ");

                        scanner.nextLine();
                        String pendingOption = scanner.nextLine();
                        if (pendingOption.equalsIgnoreCase("a")) {
                            System.out.print("Index: ");
                            int inputID = scanner.nextInt();
                            adminPage.approve(inputID);
                        } else if (pendingOption.equalsIgnoreCase("b")) {
                            System.out.print("Index: ");
                            int inputID = scanner.nextInt();
                            adminPage.delete(inputID);
                        }
                        break;
                    default:
                        System.out.println("Thanks for using the app");
                        break;
                }
            } else {
                System.out.println("Please ensure that your email and password is correct.");
            }

        } else {
            System.out.println();

            SignUpController signUp = new SignUpController();
            //Function related to Sign In
            System.out.print("Email: ");
            String inputEmail = scanner.nextLine();
            System.out.print("Username: ");
            String inputUsername = scanner.nextLine();
            System.out.print("Password: ");
            String inputPassword = scanner.nextLine();
            System.out.print("Confirm Password: ");
            String inputConfirmPassword = scanner.nextLine();

            while(!signUp.verifyCorrectEmail(inputEmail) || !signUp.verifyStrongPassword(inputPassword)) {
                if (!signUp.verifyCorrectEmail(inputEmail)) {
                    System.out.println("Please ensure your email has the correct format.\n");
                    System.out.print("Email: ");
                    inputEmail = scanner.nextLine();
                } else if (!signUp.verifyStrongPassword(inputPassword)) {
                    System.out.println("Please ensure your password has minimum length of 8, contains at least " +
                            "1 lowercase, 1 uppercase, 1 special character and 1 digit.\n");
                    System.out.print("Password: ");
                    inputPassword = scanner.nextLine();
                    System.out.print("Confirm Password: ");
                    inputConfirmPassword = scanner.nextLine();
                }
            }

            if (inputPassword.equals(inputConfirmPassword)) {
                // if password and confirm password are the same
                SignUpController signUpController = new SignUpController();
                signUpController.createUserAccount(inputEmail,inputUsername,inputPassword);
                System.out.println("Your account is successfully created");
            } else {
                // if password and confirm password are different
                System.out.println("Password and confirm password are different");
            }
        }
    }

}