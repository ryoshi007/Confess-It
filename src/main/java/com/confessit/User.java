package com.confessit;

import java.util.ArrayList;
import java.util.Date;

/**
 * A class that used to store user personal information
 */
public class User {

    private String username;
    private String email;
    private String password;
    private Date dateOfBirth;
    private char gender;
    private String description;
    private ArrayList<Integer> history = new ArrayList<>();
    private String role;
    private ArrayList<Integer> archive = new ArrayList<>();

    /**
     *
     * @param username username of the user account
     * @param email email address of the user account
     * @param password password of the user account
     * @param dateOfBirth date of birth of the user
     * @param gender gender of the user
     * @param description description of the user
     * @param role role of the user account (Admin or User)
     */
    public User(String username, String email, String password, Date dateOfBirth, char gender, String description, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
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
     * get the gender of the user
     * @return gender
     */
    public char getGender() {
        return gender;
    }

    /**
     * set the gender of the user
     * @param gender gender of the user
     */
    public void setGender(char gender) {
        this.gender = gender;
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
     * get the tag IDs of post that posted by the user
     * @return history
     */
    public ArrayList<Integer> getHistory() {
        return history;
    }

    /**
     * add a post that posted by the user by passing tag ID of the post
     * @param tagID tag ID of a post
     */
    public void addToHistory(int tagID) {
        history.add(tagID);
    }

    /**
     * remove a specific post that posted by the user by passing tag ID of the post
     * @param tagID tag ID of a post
     */
    public void removeFromHistory(int tagID) {
        history.remove(tagID);
    }

    /**
     * get the role of the user
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * set the role of the user
     * @param role role (Admin or User)
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * get the tag ID of a post that saved by the user
     * @return archive
     */
    public ArrayList<Integer> getArchive() {
        return archive;
    }

    /**
     * add a post to archive by passing tag ID of a post
     * @param tagID tag ID of a post
     */
    public void addToArchive(int tagID) {
        archive.add(tagID);
    }

    /**
     * remove a post from archive by passing tag ID of a post
     * @param tagID tag ID of a post
     */
    public void removeFromArchive(int tagID) {
        archive.remove(tagID);
    }
}
