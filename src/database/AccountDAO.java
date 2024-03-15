package database;

import database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    public final String tableName;

    public AccountDAO() {
        this.tableName = "account";
    }
    public String getTableName() {
        return this.tableName;
    }

    public void addToDB(String username, String password, int money) {
        try {
            try (Connection connection = DBConnection.getConnection();
                 Statement statement = connection.createStatement()) {

                String query = "INSERT INTO " + getTableName() + " VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    preparedStatement.setInt(3, money);

                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding user to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean accountExists(String username) {
        boolean exists = false;
        try {
            String query = "SELECT username FROM " + getTableName() + " WHERE username = ?";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if(resultSet.next()) {
                        exists = true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking user credentials: " + e.getMessage());
            e.printStackTrace();
        }
        return exists;
    }

    public int getMoney(String username){
        int money = 0;
        try{
            String query = "SELECT money FROM " + getTableName() + " WHERE username = ?";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if(resultSet.next()) {
                        money = resultSet.getInt("money");
                    }
                }
            }
        }
        catch (SQLException e){
            System.err.println("Error fetching player's money: " + e.getMessage());
            e.printStackTrace();
        }
        return money;
    }

    private List<String> getRanking() {
        List<String> rankingList = new ArrayList<>();
        try {
            String query = "SELECT username, money FROM " + getTableName() + " ORDER BY money DESC";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    int rank = 1;
                    while (resultSet.next()) {
                        String username = resultSet.getString("username");
                        int money = resultSet.getInt("money");
                        String entry = rank + ". " + username + " - " + money;
                        rankingList.add(entry);
                        rank++;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching ranking: " + e.getMessage());
            e.printStackTrace();
        }
        return rankingList;
    }

    public List<String> getTopRanking(String username) {
        List<String> rankingList = getRanking();
        int displayCount = Math.min(rankingList.size(), 5);
        List<String> topRanking = new ArrayList<>();

        for (int i = 0; i < displayCount; i++) {
            topRanking.add(rankingList.get(i));
        }

        return topRanking;
    }

    public String getPassword(String username) {
        String password = "";
        try {
            String query = "SELECT password FROM " + getTableName() + " WHERE username = ?";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        password = resultSet.getString("password");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting player's password: " + e.getMessage());
            e.printStackTrace();
        }
        return password;
    }

    public void alterMoney(String username, int amountToAdd) {
        try {
            String query = "UPDATE " + getTableName() + " SET money = money + ? WHERE username = ?";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, amountToAdd);
                preparedStatement.setString(2, username);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error changing player's balance: " + e.getMessage());
            e.printStackTrace();
        }
    }
}