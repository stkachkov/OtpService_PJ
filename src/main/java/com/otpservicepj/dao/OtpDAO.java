package com.otpservicepj.dao;

import com.otpservicepj.config.DatabaseConfig;
import com.otpservicepj.model.OtpCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class OtpDAO {

    private static final Logger logger = LoggerFactory.getLogger(OtpDAO.class);

    public OtpCode saveCode(OtpCode otpCode) {
        String sql = "INSERT INTO otp_codes (user_id, operation_id, code, status, expires_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, otpCode.getUserId());
            pstmt.setString(2, otpCode.getOperationId());
            pstmt.setString(3, otpCode.getCode());
            pstmt.setString(4, otpCode.getStatus());
            pstmt.setTimestamp(5, Timestamp.valueOf(otpCode.getExpiresAt()));
            
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    otpCode.setId(rs.getInt(1));
                    return otpCode;
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving OTP code: {}", e.getMessage(), e);
        }
        return null;
    }

    public Optional<OtpCode> findCode(String code) {
        String sql = "SELECT * FROM otp_codes WHERE code = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new OtpCode(
                    rs.getInt("id"), rs.getInt("user_id"), rs.getString("operation_id"),
                    rs.getString("code"), rs.getString("status"),
                    rs.getTimestamp("expires_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            logger.error("Error finding OTP code: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating OTP status: {}", e.getMessage(), e);
        }
    }
}
