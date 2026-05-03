package com.otpservicepj.dao;

import com.otpservicepj.config.DatabaseConfig;
import com.otpservicepj.model.OtpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class ConfigDAO {

    private static final Logger logger = LoggerFactory.getLogger(ConfigDAO.class);

    public Optional<OtpConfig> getConfig() {
        String sql = "SELECT * FROM otp_config LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return Optional.of(new OtpConfig(
                    rs.getInt("id"), rs.getInt("code_length"), rs.getInt("lifetime_seconds")
                ));
            }
        } catch (SQLException e) {
            logger.error("Error getting config: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void updateConfig(OtpConfig config) {
        
        String sql = "INSERT INTO otp_config (id, code_length, lifetime_seconds) VALUES (1, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET code_length = EXCLUDED.code_length, lifetime_seconds = EXCLUDED.lifetime_seconds";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, config.getCodeLength());
            pstmt.setInt(2, config.getLifetimeSeconds());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating config: {}", e.getMessage(), e);
        }
    }
}
