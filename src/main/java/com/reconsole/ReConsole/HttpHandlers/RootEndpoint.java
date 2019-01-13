package com.reconsole.ReConsole.HttpHandlers;

import com.google.gson.JsonObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.OutputStream;

public class RootEndpoint implements HttpHandler {
    private JavaPlugin plugin;
    public RootEndpoint(JavaPlugin javaPlugin) { plugin = javaPlugin; }

    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept"
        );
        // Build the JSON object.
        JsonObject json = new JsonObject();
        json.addProperty("code", 200);
        json.addProperty("online", true);
        json.addProperty("maxPlayers", plugin.getServer().getMaxPlayers());
        json.addProperty("playersOnline", plugin.getServer().getOnlinePlayers().size());
        json.addProperty("versionName", plugin.getServer().getVersion());
        String res = json.toString();
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}
