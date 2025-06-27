package com.kataboard.dtos.project;

import com.kataboard.util.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProjectRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private ProjectStatus status;

    private List<String> collaborators;
}
