package com.kataboard.services.implement;

import com.kataboard.dtos.tag.TagResponse;
import com.kataboard.dtos.task.TaskRequest;
import com.kataboard.dtos.task.TaskResponse;
import com.kataboard.exceptions.ForbiddenException;
import com.kataboard.exceptions.NotFoundException;
import com.kataboard.models.Project;
import com.kataboard.models.Tag;
import com.kataboard.models.Task;
import com.kataboard.models.User;
import com.kataboard.repositories.ProjectRepository;
import com.kataboard.repositories.TagRepository;
import com.kataboard.repositories.TaskRepository;
import com.kataboard.repositories.UserRepository;
import com.kataboard.services.interfaces.ITaskService;
import com.kataboard.util.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements ITaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;

    @Override
    public TaskResponse create(Long projectId, TaskRequest request, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Proyecto no encontrado"));

        boolean isOwner = project.getOwner().getEmail().equals(userEmail);
        boolean isCollaborator = project.getCollaborators()
                .stream()
                .anyMatch(u -> u.getEmail().equals(userEmail));

        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Usuario creador no encontrado"));

        if (!isOwner && !isCollaborator) {
            throw new ForbiddenException("No tienes permisos para crear tareas en este proyecto");
        }

        User assignedUser = null;
        if (request.getAssignedToEmail() != null) {
            assignedUser = userRepository.findByEmail(request.getAssignedToEmail())
                    .orElseThrow(() -> new NotFoundException("Usuario asignado no existe"));

            boolean isOwnerAssignedUser = project.getOwner().equals(assignedUser);
            boolean isCollaboratorAssignedUser = project.getCollaborators().contains(assignedUser);

            if (!isOwnerAssignedUser && !isCollaboratorAssignedUser) {
                throw new ForbiddenException("El usuario no pertenece al proyecto");
            }
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .dueDate(request.getDueDate())
                .project(project)
                .estimatedHours(request.getEstimatedHours())
                .assignedTo(assignedUser)
                .createdBy(creator)
                .build();
        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            task.setTags(tags);
        }

        return toResponse(taskRepository.save(task));
    }

    @Override
    public List<TaskResponse> getAllByProject(Long projectId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Proyecto no encontrado"));

        if (!project.getOwner().getEmail().equals(userEmail)) {
            throw new ForbiddenException("No autorizado");
        }

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TaskResponse getById(Long id) {
        return toResponse(taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tarea no encontrada")));
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tarea no encontrada"));

        boolean isOwner = task.getProject().getOwner().getEmail().equals(userEmail);
        boolean isCollaborator = task.getProject().getCollaborators()
                .stream()
                .anyMatch(u -> u.getEmail().equals(userEmail));

        if (!isOwner && !isCollaborator) {
            throw new ForbiddenException("No tienes permisos para crear tareas en este proyecto");
        }

        User assignedUser = null;
        if (request.getAssignedToEmail() != null) {
            assignedUser = userRepository.findByEmail(request.getAssignedToEmail())
                    .orElseThrow(() -> new NotFoundException("Usuario asignado no existe"));

            boolean isOwnerAssignedUser = task.getProject().getOwner().equals(assignedUser);
            boolean isCollaboratorAssignedUser = task.getProject().getCollaborators().contains(assignedUser);

            if (!isOwnerAssignedUser && !isCollaboratorAssignedUser) {
                throw new ForbiddenException("El usuario no pertenece al proyecto");
            }
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setAssignedTo(assignedUser);
        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            task.setTags(tags);
        }

        return toResponse(taskRepository.save(task));
    }

    @Override
    public void delete(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tarea no encontrada"));

        if (!task.getProject().getOwner().getEmail().equals(userEmail)) {
            throw new ForbiddenException("No puedes eliminar esta tarea");
        }

        if (!task.getCreatedBy().getEmail().equals(userEmail)) {
            throw new ForbiddenException("No puedes eliminar esta tarea");
        }

        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .projectId(task.getProject().getId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .estimatedHours(task.getEstimatedHours())
                .assignedToEmail(task.getAssignedTo() != null ? task.getAssignedTo().getEmail() : null)
                .createdByEmail(task.getCreatedBy().getEmail())
                .tags(
                        task.getTags().stream()
                                .map(tag -> TagResponse.builder()
                                        .id(tag.getId())
                                        .name(tag.getName())
                                        .color(tag.getColor())
                                        .build())
                                .toList()
                )
                .build();
    }
}
