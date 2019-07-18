package com.reconsole.reconsole.loginstrategies;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class MySQLStrategy extends SQLStrategy {

    public MySQLStrategy(JavaPlugin plugin) throws SQLException {
        super(plugin);
    }

    @Override
    protected void initConnection(JavaPlugin plugin) throws SQLException {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("mysql");
        connection = DriverManager.getConnection(
                section.getString("connection-url").replace("{df}", plugin.getDataFolder().getPath()),
                section.getString("username"),
                section.getString("password"));
    }

    @Override
    public boolean register(String username, String hashedPass) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE STRCMP(username, ?) = 0 " +
                "AND STRCMP(password, ?) = 0 LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            statement.setString(1, username);
            statement.setString(2, hashedPass);
            ResultSet result = statement.executeQuery();
            if (result.first()) {
                return false;
            } else {
                result.moveToInsertRow();
                result.updateString("username", username);
                result.updateString("password", hashedPass);
                result.insertRow();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
