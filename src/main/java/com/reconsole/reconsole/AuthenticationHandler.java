package com.reconsole.reconsole;

import com.reconsole.reconsole.loginstrategies.*;

import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;

import com.google.common.hash.Hashing;

public class AuthenticationHandler {
    private LoginStrategy loginStrategy;
    private HashMap<String, String> accessTokens = new HashMap<>();

    public boolean validateToken (String accessToken) {
        // Self-explanatory?
        return accessTokens.containsKey(accessToken);
    }

    AuthenticationHandler(JavaPlugin javaPlugin) {
        // Determine the type of strategy to use.
        loginStrategy = new TestStrategy();
        String strategy = javaPlugin.getConfig().getString("login-method");

        // Available strategies when complete: mongodb, sqlite, mysql, authme, authme-mysql
        switch (strategy) {
            case "mongodb":
                loginStrategy = new MongoStrategy(javaPlugin);
                break;
            case "sqlite":
                try {
                    loginStrategy = new SQLiteStrategy(javaPlugin);
                } catch (Exception e) {
                    javaPlugin.getLogger().log(Level.SEVERE, "Failed to connect to SQLite!", e);
                }
                break;
            case "mysql":
                try {
                    loginStrategy = new MySQLStrategy(javaPlugin);
                } catch (Exception e) {
                    javaPlugin.getLogger().log(Level.SEVERE, "Failed to connect to MySQL!", e);
                }
                break;
        }
    }

    public String authenticate (String username, String password) {
        // We hash the raw password and authenticate with the login strategy.
        String hashedPass = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
        boolean validCredentials = this.loginStrategy.authenticate(username, hashedPass);

        // If the credentials are invalid, we return null.
        if (!validCredentials) return null;

        // We generate 96 random bytes securely and generate an access token.
        byte[] accessTokenBytes = new byte[96];
        new SecureRandom().nextBytes(accessTokenBytes);
        String accessToken = Base64.getEncoder().encodeToString(accessTokenBytes);

        // We then save this in our access token.
        this.accessTokens.put(accessToken, username);
        return accessToken;
    }

    public boolean register (String username, String password) {
        // We hash the raw password and register with the login strategy.
        String hashedPass = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
        return this.loginStrategy.register(username, hashedPass);
    }
}
