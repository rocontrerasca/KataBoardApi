package com.kataboard.dtos.user;

import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    private Boolean active;
}