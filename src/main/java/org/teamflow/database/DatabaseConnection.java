package org.teamflow.database;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to DB successfully!");
            connection.close();
        } catch (Exception e) {
            System.out.println("Database connection failed:");
            e.printStackTrace();
        }
    }
}
