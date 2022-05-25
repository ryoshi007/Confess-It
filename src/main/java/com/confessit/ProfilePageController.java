package com.confessit;

import java.security.NoSuchAlgorithmException;
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
        SecurePassword secure = new SecurePassword();

        //To check the original password is the same as the inputted password
        //If yes, return
        try {
            if (secure.validatePassword(passwordInput,user.getPassword())) {
                System.out.println("Please use a different password");
                return;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        //Encrypt the inputted password
        String securePassword = null;
        try {
            securePassword = secure.generateSecurePassword(passwordInput);
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
