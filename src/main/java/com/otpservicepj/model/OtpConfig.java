package com.otpservicepj.model;

public class OtpConfig {
    private final int id;
    private final int codeLength;
    private final int lifetimeSeconds;

    public OtpConfig(int id, int codeLength, int lifetimeSeconds) {
        this.id = id;
        this.codeLength = codeLength;
        this.lifetimeSeconds = lifetimeSeconds;
    }
    
    public int getId() { return id; }
    public int getCodeLength() { return codeLength; }
    public int getLifetimeSeconds() { return lifetimeSeconds; }
}
