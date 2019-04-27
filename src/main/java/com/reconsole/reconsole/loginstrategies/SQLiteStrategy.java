package com.reconsole.reconsole.loginstrategies;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class SQLiteStrategy extends SQLStrategy {

    public SQLiteStrategy(JavaPlugin javaPlugin) throws SQLException {
        super(javaPlugin, "sqlite");
    }
}
