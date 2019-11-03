package com.reconsole.reconsole.loginstrategies;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public class AuthMeStrategy implements LoginStrategy {
    public AuthMeStrategy(JavaPlugin javaPlugin) throws ClassNotFoundException {
        Class.forName("fr.xephi.authme.api.v3.AuthMeApi");
    }

    @Override
    public boolean authenticate (String username, String hashedPass) {
        if (username.length() == 0 || hashedPass.length() == 0) return false;
        try {
            Method getInstance = Class.forName("fr.xephi.authme.api.v3.AuthMeApi").getMethod("getInstance");
            Object api = getInstance.invoke(null);
            Method checkPassword = api.getClass().getMethod("checkPassword", String.class, String.class);
            Object isValid = checkPassword.invoke(api, username, hashedPass);
            return (boolean) isValid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // We will not handle this stuff.
    @Override
    public boolean register (String username, String hashedPass) { return false; }

    public boolean delete (String username) { return false; }

    public boolean changepw (String username, String hashedPass) { return false; }
}
