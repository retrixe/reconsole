package com.reconsole.reconsole.httphandlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import com.reconsole.reconsole.AuthenticationHandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class WhitelistEndpoint implements HttpHandler {
    private JavaPlugin plugin;
    private AuthenticationHandler authenticationHandler;
    public WhitelistEndpoint(JavaPlugin javaPlugin, AuthenticationHandler auth) {
        plugin = javaPlugin;
        authenticationHandler = auth;
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
        // Depending on the path we will perform an operation.
        String path = exchange.getRequestURI().getPath();
        // If we need to add a player to the whitelist.
        switch (path) {
            case "/whitelist/toggle": {
                // Toggle it.
                plugin.getServer().setWhitelist(!plugin.getServer().hasWhitelist());
                // Build the JSON object.
                JsonObject json = new JsonObject();
                json.addProperty("code", 200);
                json.addProperty("enabled", plugin.getServer().hasWhitelist());
                String res = json.toString();
                // Sending the response.
                exchange.sendResponseHeaders(200, res.length());
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(res.getBytes());
                outputStream.close();
                return;
            }
            case "/whitelist/addPlayerByName": {
                String query = exchange.getRequestURI().getQuery();
                plugin.getServer().getPlayer(query.split("=")[1]).setWhitelisted(true);
                // Build the JSON object.
                JsonObject json = new JsonObject();
                json.addProperty("code", 200);
                json.addProperty("success", true);
                String res = json.toString();
                // Sending the response.
                exchange.sendResponseHeaders(200, res.length());
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(res.getBytes());
                outputStream.close();
                return;
            }
            case "/whitelist/removePlayerByUUID": {
                String query = exchange.getRequestURI().getQuery();
                plugin.getServer().getPlayer(UUID.fromString(query.split("=")[1])).setWhitelisted(false);
                // Build the JSON object.
                JsonObject json = new JsonObject();
                json.addProperty("code", 200);
                json.addProperty("success", true);
                String res = json.toString();
                // Sending the response.
                exchange.sendResponseHeaders(200, res.length());
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(res.getBytes());
                outputStream.close();
                return;
            }
        }
        // Get an array of whitelisted players.
        JsonArray whitelistedPlayers = new JsonArray();
        for (OfflinePlayer offlinePlayer : plugin.getServer().getWhitelistedPlayers()) {
            JsonObject playerObj = new JsonObject();
            playerObj.addProperty("name", offlinePlayer.getName());
            playerObj.addProperty("uuid", offlinePlayer.getUniqueId().toString());
            whitelistedPlayers.add(playerObj);
        }
        // Build the JSON object.
        JsonObject json = new JsonObject();
        json.addProperty("code", 200);
        json.addProperty("enabled", plugin.getServer().hasWhitelist());
        json.add("whitelistedPlayers", whitelistedPlayers);
        String res = json.toString();
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}
