package com.digigram.digigram_backend.model;

import java.time.Instant;

public class Certificate {

    private String id;
    private String uid;
    private String phone;
    private String type;
    private String reason;
    private String status;
    private String verificationCode;
    private Instant createdAt;
    private Instant approvedAt;

    public Certificate() {}

    // Getters & Setters
}