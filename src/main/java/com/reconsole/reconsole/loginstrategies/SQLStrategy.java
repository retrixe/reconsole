package com.reconsole.reconsole.loginstrategies;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SQLStrategy implements LoginStrategy {

    // Initialize inside initConnection
    protected Connection connection;

    public SQLStrategy(JavaPlugin plugin) throws SQLException {
        initConnection(plugin);
    }

    protected abstract void initConnection(JavaPlugin plugin) throws SQLException;

    @Override
    public boolean authenticate(String username, String hashedPass) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE STRCMP(username, ?) =  0 " +
                "AND STRCMP(password, ?) = 0 LIMIT 1")) {
            statement.setString(1, username);
            statement.setString(2, hashedPass);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.first();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String username) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE STRCMP(username, ?) = 0 " +
                "LIMIT 1")) {
            statement.setString(1, username);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changepw(String username, String hashedPass) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET password = ? WHERE " +
                "STRCMP(username, ?) = 0 LIMIT 1")) {
            statement.setString(1, hashedPass);
            statement.setString(2, username);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
