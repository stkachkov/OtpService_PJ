package com.otpservicepj.auth;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TokenStore {
    private final Map<String, SimpleToken> tokenMap = new ConcurrentHashMap<>();

    public void storeToken(SimpleToken token) {
        tokenMap.put(token.getToken(), token);
    }

    public Optional<SimpleToken> validateToken(String tokenValue) {
        SimpleToken token = tokenMap.get(tokenValue);
        if (token != null && !token.isExpired()) {
            return Optional.of(token);
        }
        if (token != null) {
            tokenMap.remove(tokenValue);
        }
        return Optional.empty();
    }
}
