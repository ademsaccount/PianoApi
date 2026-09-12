package com.piano.repository;

import com.piano.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InstrumentRepository extends JpaRepository<Instrument, UUID> {
    List<Instrument> findByProjectIdOrderBySortOrderAsc(UUID projectId);
}
