package com.confessit;

public class UserHolder {
    private User user;
    private int currentPageNumber = -1;
    private final static UserHolder INSTANCE = new UserHolder();

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

    public void clearInfo() {
        this.user = null;
        currentPageNumber = -1;
    }
}
