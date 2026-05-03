package com.otpservicepj.api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.otpservicepj.model.dto.LoginRequest;
import com.otpservicepj.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class LoginHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    private final AuthService authService;
    private final Gson gson = new Gson();

    public LoginHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                LoginRequest request = gson.fromJson(reader, LoginRequest.class);
                Optional<String> tokenOpt = authService.login(request);

                if (tokenOpt.isPresent()) {
                    String response = gson.toJson(Map.of("token", tokenOpt.get()));
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 401, "Unauthorized");
                }
            } catch (Exception e) {
                logger.error("Error during login: {}", e.getMessage(), e);
                sendResponse(exchange, 500, "Internal Server Error");
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
