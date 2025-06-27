package com.kataboard.dtos.task;

import com.kataboard.util.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class TaskRequest {
    @NotBlank
    private String title;

    private String description;
    private TaskStatus status;
    private LocalDate dueDate;

    @Min(1)
    @Max(1000)
    private Integer estimatedHours;

    private String assignedToEmail;

    private Set<Long> tagIds;
}
