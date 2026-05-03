package com.otpservicepj.service;

import java.util.Map;
import java.util.Optional;

public class NotificationServiceFactory {
    private final Map<String, NotificationService> services;

    public NotificationServiceFactory(Map<String, NotificationService> services) {
        this.services = services;
    }

    public Optional<NotificationService> getService(String channel) {
        return Optional.ofNullable(services.get(channel.toUpperCase()));
    }
}
