package com.kataboard.controllers;

import com.kataboard.dtos.task.StatusUpdateRequest;
import com.kataboard.dtos.task.TaskRequest;
import com.kataboard.dtos.task.TaskResponse;
import com.kataboard.security.JwtUtil;
import com.kataboard.services.interfaces.ITaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;
    private final JwtUtil jwtUtil;

    @PostMapping("/project/{projectId}")
    public ResponseEntity<TaskResponse> create(@PathVariable Long projectId,
                                               @RequestBody @Valid TaskRequest request,
                                               HttpServletRequest httpRequest) {
        String email = jwtUtil.extractUsername(jwtUtil.resolveToken(httpRequest));
        return ResponseEntity.ok(taskService.create(projectId, request, email));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> list(@PathVariable Long projectId,
                                                   HttpServletRequest httpRequest) {
        String email = jwtUtil.extractUsername(jwtUtil.resolveToken(httpRequest));
        return ResponseEntity.ok(taskService.getAllByProject(projectId, email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id,
                                               @RequestBody @Valid TaskRequest request,
                                               HttpServletRequest httpRequest) {
        String email = jwtUtil.extractUsername(jwtUtil.resolveToken(httpRequest));
        return ResponseEntity.ok(taskService.update(id, request, email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       HttpServletRequest httpRequest) {
        String email = jwtUtil.extractUsername(jwtUtil.resolveToken(httpRequest));
        taskService.delete(id, email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        taskService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }
}
