package com.piano.repository;

import com.piano.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findByInstrumentIdOrderBySortOrderAsc(UUID instrumentId);
    void deleteByInstrumentId(UUID instrumentId);
}
