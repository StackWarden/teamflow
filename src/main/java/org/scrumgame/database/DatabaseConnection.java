package org.scrumgame.database;

import io.github.cdimascio.dotenv.Dotenv;
import org.scrumgame.database.FileUtil;
import java.io.IOException;
import java.sql.*;

public class DatabaseConnection {
    private static Connection connection;
    private static boolean initialized = false;

    private static final Dotenv dotenv = Dotenv.load();
    private static final String url = dotenv.get("DB_URL");
    private static final String dbName = dotenv.get("DB_NAME");
    private static final String user = dotenv.get("DB_USER");
    private static final String password = dotenv.get("DB_PASSWORD");

    public static synchronized Connection getConnection() {
        try {
            if (!initialized) {
                initializeDatabase();
                initialized = true;
            }

            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url + dbName, user, password);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get DB connection", e);
        }
        return connection;
    }

    public static void checkConnection() {
        try {
            Connection conn = getConnection();

            if (!conn.isValid(0)) {
                throw new SQLException("Database connection is not valid.");
            }
        } catch (Exception e) {
            System.out.println("Database connection check failed: " + e.getMessage());
            throw new RuntimeException("Cannot continue without DB connection.", e);
        }
    }

    private static void initializeDatabase() {
        boolean shouldRunSeeder = false;

        try {
            Connection checkConn = DriverManager.getConnection(url, user, password);
            ResultSet rs = checkConn.createStatement().executeQuery(
                    "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbName + "'"
            );

            if (!rs.next()) {
                Statement stmt = checkConn.createStatement();
                stmt.executeUpdate("CREATE DATABASE " + dbName);
                stmt.close();
                shouldRunSeeder = true;
            }

            rs.close();
            checkConn.close();

            connection = DriverManager.getConnection(url + dbName, user, password);

            executeSQLFromFile(connection, "src/main/java/org/teamflow/database/tables/database_query.sql");

            if (shouldRunSeeder) {
                executeSQLFromFile(connection, "src/main/java/org/teamflow/database/tables/seeder.sql");
                System.out.println("Seed data inserted.");
            }
        } catch (Exception e) {
            System.out.println("Database setup failed: " + e.getMessage());
            throw new RuntimeException("Initialization failed", e);
        }
    }

    private static void executeSQLFromFile(Connection connection, String filepath) throws IOException, SQLException {
        String sql = FileUtil.readSQLFile(filepath);
        String[] queries = sql.split(";");

        try (Statement stmt = connection.createStatement()) {
            for (String raw : queries) {
                String query = raw.trim();
                if (query.isEmpty()) continue;

                try {
                    stmt.execute(query + ";");
                } catch (SQLException e) {
                    System.err.println("Skipping failed query:");
                    System.err.println(query);
                    System.err.println("Reason: " + e.getMessage());
                }
            }
        }
    }
}
