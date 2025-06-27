package com.kataboard.services.interfaces;

import com.kataboard.dtos.task.TaskRequest;
import com.kataboard.dtos.task.TaskResponse;
import com.kataboard.util.TaskStatus;

import java.util.List;

public interface ITaskService {
    TaskResponse create(Long projectId, TaskRequest request, String userEmail);
    List<TaskResponse> getAllByProject(Long projectId, String userEmail);
    TaskResponse getById(Long id);
    TaskResponse update(Long id, TaskRequest request, String userEmail);
    void delete(Long id, String userEmail);
    void updateStatus(Long taskId, TaskStatus newStatus);
}
