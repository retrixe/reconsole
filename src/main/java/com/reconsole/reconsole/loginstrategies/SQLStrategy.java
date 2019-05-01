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
}
