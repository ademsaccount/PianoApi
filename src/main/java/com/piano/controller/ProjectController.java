package com.piano.controller;

import com.piano.dto.ProjectRequest;
import com.piano.dto.ProjectResponse;
import com.piano.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private static final UUID DUMMY_USER = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final ProjectService projectService;

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> listProjects(Authentication auth) {
        UUID userId = DUMMY_USER;
        return ResponseEntity.ok(projectService.listProjects(userId));
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> createProject(Authentication auth, @RequestBody ProjectRequest req) {
        UUID userId = DUMMY_USER;
        return ResponseEntity.ok(projectService.createProject(userId, req));
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> getProject(Authentication auth, @PathVariable UUID id) {
        UUID userId = DUMMY_USER;
        return ResponseEntity.ok(projectService.getProject(userId, id));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(Authentication auth, @PathVariable UUID id, @RequestBody ProjectRequest req) {
        UUID userId = DUMMY_USER;
        return ResponseEntity.ok(projectService.updateProject(userId, id, req));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(Authentication auth, @PathVariable UUID id) {
        UUID userId = DUMMY_USER;
        projectService.deleteProject(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/instruments/{id}/notes")
    public ResponseEntity<Void> bulkReplaceNotes(Authentication auth, @PathVariable UUID id, @RequestBody List<ProjectRequest.NoteDto> notes) {
        UUID userId = DUMMY_USER;
        projectService.bulkReplaceNotes(userId, id, notes);
        return ResponseEntity.ok().build();
    }
}
