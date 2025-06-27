package com.kataboard.dtos;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}