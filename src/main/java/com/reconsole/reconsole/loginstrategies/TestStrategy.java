package com.reconsole.reconsole.loginstrategies;

import com.reconsole.reconsole.loginstrategies.LoginStrategy;

public class TestStrategy implements LoginStrategy {
    public boolean validate (String username, String hashedPass) {
        if (username.length() > 0 && hashedPass.length() > 0) return true;
        return false;
    }
}
