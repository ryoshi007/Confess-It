package com.confessit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

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

            //Fetch username and password from the database, if true let the user in
            LogInController logInController = new LogInController();
            isAuthenticated = logInController.validateLogin(inputEmail,inputPassword);

            if (isAuthenticated) {
                System.out.println("Login successful");
            }

            //Fetch the data from database to ensure everything is correct
            //Implement the password hashing function here
            if (isAuthenticated) {
                System.out.println("Interface\n--------------------------------------------------");
                System.out.println("1. Submit a post");
                System.out.println("2. Search a post");

                System.out.println("\n\n----------------------------------------------------");
                System.out.println("Today's Confession");

                //Fetch 3 random confession from the database
                System.out.println("3. ");
                System.out.println("4. ");
                System.out.println("5. ");
                System.out.print("\nInput: ");
                input = scanner.nextInt();

                switch (input) {
                    case 1:
                        //Function related to Add Post
                        SubmitPostController submitController = new SubmitPostController();
                        System.out.print("Content: ");
                        scanner.nextLine();
                        String content = scanner.nextLine();
                        System.out.println(content);
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

                        input = scanner.nextInt();
                        switch (input) {
                            case 1:
                                //Search via keyword
                                break;
                            case 2:
                                //Search via date time
                                break;
                            case 3:
                                //Search via Date
                                break;
                            default:
                                //Search via Post ID
                                break;

                        }
                        break;
                    case 3:
                        //Print detail of a post
                        break;
                    case 4:
                        //Print detail of a post
                        break;
                    default:
                        //Print detail of a post
                        break;
                }
            } else {
                System.out.println("Please ensure that your email and password is correct.");
            }

        } else {
            System.out.println();

            //Function related to Sign In
            System.out.print("Email: ");
            String inputEmail = scanner.nextLine();
            System.out.print("Username: ");
            String inputUsername = scanner.nextLine();
            System.out.print("Password: ");
            String inputPassword = scanner.nextLine();
            System.out.print("Confirm Password: ");
            String inputConfirmPassword = scanner.nextLine();

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