package com.krushna.commercecore.dto;

import jakarta.validation.constraints.NotBlank;

public class TokenRefreshRequestDTO {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    public TokenRefreshRequestDTO() {}

    public TokenRefreshRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
