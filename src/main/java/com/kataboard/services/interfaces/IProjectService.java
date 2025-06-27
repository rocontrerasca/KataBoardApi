package com.kataboard.services.interfaces;

import com.kataboard.dtos.project.ProjectRequest;
import com.kataboard.dtos.project.ProjectResponse;

import java.util.List;

public interface IProjectService {
    ProjectResponse create(ProjectRequest request, String ownerEmail);
    List<ProjectResponse> getAllByUser(String userEmail);
    ProjectResponse getById(Long id);
    ProjectResponse update(Long id, ProjectRequest request, String requesterEmail);
    void delete(Long id, String requesterEmail);

    void addCollaborator(Long projectId, String emailToAdd, String requesterEmail);
}
