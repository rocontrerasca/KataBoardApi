package com.kataboard.dtos.project;

import com.kataboard.util.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProjectResponse  implements Comparable<ProjectResponse>{
    private Long id;
    private String name;
    private String description;
    private String ownerEmail;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private List<String> collaboratorEmails;

    @Override
    public int compareTo(ProjectResponse other) {
        return this.createdAt.compareTo(other.createdAt);
    }
}

