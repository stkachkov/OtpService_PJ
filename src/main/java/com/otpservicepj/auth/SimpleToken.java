package com.otpservicepj.auth;

import java.time.Instant;
import java.util.UUID;

public class SimpleToken {
    private final String token;
    private final int userId;
    private final Instant expiryDate;

    public SimpleToken(int userId, long validityInSeconds) {
        this.token = UUID.randomUUID().toString();
        this.userId = userId;
        this.expiryDate = Instant.now().plusSeconds(validityInSeconds);
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}
