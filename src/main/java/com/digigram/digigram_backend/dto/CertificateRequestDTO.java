package com.digigram.digigram_backend.dto;

public class CertificateRequestDTO {

    private String phone;
    private String type;
    private String reason;

    public CertificateRequestDTO() {}

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}