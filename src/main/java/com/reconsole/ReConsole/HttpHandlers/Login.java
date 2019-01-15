package com.reconsole.ReConsole.HttpHandlers;

import com.reconsole.ReConsole.LoginStrategies.LocalFileStrategy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.bukkit.plugin.java.JavaPlugin;
import com.google.gson.JsonObject;
import com.google.common.hash.Hashing;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Login implements HttpHandler {
    private JavaPlugin plugin;
    private HashMap<String, String> tokens;
    public Login(JavaPlugin javaPlugin, HashMap<String, String> tokenMap) { plugin = javaPlugin; tokens = tokenMap; }

    public void handle(HttpExchange exchange) throws IOException {
        // Validate if credentials were sent, if invalid error.
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            JsonObject json = new JsonObject();
            json.addProperty("code", 405);
            json.addProperty("success", false);
            exchange.sendResponseHeaders(405, json.toString().length());
            exchange.getResponseBody().write(json.toString().getBytes());
            exchange.close();
            return;
        } else if (
            !exchange.getRequestHeaders().containsKey("Username") ||
            !exchange.getRequestHeaders().containsKey("Password")
        ) {
            JsonObject json = new JsonObject();
            json.addProperty("code", 401);
            json.addProperty("success", false);
            json.addProperty("status", "no_credentials_provided");
            exchange.sendResponseHeaders(401, json.toString().length());
            exchange.getResponseBody().write(json.toString().getBytes());
            exchange.close();
            return;
        }
        // Call the login strategy (read from config about it first).
        // We need to support configuration of either using our own credential system to local file,
        // own credential system to SQL or AuthMe local file support.
        LocalFileStrategy localFileStrategy = new LocalFileStrategy(plugin);
        String password = exchange.getRequestHeaders().getFirst("Username");
        String hashedPass = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString(); // Hash the pass.
        boolean valid = localFileStrategy.handle(exchange.getRequestHeaders().getFirst("Username"), hashedPass);
        // Set a token for the user in a high level HashMap if valid.
        if (!valid) {
            JsonObject json = new JsonObject();
            json.addProperty("code", 401);
            json.addProperty("success", false);
            json.addProperty("status", "invalid_credentials");
            exchange.sendResponseHeaders(401, json.toString().length());
            exchange.getResponseBody().write(json.toString().getBytes());
            exchange.close();
            return;
        }
        String accessToken = "test"; // Help me issue a token pls.
        tokens.put(accessToken, exchange.getRequestHeaders().getFirst("Username"));
        // Build the JSON object.
        JsonObject json = new JsonObject();
        json.addProperty("code", 200);
        json.addProperty("success", true);
        json.addProperty("status", "authenticated");
        json.addProperty("access_token", accessToken);
        String res = json.toString();
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}

