package com.financetracker;

import java.sql.*;

public class DatabaseHelper {
    // The database file will be created right in your project root folder
    private static final String URL = "jdbc:sqlite:finance.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static boolean newUser() {
        String sql = "SELECT COUNT(1) FROM transactions";

        try(Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            if (rs.next()) {
                int rowCount = rs.getInt(1);
                // If rowCount is 0, they have never entered a transaction before
                return rowCount == 0;
            }

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }
        // default to false so the correct message is displayed when the user loads.
        return false;
    }


    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS transactions ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " amount REAL NOT NULL,"
                + " category TEXT NOT NULL,"
                + " description TEXT,"
                + " dateTime TEXT NOT NULL"
                + ");";
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS profile ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT NOT NULL,"
                + " salary REAL not NULL,"
                + " timeOfAccountCreation TEXT NOT NULL"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTableSQL);
            stmt.execute(createTableSQL);
            System.out.println("Database initialized and table verified.");
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }

}
