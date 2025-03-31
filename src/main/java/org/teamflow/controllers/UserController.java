package org.teamflow.controllers;
import org.teamflow.database.DatabaseConnection;

import java.sql.SQLException;


public class UserController {
    private boolean isLoggedIn = false;
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }


    public UserController() {
        String userTableQuery = """
            CREATE TABLE IF NOT EXISTS user (
                id INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try {
            DatabaseConnection.query(userTableQuery);
            System.out.println("User table ensured.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user table", e);
        }
    }

    public boolean registerUser(String username) throws Exception{

        return false;
    }
}
