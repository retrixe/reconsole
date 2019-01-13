package com.reconsole.ReConsole;

// Plugin related imports.
import org.bukkit.plugin.java.JavaPlugin;

// Handler classes.
import com.reconsole.ReConsole.HttpHandlers.Login;
import com.reconsole.ReConsole.HttpHandlers.RootEndpoint;

// HTTP server related imports.
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class Main extends JavaPlugin {
    private HttpServer server;
    private HashMap<String, String> tokens = new HashMap();

    @Override
    public void onEnable () {
        // Start HTTP server.
        try {
            this.server = HttpServer.create(new InetSocketAddress(4200), 0);
            this.server.setExecutor(null);
            this.server.createContext("/login", new Login(this, this.tokens));
            this.server.createContext("/", new RootEndpoint(this));
            this.server.start();
            System.out.println("[ReConsole] HTTP server successfully listening on port 4200.");
        } catch (IOException e) {
            System.err.println("[ReConsole] HTTP server failed to listen on port 4200!");
        }
    }
}
