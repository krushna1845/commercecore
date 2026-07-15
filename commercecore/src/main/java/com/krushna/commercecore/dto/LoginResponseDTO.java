package com.krushna.commercecore.dto;

public class LoginResponseDTO {

    private String token;
    private String refreshToken;
    private String username;
    private String role;
    private boolean mfaRequired = false;
    private String tempToken;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, String refreshToken, String username, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
        this.mfaRequired = false;
    }

    public LoginResponseDTO(boolean mfaRequired, String tempToken, String username) {
        this.mfaRequired = mfaRequired;
        this.tempToken = tempToken;
        this.username = username;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isMfaRequired() { return mfaRequired; }
    public void setMfaRequired(boolean mfaRequired) { this.mfaRequired = mfaRequired; }

    public String getTempToken() { return tempToken; }
    public void setTempToken(String tempToken) { this.tempToken = tempToken; }
}
