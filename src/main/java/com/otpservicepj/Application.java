package com.otpservicepj;

import com.otpservicepj.api.auth.AuthFilter;
import com.otpservicepj.api.handler.admin.AdminConfigHandler;
import com.otpservicepj.api.handler.admin.AdminUsersHandler;
import com.otpservicepj.api.handler.user.GenerateOtpHandler;
import com.otpservicepj.api.handler.user.ValidateOtpHandler;
import com.otpservicepj.auth.TokenStore;
import com.otpservicepj.dao.ConfigDAO;
import com.otpservicepj.dao.OtpDAO;
import com.otpservicepj.dao.UserDAO;
import com.otpservicepj.api.handler.LoginHandler;
import com.otpservicepj.api.handler.RegisterHandler;
import com.otpservicepj.service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import com.sun.net.httpserver.HttpServer;

public class Application {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        UserDAO userDAO = new UserDAO();
        OtpDAO otpDAO = new OtpDAO();
        ConfigDAO configDAO = new ConfigDAO();

        TokenStore tokenStore = new TokenStore();
        AuthService authService = new AuthService(userDAO, tokenStore);
        UserService userService = new UserService(userDAO);
        OtpService otpService = new OtpService(otpDAO, configDAO);
        ScheduledTaskService scheduledTaskService = new ScheduledTaskService();

        String telegramToken = System.getenv("TELEGRAM_BOT_TOKEN");
        String telegramChatId = System.getenv("TELEGRAM_CHAT_ID");

        Map<String, NotificationService> notificationServices = Map.of(
                "FILE", new FileNotificationService(),
                "EMAIL", new EmailNotificationService(),
                "SMS", new SmsNotificationService(),
                "TELEGRAM", new TelegramNotificationService(telegramToken, telegramChatId)
        );
        NotificationServiceFactory notificationFactory = new NotificationServiceFactory(notificationServices);

        server.createContext("/register", new RegisterHandler(authService));
        server.createContext("/login", new LoginHandler(authService));

        var userApiContext = server.createContext("/api/user/generate-otp", new GenerateOtpHandler(otpService, notificationFactory));
        userApiContext.getFilters().add(new AuthFilter(tokenStore, userDAO, List.of("USER", "ADMIN")));
        
        var validateOtpContext = server.createContext("/api/user/validate-otp", new ValidateOtpHandler(otpService));
        validateOtpContext.getFilters().add(new AuthFilter(tokenStore, userDAO, List.of("USER", "ADMIN")));

        var adminConfigContext = server.createContext("/api/admin/config", new AdminConfigHandler(configDAO));
        adminConfigContext.getFilters().add(new AuthFilter(tokenStore, userDAO, List.of("ADMIN")));
        
        var adminUsersContext = server.createContext("/api/admin/users", new AdminUsersHandler(userService));
        adminUsersContext.getFilters().add(new AuthFilter(tokenStore, userDAO, List.of("ADMIN")));

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        scheduledTaskService.start();
        
        server.start();
        System.out.println("Server started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop(0);
            scheduledTaskService.stop();
        }));
    }
}
