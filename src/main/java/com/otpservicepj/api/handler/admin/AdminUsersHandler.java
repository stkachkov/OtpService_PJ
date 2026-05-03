package com.otpservicepj.api.handler.admin;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.otpservicepj.model.User;
import com.otpservicepj.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class AdminUsersHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(AdminUsersHandler.class);
    private final UserService userService;
    private final Gson gson = new Gson();

    public AdminUsersHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            User actor = (User) exchange.getAttribute("user");

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                if (path.equals("/api/admin/users")) {
                    List<User> users = userService.getAllUsersExceptAdmins();
                    String response = gson.toJson(users);
                    sendResponse(exchange, 200, response);
                }
            } else if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
                String[] parts = path.split("/");
                if (parts.length == 5) { 
                    int userIdToDelete = Integer.parseInt(parts[4]);
                    userService.deleteUser(userIdToDelete, actor);
                    sendResponse(exchange, 200, "User deleted");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (SecurityException e) {
            logger.warn("Authorization failure: {}", e.getMessage());
            sendResponse(exchange, 403, "Forbidden: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing admin user request: {}", e.getMessage(), e);
            sendResponse(exchange, 500, "Internal Server Error");
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
