package com.otpservicepj.service;

import com.otpservicepj.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void start() {
        scheduler.scheduleAtFixedRate(this::expireCodes, 0, 1, TimeUnit.MINUTES);
    }

    private void expireCodes() {
        String sql = "UPDATE otp_codes SET status = 'EXPIRED' WHERE expires_at <= ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            int updatedRows = pstmt.executeUpdate();
            if (updatedRows > 0) {
                logger.info("Expired {} OTP codes.", updatedRows);
            }
        } catch (SQLException e) {
            logger.error("Error expiring OTP codes: {}", e.getMessage(), e);
        }
    }

    public void stop() {
        scheduler.shutdown();
    }
}
