package com.otpservicepj.dao;

import com.otpservicepj.config.DatabaseConfig;
import com.otpservicepj.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public User createUser(User user) {
        String sql = "INSERT INTO users (login, password_hash, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getLogin());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
        }
        return null;
    }

    public Optional<User> findUserByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                );
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by login: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<User> findUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
        }
    }

    public List<User> findUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            logger.error("Error finding users by role: {}", e.getMessage(), e);
        }
        return users;
    }

    public List<User> getAllUsersExceptAdmins() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role != 'ADMIN'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            logger.error("Error getting all users except admins: {}", e.getMessage(), e);
        }
        return users;
    }
}
