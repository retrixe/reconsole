package com.reconsole.reconsole.loginstrategies;

import org.bukkit.plugin.java.JavaPlugin;

public class TestStrategy {
    private JavaPlugin plugin;
    public TestStrategy(JavaPlugin javaPlugin) { plugin = javaPlugin; }

    public boolean validate (String username, String hashedPass) {
        if (username.length() > 0 && hashedPass.length() > 0) return true;
        return false;
    }
}
