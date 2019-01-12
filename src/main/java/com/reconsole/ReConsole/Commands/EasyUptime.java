package com.reconsole.ReConsole.Commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public class EasyUptime implements CommandExecutor {
    private long initTime;
    public EasyUptime(long initialTime) {
        initTime = initialTime;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long currentTime = System.nanoTime();
        long diff = currentTime - initTime;
        try {
            sender.sendMessage(diff + " nanoseconds since the server has been online \\o/");
        } catch (Error e) { return false; }
        return true;
    }
}
