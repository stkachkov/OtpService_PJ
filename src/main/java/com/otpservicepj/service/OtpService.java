package com.otpservicepj.service;

import com.otpservicepj.dao.ConfigDAO;
import com.otpservicepj.dao.OtpDAO;
import com.otpservicepj.model.OtpCode;
import com.otpservicepj.model.OtpConfig;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

public class OtpService {
    private final OtpDAO otpDAO;
    private final ConfigDAO configDAO;
    private final Random random = new SecureRandom();

    public OtpService(OtpDAO otpDAO, ConfigDAO configDAO) {
        this.otpDAO = otpDAO;
        this.configDAO = configDAO;
    }

    public String generateCode(int userId, String operationId) {
        OtpConfig config = configDAO.getConfig().orElse(new OtpConfig(0, 6, 300)); 
        String code = generateRandomCode(config.getCodeLength());
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(config.getLifetimeSeconds());
        
        OtpCode otpCode = new OtpCode(0, userId, operationId, code, "ACTIVE", expiresAt);
        otpDAO.saveCode(otpCode);
        
        return code;
    }

    public boolean validateCode(String code) {
        return otpDAO.findCode(code).map(otp -> {
            if ("ACTIVE".equals(otp.getStatus()) && otp.getExpiresAt().isAfter(LocalDateTime.now())) {
                otpDAO.updateStatus(otp.getId(), "USED");
                return true;
            }
            return false;
        }).orElse(false);
    }
    
    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
