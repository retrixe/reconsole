package com.reconsole.reconsole.httphandlers;

import com.google.gson.JsonObject;

import com.reconsole.reconsole.AuthenticationHandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;

public class ServerPropertiesEndpoint implements HttpHandler {
    private AuthenticationHandler authenticationHandler;
    public ServerPropertiesEndpoint(AuthenticationHandler auth) {
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
        // If we need to edit the server.properties.
        if (exchange.getRequestURI().getPath().equals("/serverProperties/write")) {
            try {
                // Write the file.
                BufferedWriter writer = new BufferedWriter(new FileWriter("server.properties"));
                // Read the body.
                InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder(512);
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                br.close();
                isr.close();
                // Write the body to the file.
                writer.write(buf.toString());
                writer.close();
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
                json.addProperty("code", 500);
                json.addProperty("success", false);
                String res = json.toString();
                // Sending the response.
                exchange.sendResponseHeaders(404, res.length());
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(res.getBytes());
                outputStream.close();
                return;
            }
        }
        // Read server.properties.
        byte[] properties = Files.readAllBytes(Paths.get("server.properties"));
        // Build the JSON object.
        JsonObject json = new JsonObject();
        json.addProperty("code", 200);
        json.addProperty("content", new String(properties));
        String res = json.toString();
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}
