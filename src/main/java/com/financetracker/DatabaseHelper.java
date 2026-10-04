package com.financetracker;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
                + " profileId INTEGER NOT NULL,"
                + " amount REAL NOT NULL,"
                + " categoryId INTEGER NOT NULL,"
                + " description TEXT NOT NULL,"
                + " createdAt TEXT NOT NULL,"
                + " FOREIGN KEY (profileId) REFERENCES profile(id),"
                + " FOREIGN KEY (categoryId) REFERENCES categories(id)"
                + ");";
        String createUsersTableSQL =
                "CREATE TABLE IF NOT EXISTS profile ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT NOT NULL,"
                + " roleId INTEGER NOT NULL," // role will be student/ employed.
                + " debt REAL not NULL DEFAULT 0,"
                + " salary REAL not NULL,"
                + " createdAt TEXT NOT NULL,"
                + " FOREIGN KEY (roleId) REFERENCES roles(id)"
                + ");";

        String createCategoryTableSQL =
                "CREATE TABLE IF NOT EXISTS categories ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT NOT NULL UNIQUE"
                + ");";

        String createRoleTableSQL =
                "CREATE TABLE IF NOT EXISTS roles ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT NOT NULL UNIQUE"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createCategoryTableSQL);
            stmt.execute(createRoleTableSQL);
            stmt.execute(createUsersTableSQL);
            stmt.execute(createTableSQL);
            insertDefaultRoles();
            System.out.println("Database initialized and table verified.");
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }

    public static void insertDefaultRoles() {

        String sql = "INSERT OR IGNORE INTO roles (name) VALUES (?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "Student");
            stmt.executeUpdate();

            stmt.setString(1, "Employed");
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }
    }

    /* ****************************************************************************************************************/
    /*                                               PROFILE TABLE                                                    */
    /* ****************************************************************************************************************/

    public static int getRoleId(String role) {
        String sql = "SELECT id FROM roles WHERE name = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } // need to add else statement to add role to roles database if not found
            }

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }

        return -1;
    }

    public static void createNewUser(String name, String role, double debt, double salary){
        int roleId = getRoleId(role);

        if (roleId == -1) {
            System.out.println("SQL error occurred");
            return;
        }

        String sql = "INSERT INTO profile "
                + "(name, roleId, debt, salary, createdAt) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, roleId);
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
        String sql = """
            SELECT profile.*, roles.name AS role
            FROM profile
            JOIN roles ON profile.roleId = roles.id
            """;
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String name = rs.getString("name");
                String role = rs.getString("role");
                double debt = rs.getDouble("debt");
                double salary = rs.getDouble("salary");
                int id = rs.getInt("id");
                String timeOfAccountCreation =
                        rs.getString("timeOfAccountCreation");
                return new Profile(name, role, debt, salary, timeOfAccountCreation, id);
            }

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }
        return null;
    }


    public static void updateProfile(int id,String name, String role, double debt, double salary) {

        int roleId = getRoleId(role);

        if (roleId == -1) {
            System.out.println("Role not found.");
            return;
        }


        String sql = "UPDATE profile SET name = ?, roleId = ?, debt = ?, salary = ? WHERE id = ? ";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, roleId);
            stmt.setDouble(3, debt);
            stmt.setDouble(4, salary);
            stmt.setInt(5, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }
    }

    /* ****************************************************************************************************************/
    /*                                         TRANSACTIONS TABLE                                                     */
    /* ****************************************************************************************************************/


    public static int getCategoryId(String category) {

        String sql = "SELECT id FROM categories WHERE name = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

            return addCategory(category);

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }

        return -1;
    }

    public static int addCategory(String category) {

        String sql = "INSERT INTO categories (name) VALUES (?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }

        return -1;
    }




    public static void addTransaction(double amount, String category, String description, String createdAt){

        int categoryId = getCategoryId(category);

        if (categoryId == -1) {
            System.out.println("Category not found.");
            return;
        }


        String sql = "INSERT INTO transactions "
                + "(profileId, amount, categoryId, description, createdAt) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, amount);
            stmt.setInt(2, categoryId);
            stmt.setString(3, description);
            stmt.setString(4, createdAt);
            stmt.setString(5, createdAt);

            stmt.executeUpdate();

        }catch(SQLException e){
            System.out.println("SQL error, " + e.getMessage());
        }
    }


    public static ArrayList<Transaction> getTransactions(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = """
            SELECT transactions.amount,
                   categories.name AS category,
                   transactions.description,
                   transactions.createdAt
            FROM transactions
            JOIN categories
                ON transactions.categoryId = categories.id
            """;
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while(rs.next()) {
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                String description = rs.getString("description");
                String dateTime = rs.getString("dateTime");
                transactions.add(new Transaction(amount, category, description, dateTime));
            }

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }
        return transactions;

    }

    public static void deleteTransactions(int profileId) {
        String sql = "DELETE FROM transactions WHERE profileId = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profileId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
        }
    }

}