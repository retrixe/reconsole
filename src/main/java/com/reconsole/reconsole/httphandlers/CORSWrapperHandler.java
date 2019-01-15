package com.reconsole.reconsole.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class CORSWrapperHandler implements HttpHandler {
    private HttpHandler handler;
    private boolean enablePreflight = false;
    public CORSWrapperHandler(HttpHandler httpHandler) {
        handler = httpHandler;
    }
    public CORSWrapperHandler(HttpHandler httpHandler, boolean preflight) {
        handler = httpHandler;
        enablePreflight = preflight;
    }

    public void handle(HttpExchange exchange) throws IOException {
        if (enablePreflight) this.handlePreflights(exchange);
        else this.handleSimpleRequest(exchange);
    }

    private void handleSimpleRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add(
                "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept"
        );
        handler.handle(exchange);
    }

    private void handlePreflights(HttpExchange exchange) throws IOException {
        // Implement CORS on the exchange.
        if (!enablePreflight) this.handleSimpleRequest(exchange);
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", exchange.getRequestHeaders().getFirst(
            "Origin"
        ));
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Max-Age", "86400");
            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Username, Password"
            );
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
            return;
        }
        handler.handle(exchange);
    }
}
