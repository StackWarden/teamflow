package org.teamflow.database;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String dbName = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try {
            Connection initialConnection = DriverManager.getConnection(url, user, password);
            Statement stmt = initialConnection.createStatement();

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.close();
            initialConnection.close();
            System.out.println("Database ensured.");

            Connection connection = DriverManager.getConnection(url + dbName, user, password);
            System.out.println("Connected to DB successfully!");
            connection.close();
        } catch (Exception e) {
            System.out.println("Database connection failed:" + e.getMessage());
        }
    }
}
