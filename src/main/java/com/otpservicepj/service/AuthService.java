package com.otpservicepj.service;

import com.otpservicepj.auth.SimpleToken;
import com.otpservicepj.auth.TokenStore;
import com.otpservicepj.dao.UserDAO;
import com.otpservicepj.model.User;
import com.otpservicepj.model.dto.LoginRequest;
import com.otpservicepj.model.dto.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO;
    private final TokenStore tokenStore;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserDAO userDAO, TokenStore tokenStore) {
        this.userDAO = userDAO;
        this.tokenStore = tokenStore;
    }

    public boolean register(RegisterRequest request) {
        if ("ADMIN".equalsIgnoreCase(request.role())) {
            if (!userDAO.findUsersByRole("ADMIN").isEmpty()) {
                 return false;
            }
        }
        
        if (userDAO.findUserByLogin(request.login()).isPresent()) {
            return false;
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User newUser = new User(0, request.login(), hashedPassword, request.role().toUpperCase());
        
        User createdUser = userDAO.createUser(newUser);
        return createdUser != null;
    }

    public Optional<String> login(LoginRequest request) {
        Optional<User> userOpt = userDAO.findUserByLogin(request.login());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                SimpleToken token = new SimpleToken(user.getId(), 3600);
                tokenStore.storeToken(token);
                return Optional.of(token.getToken());
            }
        }
        return Optional.empty();
    }
}
