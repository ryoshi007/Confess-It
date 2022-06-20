package com.confessit;

import java.util.Date;

/**
 * A class that used to store user personal information
 */
public class User {

    /**
     * Account username
     */
    private String username;

    /**
     * Account email address
     */
    private String email;

    /**
     * Account password
     */
    private String password;

    /**
     * User's date of birth
     */
    private Date dateOfBirth;

    /**
     * User's description
     */
    private String description;

    /**
     * User's role (0 = user, 1 = admin)
     */
    private int role;

    /**
     * A no-arg constructor
     */
    public User() {
    }

    /**
     *
     * @param username username of the user account
     * @param email email address of the user account
     * @param password password of the user account
     * @param dateOfBirth date of birth of the user
     * @param description description of the user
     * @param role role of the user account (1 = Admin or 0 = User)
     */
    public User(String username, String email, String password, Date dateOfBirth, String description, int role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.description = description;
        this.role = role;
    }

    /**
     * get the username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * set the username
     * @param username username of the user account
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * get the user email address
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * set the user email address
     * @param email email address of the user account
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * get the password of the user account
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * set the password of the user account
     * @param password password of the user account
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * get the date of birth of the user
     * @return date of birth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * set the date of birth of the user
     * @param dateOfBirth date of birth of the user
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * get the user description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * get the user description
     * @param description user description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get the role of the user
     * @return role (1 = Admin or 0 = User)
     */
    public int getRole() {
        return role;
    }

    /**
     * set the role of the user
     * @param role role (1 = Admin or 0 = User)
     */
    public void setRole(int role) {
        this.role = role;
    }

}
