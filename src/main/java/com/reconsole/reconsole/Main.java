package com.reconsole.reconsole;

// Plugin related imports.
import com.reconsole.reconsole.httphandlers.PerformanceMetricsEndpoint;
import org.bukkit.plugin.java.JavaPlugin;

// Handler classes.
import com.reconsole.reconsole.httphandlers.LoginEndpoint;
import com.reconsole.reconsole.httphandlers.RootEndpoint;
import com.reconsole.reconsole.httphandlers.CORSWrapperHandler;

// HTTP server related imports.
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private HttpServer server;

    @Override
    public void onEnable () {
        // Setup authentication.
        AuthenticationHandler authHandler = new AuthenticationHandler(this);

        // Setup default configuration.
        // this.saveDefaultConfig();

        // Start HTTP server.
        try {
            this.server = HttpServer.create(new InetSocketAddress(4200), 0);
            this.server.setExecutor(null);
            // Initialize our login and performance metrics endpoint.
            CORSWrapperHandler loginEndpoint = new CORSWrapperHandler(new LoginEndpoint(authHandler), true);
            PerformanceMetricsEndpoint metricsEndpoint = new PerformanceMetricsEndpoint(this, authHandler);
            // Register endpoint handlers.
            this.server.createContext("/", new CORSWrapperHandler(new RootEndpoint(this)));
            this.server.createContext("/performanceMetrics", new CORSWrapperHandler(metricsEndpoint));
            this.server.createContext("/login", loginEndpoint);
            // Start the server and log if successful.
            this.server.start();
            this.getLogger().log(Level.INFO, "HTTP server successfully listening on port 4200.");
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "HTTP server failed to listen on port 4200!", e);
        }
    }

    @Override
    public void onDisable() { this.server.stop(0); }
}
