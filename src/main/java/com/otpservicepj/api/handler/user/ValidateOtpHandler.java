package com.otpservicepj.api.handler.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.otpservicepj.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Map;

public class ValidateOtpHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ValidateOtpHandler.class);
    private final OtpService otpService;
    private final Gson gson = new Gson();

    public ValidateOtpHandler(OtpService otpService) {
        this.otpService = otpService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody())) {
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> body = gson.fromJson(reader, type);
                String code = body.get("code");
                
                if (otpService.validateCode(code)) {
                    sendResponse(exchange, 200, "{\"status\":\"success\", \"message\":\"OTP is valid\"}");
                } else {
                    sendResponse(exchange, 400, "{\"status\":\"error\", \"message\":\"Invalid or expired OTP\"}");
                }
            } catch (Exception e) {
                logger.error("Error validating OTP: {}", e.getMessage(), e);
                sendResponse(exchange, 500, "Internal Server Error");
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, message.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
}
