package com.kataboard.dtos.task;

import com.kataboard.dtos.tag.TagResponse;
import com.kataboard.util.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer estimatedHours;
    private String assignedToEmail;
    private String createdByEmail;
    private List<TagResponse> tags;
}
