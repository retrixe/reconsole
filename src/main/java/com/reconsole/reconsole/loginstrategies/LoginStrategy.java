package com.reconsole.reconsole.loginstrategies;

public interface LoginStrategy {
  boolean authenticate (String username, String hashedPass);
  boolean register (String username, String hashedPass);
  /*
  TODO: Currently stub impl. in /reconsole command and authentication handler. Must do when SQLStrategy updated.
  boolean delete (String username);
  boolean changepw (String username, String hashedPass);
  */
}
