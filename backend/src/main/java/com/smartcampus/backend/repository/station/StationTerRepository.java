package com.smartcampus.backend.repository.station;

import com.smartcampus.backend.entity.station.StationTer;
import com.smartcampus.backend.dto.station.StationPositionTerDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StationTerRepository extends JpaRepository<StationTer, Long> {

    Optional<StationTer> findByNavitiaId(String navitiaId);

    boolean existsByNavitiaId(String navitiaId);

    @Query("""
        SELECT new com.smartcampus.backend.dto.station.StationPositionTerDTO(
            s.id, s.navitiaId, s.nom, s.latitude, s.longitude)
        FROM StationTer s
        """)
    List<StationPositionTerDTO> findAllPositions();
}