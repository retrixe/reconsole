package com.reconsole.ReConsole.HttpHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class RootEndpoint implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String res = "{\"code\": 200}";
        // Sending the response.
        exchange.sendResponseHeaders(200, res.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(res.getBytes());
        outputStream.close();
    }
}
