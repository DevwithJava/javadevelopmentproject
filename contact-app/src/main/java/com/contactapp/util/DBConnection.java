package com.contactapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * TASK 1 — updated to add category, favorite and photo_path columns automatically.
 */
public class DBConnection {

    private static final String URL = "jdbc:sqlite:contact.db";
    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            migrateDatabase(connection);
        }
        return connection;
    }

    private static void migrateDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try {
                stmt.execute("ALTER TABLE person ADD COLUMN category VARCHAR(20) NOT NULL DEFAULT 'Other'");
                System.out.println("[DB] Added 'category' column.");
            } catch (SQLException e) { }

            try {
                stmt.execute("ALTER TABLE person ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0");
                System.out.println("[DB] Added 'favorite' column.");
            } catch (SQLException e) { }

            try {
                stmt.execute("ALTER TABLE person ADD COLUMN photo_path VARCHAR(500) NULL");
                System.out.println("[DB] Added 'photo_path' column.");
            } catch (SQLException e) { }

        } catch (SQLException e) {
            System.err.println("[DB] Migration error: " + e.getMessage());
        }
    }
}