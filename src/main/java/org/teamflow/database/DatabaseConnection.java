package org.teamflow.database;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static Connection connection;

    static {
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String dbName = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try {
            Connection init = DriverManager.getConnection(url, user, password);
            Statement stmt = init.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.close();
            init.close();

            connection = DriverManager.getConnection(url + dbName, user, password);
            System.out.println("Connected to DB successfully!");
        } catch (Exception e) {
            System.out.println("Database connection failed:" + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }
    public static void query(String query) throws SQLException {
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String dbName = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        Connection connection = DriverManager.getConnection(url + dbName, user, password);

        if (!query.isEmpty()) {
            connection.createStatement().executeUpdate(query);
        }
    }
}
