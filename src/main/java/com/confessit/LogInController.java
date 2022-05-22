package com.confessit;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LogInController {

    /***
     * To validate the information inputted by the user for log in purpose
     * @param email is the email inputted by the user
     * @param password is the password inputted by the user
     * @return boolean value whether the login is successful or not
     */
    public boolean validateLogin(String email, String password) {
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
                if (retrievedEmail.equals(email) && validatePassword(password, retrievedPassword)) {
                    // if email and password matches
                    return true;
                }
            } else {
                return false;
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

    /***
     * Validate the user-inputted password with the retrieved password from the database
     * @param inputPassword is the password inputted by the user
     * @param correctPassword is the password retrieved from the database
     * @return the boolean value whether the user-inputted password matches with the retrieved password
     */
    private boolean validatePassword(String inputPassword, String correctPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = correctPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);

        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(inputPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    /***
     * Convert hex string into byte array
     * @param hex is the string that made up of hex values
     * @return the byte array
     */
    private byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public User fetchUserInformation(String email) {

        User user = new User();

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
                user.setEmail(queryResult.getString("email"));
                user.setUsername(queryResult.getString("username"));
                user.setPassword(queryResult.getString("password"));
                user.setDateOfBirth(queryResult.getDate("dateofbirth"));
                user.setDescription(queryResult.getString("description"));
                user.setRole(queryResult.getInt("role"));
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
        return user;
    }
}
