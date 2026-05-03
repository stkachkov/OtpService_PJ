package com.otpservicepj.api.auth;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import com.otpservicepj.auth.TokenStore;
import com.otpservicepj.dao.UserDAO;
import com.otpservicepj.model.User;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public class AuthFilter extends Filter {
    private final TokenStore tokenStore;
    private final UserDAO userDAO;
    private final List<String> requiredRoles;

    public AuthFilter(TokenStore tokenStore, UserDAO userDAO, List<String> requiredRoles) {
        this.tokenStore = tokenStore;
        this.userDAO = userDAO;
        this.requiredRoles = requiredRoles;
    }

    @Override
    public String description() {
        return "Handles token-based authentication and authorization";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(exchange, 401, "Unauthorized: Missing or invalid token");
            return;
        }

        String token = authHeader.substring(7);
        Optional<User> userOpt = tokenStore.validateToken(token)
            .flatMap(simpleToken -> userDAO.findUserById(simpleToken.getUserId()));

        if (userOpt.isEmpty()) {
            sendError(exchange, 401, "Unauthorized: Invalid or expired token");
            return;
        }

        User user = userOpt.get();
        if (!requiredRoles.isEmpty() && !requiredRoles.contains(user.getRole())) {
            sendError(exchange, 403, "Forbidden: Insufficient permissions");
            return;
        }
        
        exchange.setAttribute("user", user);
        chain.doFilter(exchange);
    }
    
    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
}
