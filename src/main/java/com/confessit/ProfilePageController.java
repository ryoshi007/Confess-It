package com.confessit;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ProfilePageController {

    /**
     * Change user username and update it in database
     * @param user a User object
     * @param username new username entered by the user
     */
    public void editUsername(User user, String username) {
        if (user.getUsername().equals(username)) {
            System.out.println("Please enter a different username");
        } else {
            Connection connectDB = null;

            try {
                DatabaseConnection connection = new DatabaseConnection();
                connectDB = connection.getConnection();
                String sql = "UPDATE user SET username = ? WHERE email = ?";
                PreparedStatement statement = connectDB.prepareStatement(sql);

                statement.setString(1, username);
                statement.setString(2, user.getEmail());
                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();

            } finally {
                if (connectDB != null) {
                    try {
                        connectDB.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Change user account password and update it in database
     * @param user a User object
     * @param passwordInput new password entered by the user
     */
    public void changeAccountPassword(User user, String passwordInput) {

        //To check the original password is the same as the inputted password
        //If yes, return
        try {
            if (validatePassword(passwordInput,user.getPassword())) {
                System.out.println("Please use a different password");
                return;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        //Encrypt the inputted password
        String securePassword = null;
        try {
            securePassword = generateSecurePassword(passwordInput);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }


        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "UPDATE user SET password = ? WHERE email = ?";
            PreparedStatement statement = connectDB.prepareStatement(sql);

            statement.setString(1, securePassword);
            statement.setString(2, user.getEmail());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
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
     * Edit user description and store it in database
     * @param user a User object
     * @param description user description entered by the user
     */
    public void editUserDescription(User user, String description) {
        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "UPDATE user SET description = ? WHERE email = ?";
            PreparedStatement statement = connectDB.prepareStatement(sql);

            statement.setString(1, description);
            statement.setString(2, user.getEmail());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
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
     * Edit user date of birth and update it in database
     * @param user a User object
     * @param dateOfBirth user date of birth entered by the user
     */
    public void editUserDateOfBirth(User user, String dateOfBirth) {

        java.sql.Date sqlDate;

        if (!valDateOfBirth(dateOfBirth)) {
            // if the date entered by the user is invalid
            return;
        } else {
            // if it is valid, convert and store it in sql Date type
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy"); // 1-1-2001
            java.util.Date date = null;
            try {
                date = sdf1.parse(dateOfBirth);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            sqlDate = new java.sql.Date(date.getTime());
        }

        Connection connectDB = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "UPDATE user SET dateofbirth = ? WHERE email = ?";
            PreparedStatement statement = connectDB.prepareStatement(sql);

            statement.setDate(1, sqlDate);
            statement.setString(2, user.getEmail());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
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
     * Check whether user current account password is the same as the new password entered by the user
     * @param inputPassword new password entered by the user
     * @param currentPassword user current account password
     * @return true if password entered by the user is the same as user current account password
     */
    private boolean validatePassword(String inputPassword, String currentPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = currentPassword.split(":");
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

    /***
     * Generate a secure password by implementing PBKDF2 algorithm
     * @param originalPassword is the password inputted by the user
     * @return an encrypted password
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

    /**
     * Check whether date of birth entered by the user is valid
     * @param dateOfBirth date of birth entered by the user
     * @return true if date is valid, else false
     */
    private boolean valDateOfBirth(String dateOfBirth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(dateOfBirth);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }
}
