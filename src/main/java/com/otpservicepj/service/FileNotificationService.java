package com.otpservicepj.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

public class FileNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(FileNotificationService.class);

    @Override
    public void send(String destination, String code) {
        try (FileWriter writer = new FileWriter("otp_codes.txt", true)) {
            writer.write("Destination: " + destination + ", Code: " + code + System.lineSeparator());
        } catch (IOException e) {
            logger.error("Error writing to file: {}", e.getMessage(), e);
        }
    }
}
