package com.kataboard.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private long expiredIn;
}
