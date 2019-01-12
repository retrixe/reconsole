package com.reconsole.ReConsole;

// Plugin related imports.
import com.reconsole.ReConsole.Commands.EasyUptime;
import org.bukkit.plugin.java.JavaPlugin;

// Handler classes.
import com.reconsole.ReConsole.HttpHandlers.Login;

// HTTP server related imports.
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main extends JavaPlugin {
    private long startTime = System.nanoTime();
    private HttpServer server;

    @Override
    public void onEnable () {
        // Start HTTP server.
        try {
            this.server = HttpServer.create(new InetSocketAddress("localhost", 4200), 0);
            this.server.setExecutor(null);
            this.server.createContext("/login", new Login());
            System.out.println("HTTP server successfully listening on port 4200.");
        } catch (IOException e) {
            System.err.println("HTTP server failed to listen on port 4200!");
        }
        this.getCommand("easyUptime").setExecutor(new EasyUptime(startTime));
    }
}
