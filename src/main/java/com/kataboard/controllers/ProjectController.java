package com.kataboard.controllers;

import com.kataboard.dtos.project.CollaboratorRequest;
import com.kataboard.dtos.project.ProjectRequest;
import com.kataboard.dtos.project.ProjectResponse;
import com.kataboard.security.JwtUtil;
import com.kataboard.services.interfaces.IProjectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@RequestBody @Valid ProjectRequest request,
                                                  HttpServletRequest httpRequest) {
        String userEmail = jwtUtil.extractUsername(jwtUtil.resolveToken(httpRequest));
        return ResponseEntity.ok(projectService.create(request, userEmail));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> list(HttpServletRequest httpRequest) {
        String userEmail = jwtUtil.extractUsername(jwtUtil.resolveToken(httpRequest));
        return ResponseEntity.ok(projectService.getAllByUser(userEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(@PathVariable Long id,
                                                  @RequestBody @Valid ProjectRequest request,
                                                  HttpServletRequest httpRequest) {
        String userEmail = jwtUtil.extractUsername(jwtUtil.resolveToken(httpRequest));
        return ResponseEntity.ok(projectService.update(id, request, userEmail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       HttpServletRequest httpRequest) {
        String userEmail = jwtUtil.extractUsername(jwtUtil.resolveToken(httpRequest));
        projectService.delete(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/collaborators")
    public ResponseEntity<Void> addCollaborator(@PathVariable Long projectId,
                                                @RequestBody CollaboratorRequest request,
                                                HttpServletRequest httpRequest) {
        String email = jwtUtil.extractUsername(jwtUtil.resolveToken(httpRequest));
        projectService.addCollaborator(projectId, request.getEmail(), email);
        return ResponseEntity.ok().build();
    }
}

