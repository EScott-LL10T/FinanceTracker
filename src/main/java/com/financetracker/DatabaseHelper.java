package com.financetracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    // The database file will be created right in your project root folder
    private static final String URL = "jdbc:sqlite:finance.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }


    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS transactions ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " amount REAL NOT NULL,"
                + " category TEXT NOT NULL,"
                + " description TEXT"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Database initialized and table verified.");
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }
}
