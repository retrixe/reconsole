package com.reconsole.reconsole;

// Plugin related imports.
import org.bukkit.plugin.java.JavaPlugin;

// Handler classes.
import com.reconsole.reconsole.httphandlers.LoginEndpoint;
import com.reconsole.reconsole.httphandlers.RootEndpoint;
import com.reconsole.reconsole.httphandlers.CORSWrapperHandler;
import com.reconsole.reconsole.httphandlers.LoginValidationEndpoint;
import com.reconsole.reconsole.httphandlers.StatisticsEndpoint;
import com.reconsole.reconsole.httphandlers.WhitelistEndpoint;
import com.reconsole.reconsole.httphandlers.OperatorEndpoint;
import com.reconsole.reconsole.httphandlers.ConsoleEndpoint;
import com.reconsole.reconsole.httphandlers.ServerPropertiesEndpoint;

// HTTP server related imports.
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {
    private HttpServer server;
    private Process ws;

    private void saveFile (String file) throws Exception {
        File configFile = new File(this.getDataFolder(), file);
        InputStream fis = Main.class.getClassLoader().getResourceAsStream(file);
        if (fis == null) throw new Exception("This JAR is corrupted, no WebSocket impl. found!");
        Files.copy(fis, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void onEnable () {
        long time = System.currentTimeMillis();
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
            LoginValidationEndpoint validationEndpoint = new LoginValidationEndpoint(authHandler);
            StatisticsEndpoint metricsEndpoint = new StatisticsEndpoint(this, authHandler, time);
            WhitelistEndpoint whitelistEndpoint = new WhitelistEndpoint(this, authHandler);
            OperatorEndpoint operatorEndpoint = new OperatorEndpoint(this, authHandler);
            ConsoleEndpoint consoleEndpoint = new ConsoleEndpoint(this, authHandler);
            ServerPropertiesEndpoint serverPrEndpoint = new ServerPropertiesEndpoint(authHandler);
            // Register endpoint handlers.
            this.server.createContext("/", new CORSWrapperHandler(new RootEndpoint(this)));
            this.server.createContext("/statistics", new CORSWrapperHandler(metricsEndpoint, true));
            this.server.createContext("/login", loginEndpoint);
            this.server.createContext("/login/validate", new CORSWrapperHandler(validationEndpoint, true));
            this.server.createContext("/whitelist", new CORSWrapperHandler(whitelistEndpoint, true));
            this.server.createContext("/operators", new CORSWrapperHandler(operatorEndpoint, true));
            this.server.createContext("/console", new CORSWrapperHandler(consoleEndpoint, true));
            this.server.createContext("/serverProperties", new CORSWrapperHandler(serverPrEndpoint, true));
            // Start the server and log if successful.
            this.server.start();
            this.getLogger().log(Level.INFO, "HTTP server successfully listening on port 4200.");
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "HTTP server failed to listen on port 4200!", e);
        }

        // Start the Node.js WebSocket gateway.
        try {
            if (!new File(this.getDataFolder(), "node-ws-console").isDirectory()) {
                boolean success = new File(this.getDataFolder(), "node-ws-console").mkdirs();
                if (!success) throw new Exception();
            }
            this.saveFile("node-ws-console/build/index.js");
            // Execute the WebSocket implementation.
            String node = System.getProperty("os.name").equalsIgnoreCase("win")
                ? this.getConfig().getConfigurationSection("nodejs").getString("windows")
                : this.getConfig().getConfigurationSection("nodejs").getString("linux");
            this.ws = Runtime.getRuntime().exec(node + " " + this.getDataFolder() + "/node-ws-console/build/index.js");
            TimeUnit.SECONDS.sleep(2); // Wait 2 seconds before the announcement.
            if (!ws.isAlive()) this.getLogger().log(Level.SEVERE, "WebSocket server failed to listen on port 4269!");
            else this.getLogger().log(Level.INFO, "WebSocket server successfully listening on port 4269!");
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Initializing the WebSocket failed due to an unknown error!", e);
        }

    }

    @Override
    public void onDisable() { this.server.stop(0); this.ws.destroy(); }
}
