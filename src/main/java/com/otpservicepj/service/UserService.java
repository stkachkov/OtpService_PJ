package com.otpservicepj.service;

import com.otpservicepj.dao.UserDAO;
import com.otpservicepj.model.User;

import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void deleteUser(int userIdToDelete, User actor) {
        if (actor == null || !"ADMIN".equalsIgnoreCase(actor.getRole())) {
            throw new SecurityException("User does not have permission to delete users.");
        }
        
        userDAO.deleteUser(userIdToDelete);
    }

    public List<User> getAllUsersExceptAdmins() {
        return userDAO.getAllUsersExceptAdmins();
    }
}
