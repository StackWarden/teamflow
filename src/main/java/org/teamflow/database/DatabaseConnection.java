package org.teamflow.database;

import io.github.cdimascio.dotenv.Dotenv;
import org.teamflow.FileUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static Connection connection;
    private static final Dotenv dotenv = Dotenv.load();
    private static final String url = dotenv.get("DB_URL");
    private static final String dbName = dotenv.get("DB_NAME");
    private static final String user = dotenv.get("DB_USER");
    private static final String password = dotenv.get("DB_PASSWORD");

    static {
        try {
            Connection init = DriverManager.getConnection(url, user, password);
            Statement stmt = init.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.close();
            init.close();

            connection = DriverManager.getConnection(url + dbName, user, password);
            System.out.println("Connected to DB successfully!");

            String schemaSQL = FileUtil.readSQLFile("src/main/java/org/teamflow/database/tables/database_query.sql");
            Statement schemaStmt = connection.createStatement();
            schemaStmt.executeUpdate(schemaSQL);
            schemaStmt.close();
            System.out.println("Database schema executed.");

            String seedSQL = FileUtil.readSQLFile("src/main/java/org/teamflow/database/tables/seeder.sql");
            Statement seedStmt = connection.createStatement();
            seedStmt.executeUpdate(seedSQL);
            seedStmt.close();
            System.out.println("Seed data inserted.");

        } catch (Exception e) {
            System.out.println("Database setup failed: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url + dbName, user, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get DB connection", e);
        }
        return connection;
    }
    public static void query(String query) throws SQLException {
        Connection connection = DriverManager.getConnection(url + dbName, user, password);

        if (!query.isEmpty()) {
            connection.createStatement().executeUpdate(query);
        }
    }
}
