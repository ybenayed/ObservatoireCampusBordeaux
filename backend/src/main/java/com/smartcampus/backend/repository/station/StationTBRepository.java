package com.smartcampus.backend.repository.station;

import com.smartcampus.backend.entity.station.StationTB;
import com.smartcampus.backend.dto.station.StationPositionTBDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StationTBRepository extends JpaRepository<StationTB, Long> {
    Optional<StationTB> findByStopId(String stopId);
    boolean existsByStopId(String stopId);

    @Query("""
        SELECT new com.smartcampus.backend.dto.station.StationPositionTBDTO(
            s.id, s.stopId, s.nom, s.mode, s.latitude, s.longitude)
        FROM StationTB s
        """)
    List<StationPositionTBDTO> findAllPositions();      
    @Query("""
        SELECT new com.smartcampus.backend.dto.station.StationPositionTBDTO(
            s.id, s.stopId, s.nom, s.mode, s.latitude, s.longitude)
        FROM StationTB s
        WHERE s.mode = :mode
        """)
    List<StationPositionTBDTO> findPositionsByMode(String mode);
}