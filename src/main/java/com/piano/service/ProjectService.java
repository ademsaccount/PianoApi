package com.piano.service;

import com.piano.dto.ProjectRequest;
import com.piano.dto.ProjectResponse;
import com.piano.model.Instrument;
import com.piano.model.Note;
import com.piano.model.Project;
import com.piano.repository.InstrumentRepository;
import com.piano.repository.NoteRepository;
import com.piano.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final InstrumentRepository instrumentRepo;
    private final NoteRepository noteRepo;

    public List<ProjectResponse> listProjects(UUID userId) {
        return projectRepo.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional
    public ProjectResponse createProject(UUID userId, ProjectRequest req) {
        Project project = Project.builder()
                .userId(userId)
                .title(req.getTitle() != null ? req.getTitle() : "Untitled")
                .composer(req.getComposer())
                .keySignature(req.getKeySignature() != null ? req.getKeySignature() : "C")
                .mode(req.getMode() != null ? req.getMode() : "major")
                .timeSignature(req.getTimeSignature() != null ? req.getTimeSignature() : "4/4")
                .bpm(req.getBpm() != null ? req.getBpm() : 120)
                .instruments(new ArrayList<>())
                .build();
        projectRepo.save(project);

        if (req.getInstruments() != null) {
            for (var instReq : req.getInstruments()) {
                saveInstrument(project, instReq, 0);
            }
        }

        return getProject(userId, project.getId());
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID userId, UUID projectId) {
        Project project = projectRepo.findById(projectId)
                .filter(p -> p.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return toFullResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(UUID userId, UUID projectId, ProjectRequest req) {
        Project project = projectRepo.findById(projectId)
                .filter(p -> p.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (req.getTitle() != null) project.setTitle(req.getTitle());
        if (req.getComposer() != null) project.setComposer(req.getComposer());
        if (req.getKeySignature() != null) project.setKeySignature(req.getKeySignature());
        if (req.getMode() != null) project.setMode(req.getMode());
        if (req.getTimeSignature() != null) project.setTimeSignature(req.getTimeSignature());
        if (req.getBpm() != null) project.setBpm(req.getBpm());

        projectRepo.save(project);

        if (req.getInstruments() != null) {
            for (var instReq : req.getInstruments()) {
                saveInstrument(project, instReq, 1);
            }
        }

        return getProject(userId, projectId);
    }

    @Transactional
    public void deleteProject(UUID userId, UUID projectId) {
        Project project = projectRepo.findById(projectId)
                .filter(p -> p.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepo.delete(project);
    }

    @Transactional
    public void bulkReplaceNotes(UUID userId, UUID instrumentId, List<ProjectRequest.NoteDto> noteDtos) {
        Instrument instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new RuntimeException("Instrument not found"));

        if (!instrument.getProject().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        noteRepo.deleteByInstrumentId(instrumentId);

        int order = 0;
        for (var noteDto : noteDtos) {
            Note note = Note.builder()
                    .instrument(instrument)
                    .type(noteDto.getType())
                    .letter(noteDto.getLetter())
                    .octave(noteDto.getOctave())
                    .duration(BigDecimal.valueOf(noteDto.getDuration()))
                    .staff(noteDto.getStaff())
                    .chordId(noteDto.getChordId())
                    .manualSpelling(noteDto.getManualSpelling() != null ? noteDto.getManualSpelling() : false)
                    .sortOrder(noteDto.getSortOrder() != null ? noteDto.getSortOrder() : order++)
                    .build();
            noteRepo.save(note);
        }
    }

    private void saveInstrument(Project project, ProjectRequest.InstrumentDto instReq, int defaultOrder) {
        Instrument instrument = Instrument.builder()
                .project(project)
                .name(instReq.getName() != null ? instReq.getName() : "Piano")
                .type(instReq.getType() != null ? instReq.getType() : "grand")
                .sortOrder(instReq.getSortOrder() != null ? instReq.getSortOrder() : defaultOrder)
                .notes(new ArrayList<>())
                .build();
        instrumentRepo.save(instrument);

        if (instReq.getNotes() != null) {
            int order = 0;
            for (var noteDto : instReq.getNotes()) {
                Note note = Note.builder()
                        .instrument(instrument)
                        .type(noteDto.getType())
                        .letter(noteDto.getLetter())
                        .octave(noteDto.getOctave())
                        .duration(BigDecimal.valueOf(noteDto.getDuration()))
                        .staff(noteDto.getStaff())
                        .chordId(noteDto.getChordId())
                        .manualSpelling(noteDto.getManualSpelling() != null ? noteDto.getManualSpelling() : false)
                        .sortOrder(noteDto.getSortOrder() != null ? noteDto.getSortOrder() : order++)
                        .build();
                noteRepo.save(note);
            }
        }
    }

    private ProjectResponse toSummaryResponse(Project p) {
        return ProjectResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .composer(p.getComposer())
                .keySignature(p.getKeySignature())
                .mode(p.getMode())
                .timeSignature(p.getTimeSignature())
                .bpm(p.getBpm())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .instruments(List.of())
                .build();
    }

    private ProjectResponse toFullResponse(Project p) {
        List<ProjectResponse.InstrumentResponse> instruments = p.getInstruments().stream()
                .map(inst -> ProjectResponse.InstrumentResponse.builder()
                        .id(inst.getId())
                        .name(inst.getName())
                        .type(inst.getType())
                        .sortOrder(inst.getSortOrder())
                        .notes(inst.getNotes().stream()
                                .map(n -> ProjectResponse.NoteResponse.builder()
                                        .id(n.getId())
                                        .type(n.getType())
                                        .letter(n.getLetter())
                                        .octave(n.getOctave())
                                        .duration(n.getDuration().doubleValue())
                                        .staff(n.getStaff())
                                        .chordId(n.getChordId())
                                        .manualSpelling(n.getManualSpelling())
                                        .sortOrder(n.getSortOrder())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return ProjectResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .composer(p.getComposer())
                .keySignature(p.getKeySignature())
                .mode(p.getMode())
                .timeSignature(p.getTimeSignature())
                .bpm(p.getBpm())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .instruments(instruments)
                .build();
    }
}
