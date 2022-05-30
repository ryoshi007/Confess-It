package com.confessit;

public class UserHolder {
    private User user;
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
}
