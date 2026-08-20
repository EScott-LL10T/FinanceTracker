package com.financetracker;

import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseHelper {
    // The database file will be created right in your project root folder
    private static final String URL = "jdbc:sqlite:finance.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String createTableSQL =
                "CREATE TABLE IF NOT EXISTS transactions ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " amount REAL NOT NULL,"
                + " category TEXT NOT NULL,"
                + " description TEXT,"
                + " dateTime TEXT NOT NULL"
                + ");";
        String createUsersTableSQL =
                "CREATE TABLE IF NOT EXISTS profile ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT NOT NULL,"
                + " role TEXT NOT NULL," // role will be student/ employed.
                + " debt REAL not NULL,"
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

    /* ****************************************************************************************************************/
    /*                                               PROFILE TABLE                                                    */
    /* ****************************************************************************************************************/

    public static void createNewUser(String name, String role, double debt, double salary){
        String sql = "INSERT INTO profile "
                + "(name, role, debt, salary, timeOfAccountCreation) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, role);
            stmt.setDouble(3, debt);
            stmt.setDouble(4, salary);
            stmt.setString(5, LocalDateTime.now().toString());

            stmt.executeUpdate();

        }catch(SQLException e){
            System.out.println("SQL error, " + e.getMessage());
        }
    }

    public static boolean newUser() {
        String sql = "SELECT COUNT(1) FROM profile ";

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

    public static Profile getProfile(){
        String sql = "SELECT name, role, debt, salary, timeOfAccountCreation FROM profile";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String name = rs.getString("name");
                String role = rs.getString("role");
                double debt = rs.getDouble("debt");
                double salary = rs.getDouble("salary");
                String timeOfAccountCreation =
                        rs.getString("timeOfAccountCreation");
                return new Profile(name, role, debt, salary, timeOfAccountCreation);
            }

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }
        return null;
    }

    /* ****************************************************************************************************************/
    /*                                         TRANSACTIONS TABLE                                                     */
    /* ****************************************************************************************************************/

    public static void addTransaction(int amount, String category, String description, String dateTime){
        String sql = "INSERT INTO transactions "
                + "(amount, category, description, dateTime) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, amount);
            stmt.setString(2, category);
            stmt.setString(3, description);
            stmt.setString(4, dateTime);

            stmt.executeUpdate();

        }catch(SQLException e){
            System.out.println("SQL error, " + e.getMessage());
        }
    }

}