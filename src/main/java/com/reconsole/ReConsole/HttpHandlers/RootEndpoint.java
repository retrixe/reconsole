package com.reconsole.ReConsole.HttpHandlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Server;

import java.io.IOException;
import java.io.OutputStream;

public class RootEndpoint implements HttpHandler {
    private Server server;
    public RootEndpoint(Server server1) { server = server1; }

    public void handle(HttpExchange exchange) throws IOException {
        // Build the JSON object.
        JsonObject json = new JsonObject();
        json.addProperty("code", 200);
        json.addProperty("online", true);
        json.addProperty("maxPlayers", server.getMaxPlayers());
        json.addProperty("playersOnline", server.getOnlinePlayers().size());
        json.addProperty("versionName", server.getVersion());
        String res = json.toString();
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}
