package com.kataboard.repositories;

import com.kataboard.models.Project;
import com.kataboard.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerEmail(String email);
    List<Project> findByOwner(User owner);
    List<Project> findByCollaboratorsContains(User user);
}
