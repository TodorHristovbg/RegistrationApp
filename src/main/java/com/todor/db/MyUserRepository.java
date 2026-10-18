package com.todor.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyUserRepository {

    public boolean registerNewUser(String first_name, String second_name, String email, String password_hash) throws SQLException{
        String insertusersql ="INSERT INTO users (first_name, second_name, email, password_hash) VALUES (?,?,?,?)";
        try(Connection connection = DataBase.getConnection();
            PreparedStatement statement = connection.prepareStatement(insertusersql)){
                statement.setString(1, first_name);
                statement.setString(2, second_name);
                statement.setString(3, email);
                statement.setString(4, password_hash);
                
                return statement.executeUpdate() == 1;
            }
    }
    public User findUserById(int userId) throws SQLException{
        String sql = "SELECT * FROM users where id = ?";
        try(Connection connection = DataBase.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, userId);
                try(ResultSet results = statement.executeQuery()){
                    if(results.next()){
                        return new User(
                            results.getInt("id"),
                            results.getString("first_name"),
                            results.getString("second_name"),
                            results.getString("password_hash"),
                            results.getString("email")
                        );
                    }
                    return null;
                    }
                
            }
    }
    public User findUserByEmail(String email) throws SQLException{
        String sql = "SELECT * FROM users where email = ?";
        try(Connection connection = DataBase.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, email);
                try(ResultSet results = statement.executeQuery()){
                    if(results.next()){
                        return new User(
                            results.getInt("id"),
                            results.getString("first_name"),
                            results.getString("second_name"),
                            results.getString("password_hash"),
                            results.getString("email")
                        );
                    }
                    return null;
                    }
                
            }
    }
    public boolean DoesUserExistForEmail(String email) throws SQLException{
        User user = findUserByEmail(email);
        if(user==null){
            return false;
        }
        return true;
    }
    public int CheckCredentials(String email, String password_hash) throws SQLException{    //returns user id
        String checkcredentials = "SELECT * FROM users WHERE email = ? AND password_hash = ?";
        try(Connection connection = DataBase.getConnection();
            PreparedStatement statement = connection.prepareStatement(checkcredentials)){
                statement.setString(1, email);
                statement.setString(2, password_hash);

            try (ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                return result.getInt("id");
            }

            return -1;
        }
        }
    }

    public boolean updateFirstName(int userId, String new_first_name) throws SQLException {

        String sql = "UPDATE users SET first_name = ? WHERE id = ?";

        try (Connection connection = DataBase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, new_first_name);
            statement.setInt(2, userId);

            return statement.executeUpdate() > 0;
        }
    }

     public boolean updateSecondName(int userId, String new_second_name) throws SQLException {

        String sql = "UPDATE users SET second_name = ? WHERE id = ?";

        try (Connection connection = DataBase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, new_second_name);
            statement.setInt(2, userId);

            return statement.executeUpdate() > 0;
        }
    }

     public boolean updatePassword(int userId, String password_hash) throws SQLException {

        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection connection = DataBase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, password_hash);
            statement.setInt(2, userId);

            return statement.executeUpdate() > 0;
        }
    }

}