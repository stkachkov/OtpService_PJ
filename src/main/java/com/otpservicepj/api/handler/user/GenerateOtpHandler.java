package com.otpservicepj.api.handler.user;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.otpservicepj.model.User;
import com.otpservicepj.model.dto.GenerateOtpRequest;
import com.otpservicepj.service.NotificationServiceFactory;
import com.otpservicepj.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class GenerateOtpHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(GenerateOtpHandler.class);

    private final OtpService otpService;
    private final NotificationServiceFactory notificationFactory;
    private final Gson gson = new Gson();

    public GenerateOtpHandler(OtpService otpService, NotificationServiceFactory notificationFactory) {
        this.otpService = otpService;
        this.notificationFactory = notificationFactory;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            User user = (User) exchange.getAttribute("user");

            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody())) {
                GenerateOtpRequest request = gson.fromJson(reader, GenerateOtpRequest.class);

                String code = otpService.generateCode(user.getId(), request.operationId());

                notificationFactory.getService(request.channel()).ifPresentOrElse(
                    service -> service.send(request.destination(), code),
                    () -> logger.error("No notification service found for channel: {}", request.channel())
                );
                
                sendResponse(exchange, 200, "OTP generation process initiated.");

            } catch (Exception e) {
                logger.error("Error generating OTP: {}", e.getMessage(), e);
                sendResponse(exchange, 500, "Internal Server Error");
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
