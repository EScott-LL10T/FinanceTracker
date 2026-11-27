package com.financetracker;


import com.financetracker.errors.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseHelper {
    // The database file will be created right in your project root folder
    private static final String URL = "jdbc:sqlite:finance.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /* Money is stored in REAL columns. Every conversion between BigDecimal and the database goes through */
    /* these two methods, so the storage format can be changed in one place.                              */

    private static void setMoney(PreparedStatement stmt, int index, BigDecimal amount) throws SQLException {
        stmt.setDouble(index, Money.of(amount).doubleValue());
    }

    private static BigDecimal getMoney(ResultSet rs, String column) throws SQLException {
        return Money.of(rs.getDouble(column));
    }

    public static boolean initializeDatabase() {

        // sql strings for creating the table if not already created normalized to 3NF/BCNF
        String createTableSQL = """
        CREATE TABLE IF NOT EXISTS transactions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        profileId INTEGER NOT NULL,
        amount REAL NOT NULL,
        categoryId INTEGER NOT NULL,
        description TEXT NOT NULL,
        createdAt VARCHAR(255) NOT NULL,
        transactionTypeId INTEGER NOT NULL,
        FOREIGN KEY (profileId) REFERENCES profile(id),
        FOREIGN KEY (categoryId) REFERENCES categories(id),
        FOREIGN KEY (transactionTypeId) REFERENCES transactionType(id)
        );
        """;

        String createProfileTableSQL = """
        CREATE TABLE IF NOT EXISTS profile (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        roleId INTEGER NOT NULL,
        debt REAL not NULL DEFAULT 0,
        salary REAL not NULL,
        createdAt VARCHAR(255) NOT NULL,
        FOREIGN KEY (roleId) REFERENCES roles(id)
        );
        """;

        String createCategoryTableSQL = """
        CREATE TABLE IF NOT EXISTS categories (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name VARCHAR(255) NOT NULL UNIQUE
        );
        """;

        String createRoleTableSQL = """
        CREATE TABLE IF NOT EXISTS roles (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name VARCHAR(255) NOT NULL UNIQUE
        );
        """;

        String createTransactionTypeSQL = """
        CREATE TABLE IF NOT EXISTS transactionType (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name VARCHAR(255) NOT NULL UNIQUE
        );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createCategoryTableSQL);
            stmt.execute(createRoleTableSQL);
            stmt.execute(createProfileTableSQL);
            stmt.execute(createTableSQL);
            stmt.execute(createTransactionTypeSQL);
            insertDefaultRoles();
            insertDefaultTransactionTypes();
            System.out.println("Database initialized and table verified.");
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
            return false;
        } catch (IllegalSQLMethodException e){
            e.printMessage();
            return false;
        }
        return true;
    }

    /* ************************************************************************************************************** */
    /*                                               Roles TABLE                                                      */
    /* ************************************************************************************************************** */




    public static void insertDefaultRoles() throws IllegalSQLMethodException {

        String sql = "INSERT OR IGNORE INTO roles (name) VALUES (?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "Student");
            stmt.executeUpdate();

            stmt.setString(1, "Employed");
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("insertDefaultRoles");
        }
    }


    public static int getRoleId(String role) throws IllegalRoleArgumentException, IllegalSQLMethodException {
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
            throw new IllegalSQLMethodException("getRoleId");
        }

        throw new IllegalRoleArgumentException();
    }


    /* ************************************************************************************************************** */
    /*                                               Category TABLE                                                   */
    /* ************************************************************************************************************** */


    public static int getCategoryId(String category) throws IllegalCategoryArgumentException,
            IllegalAddCategoryException, IllegalSQLMethodException{

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
            throw new IllegalSQLMethodException("getCategoryId");

        } catch(IllegalSQLMethodException e){
            e.printMessage();
        }

        throw new IllegalCategoryArgumentException();
    }

    public static int addCategory(String category) throws IllegalAddCategoryException, IllegalSQLMethodException {

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
            throw new IllegalSQLMethodException("addCategory");
        }

        throw new IllegalAddCategoryException();
    }

    /* ************************************************************************************************************** */
    /*                                          TransactionType TABLE                                                 */
    /* ************************************************************************************************************** */

    public static void insertDefaultTransactionTypes() throws IllegalSQLMethodException {

        String sql = "INSERT OR IGNORE INTO transactionType (name) VALUES (?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // "Expense", "Income"

            stmt.setString(1, "Income");
            stmt.executeUpdate();

            stmt.setString(1, "Expense");
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("insertDefaultTransactionTypes");
        }
    }

    public static int getTransactionTypeId(String type) throws IllegalTransactionTypeArgumentException,
            IllegalSQLMethodException{

        String sql = "SELECT id FROM transactionType WHERE name = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } // need to add else statement to add role to roles database if not found
            }

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("getTransactionTypeId");
        }
        throw new IllegalTransactionTypeArgumentException();
    }


    /* ************************************************************************************************************** */
    /*                                               PROFILE TABLE                                                    */
    /* ************************************************************************************************************** */



    public static void createNewUser(String name, String role, BigDecimal debt, BigDecimal salary) throws IllegalSQLMethodException{
        int roleId;
        try {
            roleId = getRoleId(role);
        }catch(IllegalRoleArgumentException e){
            e.printMessage();
            return;
        }catch(IllegalSQLMethodException e){
            e.printMessage();
            return;
        }


        String sql = """
        INSERT INTO profile (name, roleId, debt, salary, createdAt)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, roleId);
            setMoney(stmt, 3, debt);
            setMoney(stmt, 4, salary);
            stmt.setString(5, LocalDateTime.now().toString());

            stmt.executeUpdate();

        }catch(SQLException e){
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("createNewUser");
        }
    }

    public static boolean newUser() throws IllegalSQLMethodException{
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
            throw new IllegalSQLMethodException("newUser");
        }
        // default to false so the correct message is displayed when the user loads.
        return false;
    }

    public static Profile getProfile() throws IllegalSQLMethodException{
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
                BigDecimal debt = getMoney(rs, "debt");
                BigDecimal salary = getMoney(rs, "salary");
                int id = rs.getInt("id");
                String createdAt = rs.getString("createdAt");
                return new Profile(name, role, debt, salary, createdAt, id);
            }

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("getProfile");
        }
        return null;
    }


    public static void updateProfile(int id,String name, String role, BigDecimal debt, BigDecimal salary) throws
            IllegalSQLMethodException {

        int roleId;
        try {
            roleId = getRoleId(role);
        }catch(IllegalRoleArgumentException e){
            e.printMessage();
            return;
        }catch(IllegalSQLMethodException e){
            e.printMessage();
            return;
        }



        String sql = "UPDATE profile SET name = ?, roleId = ?, debt = ?, salary = ? WHERE id = ? ";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, roleId);
            setMoney(stmt, 3, debt);
            setMoney(stmt, 4, salary);
            stmt.setInt(5, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("updateProfile");
        }
    }

    /* ****************************************************************************************************************/
    /*                                         TRANSACTIONS TABLE                                                     */
    /* ****************************************************************************************************************/




    public static void addTransaction(BigDecimal amount, String category, String description, String createdAt,
                                      int profileId, String transactionType) throws IllegalSQLMethodException{

        int categoryId;
        try {
            categoryId = getCategoryId(category);
        } catch (IllegalCategoryArgumentException e) {
            e.printMessage();
            return;
        } catch (IllegalAddCategoryException e){
            e.printMessage();
            return;
        }catch (IllegalSQLMethodException e){
            e.printMessage();
            return;
        }

        int transactionId;
        try{
            transactionId = getTransactionTypeId(transactionType);
        }catch(IllegalTransactionTypeArgumentException e){
            e.printMessage();
            return;
        }catch(IllegalSQLMethodException e){
            e.printMessage();
            return;
        }

        String sql = """
        INSERT INTO transactions (profileId, amount, categoryId, description, createdAt, transactionTypeId)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profileId);
            setMoney(stmt, 2, amount);
            stmt.setInt(3, categoryId);
            stmt.setString(4, description);
            stmt.setString(5, createdAt);
            stmt.setInt(6, transactionId);

            stmt.executeUpdate();

        }catch(SQLException e){
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("addTransaction");
        }
    }


    public static ArrayList<Transaction> getTransactions(int profileId) throws IllegalSQLMethodException{
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = """
        SELECT t.id,
               t.amount,
               c.name AS category,
               tt.name AS type,
               t.description,
               t.createdAt
        FROM transactions t
        JOIN categories c        ON t.categoryId = c.id
        JOIN transactionType tt  ON t.transactionTypeId = tt.id
        WHERE t.profileId = ?
        ORDER BY t.createdAt DESC;
        """;
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profileId);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                int id = rs.getInt("id");
                BigDecimal amount = getMoney(rs, "amount");
                String category = rs.getString("category");
                String description = rs.getString("description");
                String createdAt = rs.getString("createdAt");
                String type = rs.getString("type");
                TransactionType transactionType = TransactionType.EXPENSE;
                if(type.equals("Income")){
                    transactionType = TransactionType.INCOME;
                }
                transactions.add(new Transaction(id, amount, category, description, createdAt, transactionType));
            }

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("getTransactions");
        }
        return transactions;

    }

    public static void deleteTransaction(int id) throws IllegalSQLMethodException{
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("deleteTransaction");
        }
    }

    public static void deleteTransactions(int profileId) throws IllegalSQLMethodException{
        String sql = "DELETE FROM transactions WHERE profileId = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profileId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL error, " + e.getMessage());
            throw new IllegalSQLMethodException("deleteTransaction");
        }
    }

}