package com.confessit;

public class UserHolder {
    private User user;
    private int currentPageNumber = -1;
    private final static UserHolder INSTANCE = new UserHolder();
    private String currentPage = "";
    private String correctSearchInput = "";
    private String searchCategory = "";

    private String userSearchInput = "";
    private boolean isAdmin = false;

    UserHolder() {};

    public static UserHolder getInstance() {
        return INSTANCE;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void setCurrentPageNumber(int number) {
        this.currentPageNumber = number;
    }

    public int getCurrentPageNumber() {
        return this.currentPageNumber;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getCorrectSearchInput() {
        return correctSearchInput;
    }

    public String getSearchCategory() {
        return searchCategory;
    }

    public void setCorrectSearchInput(String correctSearchInput) {
        this.correctSearchInput = correctSearchInput;
    }

    public void setSearchCategory(String searchCategory) {
        this.searchCategory = searchCategory;
    }

    public String getUserSearchInput() {
        return userSearchInput;
    }

    public void setUserSearchInput(String userSearchInput) {
        this.userSearchInput = userSearchInput;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void clearInfo() {
        this.user = null;
        currentPageNumber = -1;
        currentPage = "";
        correctSearchInput = "";
        userSearchInput = "";
        isAdmin = false;
    }
}
