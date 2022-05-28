package com.confessit;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController extends CreateAccount{

    /***
     * Create a user account (role = 0)
     * Insert the information inputted by the user into database for creating account
     * @param email is the email inputted by the user
     * @param username is the username inputted by the user
     * @param password is the password inputted by the user
     */
    public void createUserAccount(String email, String username, String password) {
        super.createAccount(email, username, password, 0);
    }
}
