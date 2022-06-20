package com.confessit;

/**
 * A class to hold the user operation information
 */
public class UserHolder {

    /**
     * Save the User object
     */
    private User user;

    /**
     * Save the current page browsed by user on the home page
     */
    private int currentPageNumber = -1;

    /**
     * Constructor to access the class
     */
    private final static UserHolder INSTANCE = new UserHolder();

    /**
     * Save the name of the current accessed page by the user
     */
    private String currentPage = "";

    /**
     * Save the corrected form of the search input by the user
     */
    private String correctSearchInput = "";

    /**
     * Save the search category by the user
     */
    private String searchCategory = "";

    /**
     * Save th search input by the user
     */
    private String userSearchInput = "";

    /**
     * Save the role of the user
     */
    private boolean isAdmin = false;

    /**
     * Empty constructor
     */
    UserHolder() {};

    /**
     * Get the class methods
     * @return the instance of this class
     */
    public static UserHolder getInstance() {
        return INSTANCE;
    }

    /**
     * Set the logged-in user
     * @param user is the user that logged in
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the User object
     * @return the User Object
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Set the page number browsed by user in the home page
     * @param number is the last page number browsed by user
     */
    public void setCurrentPageNumber(int number) {
        this.currentPageNumber = number;
    }

    /**
     * Get the page number browsed by user in the home page
     * @return the last page number browsed by user
     */
    public int getCurrentPageNumber() {
        return this.currentPageNumber;
    }

    /**
     * Get the current page accessed by the user
     * @return the page name accessed by the user
     */
    public String getCurrentPage() {
        return currentPage;
    }

    /**
     * Set the current page accessed by the user
     * @param currentPage is the current page name accessed by the user
     */
    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Get the corrected form of search input by the user
     * @return the corrected form of search input by the user
     */
    public String getCorrectSearchInput() {
        return correctSearchInput;
    }

    /**
     * Get the search category chose by the user
     * @return the search category chose by the user
     */
    public String getSearchCategory() {
        return searchCategory;
    }

    /**
     * Set the corrected form of search input by the user
     * @param correctSearchInput is the corrected form of the search input by the user
     */
    public void setCorrectSearchInput(String correctSearchInput) {
        this.correctSearchInput = correctSearchInput;
    }

    /**
     * Set the search category by the user
     * @param searchCategory is the category chose by the user
     */
    public void setSearchCategory(String searchCategory) {
        this.searchCategory = searchCategory;
    }

    /**
     * Get the search term inputted by the user
     * @return the search term inputted by the user
     */
    public String getUserSearchInput() {
        return userSearchInput;
    }

    /**
     * Set the search term from the user
     * @param userSearchInput is the search term inputted by the user
     */
    public void setUserSearchInput(String userSearchInput) {
        this.userSearchInput = userSearchInput;
    }

    /**
     * Get the boolean value for the user's role
     * @return the boolean value of the user's role
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Set the user's role
     * @param admin is boolean value whether the person logged in is user or admin
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Clear all the instances in this class
     */
    public void clearInfo() {
        this.user = null;
        currentPageNumber = -1;
        currentPage = "";
        correctSearchInput = "";
        userSearchInput = "";
        isAdmin = false;
    }
}
