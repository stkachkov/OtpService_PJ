package com.otpservicepj.model.dto;

public record GenerateOtpRequest(String operationId, String channel, String destination) {}
