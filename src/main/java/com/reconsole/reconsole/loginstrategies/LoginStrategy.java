package com.reconsole.reconsole.loginstrategies;

public interface LoginStrategy {
  public boolean validate (String username, String hashedPass);
}
