package com.reconsole.reconsole.loginstrategies;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class SQLStrategy implements LoginStrategy {

    private Connection connection;

    SQLStrategy(JavaPlugin javaPlugin, String dbName) throws SQLException {
        ConfigurationSection section = javaPlugin.getConfig().getConfigurationSection(dbName);
        connection = DriverManager.getConnection(
                section.getString("connection-url").replace("{df}", javaPlugin.getDataFolder().getPath()),
                section.getString("username"),
                section.getString("password"));
        // Create the users table if it doesn't exist already.
        PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS users (username varchar(255), password varchar(255))"
        );
        boolean success = statement.execute();
        if (!success) throw new SQLException("Unable to create users table! Logging into ReConsole will not work.");
    }

    @Override
    public boolean authenticate(String username, String hashedPass) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE STRCMP(username, ?) =  0 " +
                    "AND STRCMP(password, ?) = 0 LIMIT 1");
            statement.setString(1, username);
            statement.setString(2, hashedPass);
            ResultSet resultSet = statement.executeQuery();
            boolean result = resultSet.first();
            statement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean register(String username, String hashedPass) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE STRCMP(username, ?) =  0 " +
                    "AND STRCMP(password, ?) = 0 LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, username);
            statement.setString(2, hashedPass);
            ResultSet result = statement.executeQuery();
            if (result.first()) {
                statement.close();
                return false;
            } else {
                result.moveToInsertRow();
                result.updateString("username", username);
                result.updateString("password", hashedPass);
                result.insertRow();
                statement.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete (String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE STRCMP(username, ?)");
            statement.setString(1, username);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changepw (String username, String hashedPass) {
        // TODO: Proper implementation retaining user.
        return this.delete(username) && this.register(username, hashedPass);
    }
}
