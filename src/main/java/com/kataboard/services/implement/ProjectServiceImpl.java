package com.kataboard.services.implement;

import com.kataboard.dtos.project.ProjectRequest;
import com.kataboard.dtos.project.ProjectResponse;
import com.kataboard.exceptions.ForbiddenException;
import com.kataboard.exceptions.NotFoundException;
import com.kataboard.models.Project;
import com.kataboard.models.User;
import com.kataboard.repositories.ProjectRepository;
import com.kataboard.repositories.UserRepository;
import com.kataboard.services.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements IProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public ProjectResponse create(ProjectRequest request, String ownerEmail) {
        var owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Set<User> collaborators = new HashSet<>();
        for (String email : request.getCollaborators()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
            collaborators.add(user);
        }

        var project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .active(true)
                .status(request.getStatus())
                .collaborators(collaborators)
                .build();

        return toResponse(projectRepository.save(project));
    }

    @Override
    public List<ProjectResponse> getAllByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        List<Project> owned = projectRepository.findByOwner(user);
        List<Project> collaborated = projectRepository.findByCollaboratorsContains(user);

        Set<Project> all = new HashSet<>();
        all.addAll(owned);
        all.addAll(collaborated);

        return all.stream()
                .map(this::toResponse)
                .sorted()
                .toList();
    }

    @Override
    public ProjectResponse getById(Long id) {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proyecto no encontrado"));
        return toResponse(project);
    }

    @Override
    public ProjectResponse update(Long id, ProjectRequest request, String requesterEmail) {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proyecto no encontrado"));

        if (!project.getOwner().getEmail().equals(requesterEmail)) {
            throw new ForbiddenException("No puedes modificar este proyecto");
        }

        Set<User> collaborators = new HashSet<>();
        for (String email : request.getCollaborators()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
            collaborators.add(user);
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setCollaborators(collaborators);

        return toResponse(projectRepository.save(project));
    }

    @Override
    public void delete(Long id, String requesterEmail) {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proyecto no encontrado"));

        if (!project.getOwner().getEmail().equals(requesterEmail)) {
            throw new ForbiddenException("No puedes eliminar este proyecto");
        }

        projectRepository.delete(project);
    }

    @Override
    public void addCollaborator(Long projectId, String emailToAdd, String requesterEmail) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Proyecto no encontrado"));

        if (!project.getOwner().getEmail().equals(requesterEmail)) {
            throw new ForbiddenException("Solo el propietario puede agregar colaboradores");
        }

        var userToAdd = userRepository.findByEmail(emailToAdd)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (project.getCollaborators() != null && !project.getCollaborators().isEmpty() && project.getCollaborators().contains(userToAdd)) {
            return;
        }
        if(project.getCollaborators() == null){
            project.setCollaborators(new HashSet<>());
        }
        project.getCollaborators().add(userToAdd);
        projectRepository.save(project);
    }

    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerEmail(project.getOwner().getEmail())
                .active(project.isActive())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .status(project.getStatus())
                .collaboratorEmails(
                        Optional.ofNullable(project.getCollaborators())
                                .orElse(Collections.emptySet())
                                .stream()
                                .map(User::getEmail)
                                .toList()
                )
                .build();
    }
}
