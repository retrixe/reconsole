package com.reconsole.reconsole.loginstrategies;

public interface LoginStrategy {
  boolean authenticate (String username, String hashedPass);
  // boolean register (String username, String password);
}
