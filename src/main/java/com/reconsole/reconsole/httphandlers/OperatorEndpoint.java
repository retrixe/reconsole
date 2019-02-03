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

public class OperatorEndpoint implements HttpHandler {
    private JavaPlugin plugin;
    private AuthenticationHandler authenticationHandler;
    public OperatorEndpoint(JavaPlugin javaPlugin, AuthenticationHandler auth) {
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
        // If we need to add a player as operator.
        switch (path) {
            case "/operators/addPlayerByName": {
                try {
                    String query = exchange.getRequestURI().getQuery().split("=")[1];
                    // Try with the API first.
                    OfflinePlayer player = plugin.getServer().getPlayer(query);
                    if (player != null) player.setWhitelisted(true);
                    // We will execute a command for this.
                    else {
                        String cmd = "op " + query;
                        boolean success = true;
                        if (!plugin.getServer().isPrimaryThread()) {
                            plugin.getServer().getScheduler().runTask(
                                    plugin,
                                    () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd)
                            );
                        } else success = plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
                        if (!success) throw new Exception();
                    }
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
                } catch (Throwable e) {
                    // Build the JSON object.
                    JsonObject json = new JsonObject();
                    json.addProperty("code", 404);
                    json.addProperty("success", false);
                    json.addProperty("status", "invalid_name");
                    String res = json.toString();
                    // Sending the response.
                    exchange.sendResponseHeaders(404, res.length());
                    OutputStream outputStream = exchange.getResponseBody();
                    outputStream.write(res.getBytes());
                    outputStream.close();
                    return;
                }
            }
            case "/operators/removePlayerByUUID": {
                try {
                    String query = exchange.getRequestURI().getQuery().split("=")[1];
                    boolean success = false;
                    // Use the Bukkit API for this.
                    for (OfflinePlayer i : plugin.getServer().getOperators()) {
                        if (i.getUniqueId().equals(UUID.fromString(query))) {
                            i.setOp(false);
                            success = true;
                        }
                    }
                    if (!success) throw new Exception();
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
                } catch (Exception e) {
                    // Build the JSON object.
                    JsonObject json = new JsonObject();
                    json.addProperty("code", 404);
                    json.addProperty("success", false);
                    json.addProperty("status", "invalid_uuid");
                    String res = json.toString();
                    // Sending the response.
                    exchange.sendResponseHeaders(404, res.length());
                    OutputStream outputStream = exchange.getResponseBody();
                    outputStream.write(res.getBytes());
                    outputStream.close();
                    return;
                }
            }
        }
        // Get an array of operators.
        JsonArray operators = new JsonArray();
        for (OfflinePlayer offlinePlayer : plugin.getServer().getOperators()) {
            JsonObject playerObj = new JsonObject();
            playerObj.addProperty("name", offlinePlayer.getName());
            playerObj.addProperty("uuid", offlinePlayer.getUniqueId().toString());
            operators.add(playerObj);
        }
        // Build the JSON object.
        JsonObject json = new JsonObject();
        json.addProperty("code", 200);
        json.add("operators", operators);
        String res = json.toString();
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}
