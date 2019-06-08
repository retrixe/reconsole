package com.reconsole.reconsole;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReconsoleCommand implements CommandExecutor {
    private JavaPlugin plugin;
    private AuthenticationHandler auth;
    ReconsoleCommand(JavaPlugin plugin, AuthenticationHandler auth) { this.plugin = plugin; this.auth = auth; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String loginMethod = this.plugin.getConfig().getString("login-method");
        if (args.length == 0) {
            sender.sendMessage("ReConsole version 1.0.0-alpha.2");
            if (!loginMethod.startsWith("authme")) {
                sender.sendMessage("This server uses " + loginMethod + " for authentication.");
                sender.sendMessage("Usage: /reconsole register/changepw <username> <password> <confirmPass> or /reconsole delete <username>");
            } else {
                sender.sendMessage("This server uses AuthMe for authentication in ReConsole.");
                sender.sendMessage("Please use AuthMe and OP/ReConsole.use to configure the server.");
            }
            return true;
        } else if (loginMethod.startsWith("authme")) {
            sender.sendMessage(
                    "The server is configured to use AuthMe for authentication. " +
                    "Please use op/deop/(the ReConsole.use permission node) and AuthMe to configure access."
            );
            return true;
        } else if (args.length != 4 && (args[0].equals("register") || args[0].equals("changepw"))) {
            sender.sendMessage("Invalid usage! /reconsole " + args[0] + " <username> <password> <confirmPass>");
            return true;
        } else if (args.length != 2 && args[0].equals("delete")) {
            sender.sendMessage("Invalid usage! /reconsole delete <username>");
            return true;
        } else if (args.length == 4 && args[0].equals("register")) {
            if (!args[2].equals(args[3])) {
                sender.sendMessage("Both passwords entered do not match!");
                return true;
            }
            boolean success = this.auth.register(args[1], args[2]);
            if (success) sender.sendMessage("Account registered successfully!");
            else sender.sendMessage("Account failed to register! Check console for more information.");
            return true;
        } // TODO: Need to add change password and delete support.
        return false;
    }
}
