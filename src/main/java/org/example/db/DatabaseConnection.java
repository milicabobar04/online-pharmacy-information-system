package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "abstract-programmer";
    private static final String PASSWORD = "root";

    private static Connection connection = null;

    // Singleton — jedna konekcija za cijelu aplikaciju
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Konekcija na bazu uspjesna!");
            }
        } catch (SQLException e) {
            System.err.println("Greska pri konekciji: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Konekcija zatvorena.");
            }
        } catch (SQLException e) {
            System.err.println("Greska pri zatvaranju: " + e.getMessage());
        }
    }
}