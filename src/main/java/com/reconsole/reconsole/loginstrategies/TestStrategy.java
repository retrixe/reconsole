package com.reconsole.reconsole.loginstrategies;

public class TestStrategy implements LoginStrategy {
    @Override
    public boolean authenticate (String username, String hashedPass) {
        return username.length() > 0 && hashedPass.length() > 0;
    }

    @Override
    public boolean register (String username, String hashedPass) {
        return true;
    }

    @Override
    public boolean delete (String username) {
        return true;
    }

    @Override
    public boolean changepw (String username, String hashedPass) {
        return true;
    }
}
