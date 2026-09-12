package com.piano.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectResponse {
    private UUID id;
    private String title;
    private String composer;
    private String keySignature;
    private String mode;
    private String timeSignature;
    private Integer bpm;
    private Instant createdAt;
    private Instant updatedAt;
    private List<InstrumentResponse> instruments;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class InstrumentResponse {
        private UUID id;
        private String name;
        private String type;
        private Integer sortOrder;
        private List<NoteResponse> notes;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class NoteResponse {
        private UUID id;
        private String type;
        private String letter;
        private Integer octave;
        private Double duration;
        private String staff;
        private String chordId;
        private Boolean manualSpelling;
        private Integer sortOrder;
    }
}
