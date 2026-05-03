package com.otpservicepj.api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.otpservicepj.model.dto.RegisterRequest;
import com.otpservicepj.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RegisterHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(RegisterHandler.class);

    private final AuthService authService;
    private final Gson gson = new Gson();

    public RegisterHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                RegisterRequest request = gson.fromJson(reader, RegisterRequest.class);
                boolean success = authService.register(request);

                if (success) {
                    sendResponse(exchange, 201, "User created successfully");
                } else {
                    sendResponse(exchange, 400, "Bad Request: User might already exist or invalid role");
                }
            } catch (Exception e) {
                logger.error("Error during registration: {}", e.getMessage(), e);
                sendResponse(exchange, 500, "Internal Server Error");
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
