package database;

import java.sql.*;

public class DBCreate {

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
            statement = connection.createStatement();

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS oczko");
            statement.executeUpdate("USE oczko");

            statement.executeUpdate("CREATE TABLE account (" +
                    "username VARCHAR(255) PRIMARY KEY," +
                    "password VARCHAR(255)," +
                    "money INT(6));");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}