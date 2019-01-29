package com.reconsole.reconsole.httphandlers;

import com.google.gson.JsonObject;
import com.reconsole.reconsole.AuthenticationHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class LoginValidationEndpoint implements HttpHandler {
    private AuthenticationHandler authHandler;
    public LoginValidationEndpoint(AuthenticationHandler auth) {
        authHandler = auth;
    }

    public void handle(HttpExchange exchange) throws IOException {
        // Validate if credentials were sent, if invalid error.
        if (!exchange.getRequestHeaders().containsKey("Access-Token")) {
            JsonObject json = new JsonObject();
            json.addProperty("code", 401);
            json.addProperty("success", false);
            json.addProperty("status", "no_credentials_provided");
            exchange.sendResponseHeaders(401, json.toString().length());
            exchange.getResponseBody().write(json.toString().getBytes());
            exchange.close();
            return;
        }
        // Check validity of token.
        boolean valid = authHandler.validateToken(exchange.getRequestHeaders().getFirst("Access-Token"));
        // Send an access denied error if token is invalid.
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
        // Build the JSON object.
        JsonObject json = new JsonObject();
        json.addProperty("code", 200);
        json.addProperty("success", true);
        json.addProperty("status", "valid_credentials");
        String res = json.toString();
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}

