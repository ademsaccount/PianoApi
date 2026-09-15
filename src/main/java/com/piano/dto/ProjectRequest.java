package com.piano.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectRequest {
    private String title;
    private String composer;
    private String keySignature;
    private String mode;
    private String timeSignature;
    private Integer bpm;
    private Double scale;
    private List<InstrumentDto> instruments;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class InstrumentDto {
        private String name;
        private String type;
        private Integer sortOrder;
        private Boolean hidden;
        private List<NoteDto> notes;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class NoteDto {
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
