package com.confessit;

import com.mysql.cj.Query;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

public class SignUpController {

    /***
     * Insert the information inputted by the user into database for creating account
     * @param email is the email inputted by the user
     * @param username is the username inputted by the user
     * @param password is the password inputted by the user
     */
    public void createUserAccount(String email, String username, String password) {
        String securePassword = null;
        try {
            securePassword = generateSecurePassword(password);
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

            queryResult = statement.executeQuery("SELECT email FROM user WHERE email= '" + email + "'");
            if (queryResult.next()) {
                // If the email address is in use,
                System.out.println("The account is in use");
            } else {
                // If the email address is not in user
                statement = connectDB.createStatement();
                statement.executeUpdate("INSERT INTO user (username, email, password) VALUES ('" + username + "','" + email + "','" + securePassword + "')");
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
    }

    /***
     * Validate the email inputted by the user is in correct format
     * @param emailInput the email inputted by the user
     * @return a boolean value to verify the email has the correct format
     */
    public boolean verifyCorrectEmail(String emailInput) {
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
    public boolean verifyStrongPassword(String passwordInput) {
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

    /***
     * Generate a secure password by implementing PBKDF2 algorithm
     * @param originalPassword the password inputted by the user
     * @return an encrypted password
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private String generateSecurePassword(String originalPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = originalPassword.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    /***
     * Generate the salt for the encrypted password
     * @return the salt for the encrypted password
     * @throws NoSuchAlgorithmException
     */
    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    /***
     * Convert array of bits into hex string
     * @param array consists of bytes of elements
     * @return hex string
     */
    private String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);

        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }
}
