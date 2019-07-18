package com.reconsole.reconsole;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ReconsoleCommand implements CommandExecutor {
    private JavaPlugin plugin;
    private AuthenticationHandler auth;
    ReconsoleCommand(JavaPlugin plugin, AuthenticationHandler auth) { this.plugin = plugin; this.auth = auth; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO: More colors.
        String loginMethod = this.plugin.getConfig().getString("login-method");
        if (args.length == 0) {
            sender.sendMessage("ReConsole version " + this.plugin.getDescription().getVersion());
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
        } else if (args.length == 2 && args[0].equals("delete")) {
            try {
                boolean success = this.auth.delete(args[1]);
                if (success) sender.sendMessage("Account deleted successfully!");
                else sender.sendMessage("This account does not exist!");
            } catch (Exception e) {
                sender.sendMessage("Failed to delete account! Check console for more information.");
                this.plugin.getLogger().log(Level.SEVERE, "Failure when deleting account " + args[1], e);
            }
            return true;
        } else if (args.length == 4 && args[0].equals("changepw")) {
            if (!args[2].equals(args[3])) {
                sender.sendMessage("Both passwords entered do not match!");
                return true;
            }
            try {
                boolean success = this.auth.changepw(args[1], args[2]);
                if (success) sender.sendMessage("Password of account changed successfully!");
                else sender.sendMessage("This account does not exist!");
            } catch (Exception e) {
                sender.sendMessage("Failed to change password of account! Check console for more information.");
                this.plugin.getLogger().log(Level.SEVERE, "Failure when changing account password " + args[1], e);
            }
            return true;
        }
        return false;
    }
}
