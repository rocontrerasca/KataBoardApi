package com.kataboard.dtos.task;

import com.kataboard.util.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {
    private TaskStatus status;
}
