package com.otpservicepj.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TelegramNotificationService implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);

    private final String botToken;
    private final String chatId;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public TelegramNotificationService(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @Override
    public void send(String destination, String code) {
        if (botToken == null || chatId == null || botToken.isEmpty() || chatId.isEmpty()) {
            logger.warn("Telegram bot token or chat ID is not configured. Skipping notification.");
            return;
        }
        String message = String.format("User %s requested an OTP code: %s", destination, code);
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                botToken, chatId, encodedMessage);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.error("Telegram API error. Status: {}, Body: {}", response.statusCode(), response.body());
            } else {
                logger.info("Telegram message sent.");
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error sending Telegram message: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
