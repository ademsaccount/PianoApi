package com.piano.repository;

import com.piano.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InstrumentRepository extends JpaRepository<Instrument, UUID> {
    List<Instrument> findByProjectIdOrderBySortOrderAsc(UUID projectId);

    @Modifying
    @Query(value = "DELETE FROM notes WHERE instrument_id IN (SELECT id FROM instruments WHERE project_id = ?1)", nativeQuery = true)
    void deleteNotesByProjectId(UUID projectId);

    @Modifying
    @Query(value = "DELETE FROM instruments WHERE project_id = ?1", nativeQuery = true)
    void deleteByProjectId(UUID projectId);
}
