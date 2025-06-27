package com.kataboard.dtos.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagRequest {
    @NotBlank
    private String name;
    private String color;
}
