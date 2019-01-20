package com.reconsole.reconsole.loginstrategies;

public class TestStrategy implements LoginStrategy {
    public boolean validate (String username, String hashedPass) {
        return username.length() > 0 && hashedPass.length() > 0;
    }
}
