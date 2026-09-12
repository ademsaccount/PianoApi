package com.piano.repository;

import com.piano.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByUserIdOrderByUpdatedAtDesc(UUID userId);
}
