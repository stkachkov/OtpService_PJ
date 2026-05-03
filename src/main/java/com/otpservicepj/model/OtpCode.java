package com.otpservicepj.model;

import java.time.LocalDateTime;

public class OtpCode {
    private int id;
    private final int userId;
    private final String operationId;
    private final String code;
    private final String status;
    private final LocalDateTime expiresAt;

    public OtpCode(int id, int userId, String operationId, String code, String status, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.operationId = operationId;
        this.code = code;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public String getOperationId() { return operationId; }
    public String getCode() { return code; }
    public String getStatus() { return status; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}
