package com.reconsole.reconsole.httphandlers;

import com.google.gson.JsonObject;

import com.reconsole.reconsole.AuthenticationHandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class StatisticsEndpoint implements HttpHandler {
    private JavaPlugin plugin;
    private long startTime;
    private AuthenticationHandler authenticationHandler;
    public StatisticsEndpoint(JavaPlugin javaPlugin, AuthenticationHandler auth, long time) {
        plugin = javaPlugin;
        authenticationHandler = auth;
        startTime = time;
    }

    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestHeaders().containsKey("Access-Token")) {
            JsonObject json = new JsonObject();
            json.addProperty("code", 401);
            json.addProperty("success", false);
            json.addProperty("status", "no_credentials_provided");
            exchange.sendResponseHeaders(401, json.toString().length());
            exchange.getResponseBody().write(json.toString().getBytes());
            exchange.close();
            return;
        } else if (!authenticationHandler.validateToken(exchange.getRequestHeaders().getFirst("Access-Token"))) {
            JsonObject json = new JsonObject();
            json.addProperty("code", 401);
            json.addProperty("success", false);
            json.addProperty("status", "invalid_access_token");
            exchange.sendResponseHeaders(401, json.toString().length());
            exchange.getResponseBody().write(json.toString().getBytes());
            exchange.close();
            return;
        }
        // How tf do you get output for this..
        // boolean tpsCommandOutput = plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "tps");

        // This is some unreliable old API I'm using right now to determine stuff..
        // I'll work on system specific calls later.
        OperatingSystemMXBean o = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        // int pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        // ^^ use this code for system-specific calls in future

        // Build the JSON object.
        JsonObject json = new JsonObject();
        json.addProperty("code", 200);
        json.addProperty("online", true);
        json.addProperty("memoryUsed", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
        json.addProperty("totalMemory", o.getTotalPhysicalMemorySize());
        json.addProperty("cpuUsage", (int)(o.getProcessCpuLoad() * 100));
        json.addProperty("onlineSince", startTime);
        // Copied from root endpoint.
        json.addProperty("maxPlayers", plugin.getServer().getMaxPlayers());
        json.addProperty("playersOnline", plugin.getServer().getOnlinePlayers().size());
        json.addProperty("versionName", plugin.getServer().getVersion());
        // json.addProperty("tps", 20.00);
        String res = json.toString();
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}
