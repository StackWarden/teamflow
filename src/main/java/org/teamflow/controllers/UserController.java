package org.teamflow.controllers;

public class UserController {
    private boolean isLoggedIn = false;
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean registerUser(String username) {

        return false;
    }
}
