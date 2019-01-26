package com.reconsole.reconsole.httphandlers;

import com.reconsole.reconsole.AuthenticationHandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;

public class LoginEndpoint implements HttpHandler {
    private AuthenticationHandler authHandler;
    public LoginEndpoint(AuthenticationHandler auth) {
        authHandler = auth;
    }

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
        // Call the authentication handler and get a token back.
        String password = exchange.getRequestHeaders().getFirst("Username");
        String token = authHandler.authenticate(exchange.getRequestHeaders().getFirst("Username"), password);
        // Send an access denied error if authentication is invalid.
        if (token == null) {
            JsonObject json = new JsonObject();
            json.addProperty("code", 401);
            json.addProperty("success", false);
            json.addProperty("status", "invalid_credentials");
            exchange.sendResponseHeaders(401, json.toString().length());
            exchange.getResponseBody().write(json.toString().getBytes());
            exchange.close();
            return;
        }
        // Build the JSON object.
        JsonObject json = new JsonObject();
        json.addProperty("code", 200);
        json.addProperty("success", true);
        json.addProperty("status", "authenticated");
        json.addProperty("access_token", token);
        String res = json.toString();
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}

