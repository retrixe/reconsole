package com.reconsole.reconsole.loginstrategies;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class MySQLStrategy extends SQLStrategy {

    public MySQLStrategy(JavaPlugin javaPlugin) throws SQLException {
        super(javaPlugin, "mysql");
    }
}
