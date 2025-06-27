package com.kataboard.dtos.user;

import lombok.Data;
import java.util.Set;

@Data
public class UpdateUserRolesRequest {
    private Set<String> roles;
}
