package com.otpservicepj.api.handler.admin;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.otpservicepj.dao.ConfigDAO;
import com.otpservicepj.model.OtpConfig;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class AdminConfigHandler implements HttpHandler {
    private final ConfigDAO configDAO;
    private final Gson gson = new Gson();

    public AdminConfigHandler(ConfigDAO configDAO) {
        this.configDAO = configDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody())) {
                OtpConfig newConfig = gson.fromJson(reader, OtpConfig.class);
                configDAO.updateConfig(newConfig);
                sendResponse(exchange, 200, "Config updated");
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }
    
    private void sendResponse(HttpExchange exchange, int code, String message) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
}
