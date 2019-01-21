package com.reconsole.reconsole;

// Plugin related imports.
import org.bukkit.plugin.java.JavaPlugin;

// Handler classes.
import com.reconsole.reconsole.httphandlers.LoginEndpoint;
import com.reconsole.reconsole.httphandlers.RootEndpoint;
import com.reconsole.reconsole.httphandlers.CORSWrapperHandler;

// HTTP server related imports.
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private HttpServer server;
    private HashMap<String, String> tokens = new HashMap<>();

    @Override
    public void onEnable () {
        // Setup default configuration.
        // this.saveDefaultConfig();
        // Start HTTP server.
        try {
            this.server = HttpServer.create(new InetSocketAddress(4200), 0);
            this.server.setExecutor(null);
            CORSWrapperHandler login = new CORSWrapperHandler(new LoginEndpoint(this, this.tokens), true);
            this.server.createContext("/login", login);
            this.server.createContext("/", new CORSWrapperHandler(new RootEndpoint(this)));
            this.server.start();
            this.getLogger().log(Level.INFO, "HTTP server successfully listening on port 4200.");
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "HTTP server failed to listen on port 4200!", e);
        }
    }

    @Override
    public void onDisable() {
        this.server.stop(0);
    }
}
