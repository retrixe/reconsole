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

        // Available strategies: mongodb, sqlite, mysql, authme
        switch (strategy) {
            case "authme":
                try {
                    loginStrategy = new AuthMeStrategy(javaPlugin);
                } catch (Exception e) {
                    javaPlugin.getLogger().log(
                            Level.SEVERE, "Unable to find AuthMe API! You will be unable to log into ReConsole.", e
                    );
                }
                break;
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
        if (loginStrategy instanceof AuthMeStrategy) hashedPass = password;
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

    boolean register (String username, String password) {
        // We hash the raw password and register with the login strategy.
        String hashedPass = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
        if (loginStrategy instanceof AuthMeStrategy) hashedPass = password;
        return this.loginStrategy.register(username, hashedPass);
    }

    // TODO
    boolean delete (String username) {
        // True if success.
        // False if account does not exist.
        // Throw error if failed to delete.
        return false;
    }

    boolean changepw (String username, String password) {
        // True if success.
        // False if account does not exist.
        // Throw error if failed to delete.
        return false;
    }
}
