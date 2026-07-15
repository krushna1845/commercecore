package com.krushna.commercecore.dto;

public class MfaSetupDTO {
    private String secret;
    private String qrCodeUrl;

    public MfaSetupDTO() {}

    public MfaSetupDTO(String secret, String qrCodeUrl) {
        this.secret = secret;
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
}
