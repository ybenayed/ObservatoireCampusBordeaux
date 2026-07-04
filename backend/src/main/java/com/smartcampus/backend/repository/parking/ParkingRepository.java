package com.smartcampus.backend.repository.parking;

import com.smartcampus.backend.entity.parking.Parking;
import com.smartcampus.backend.dto.parking.ParkingPositionDTO;  
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
    Optional<Parking> findByIdent(String ident);
    boolean existsByIdent(String ident);
    // Positions legeres pour la carte - ne charge que 6 colonnes, pas toute l'entite
    @Query("""
        SELECT new com.smartcampus.backend.dto.parking.ParkingPositionDTO(
            p.id, p.ident, p.nom, p.taType, p.latitude, p.longitude)
        FROM Parking p
        """)
    List<ParkingPositionDTO> findAllPositions();

    // Positions filtrees par type (utile si le front veut filtrer cote backend)
    @Query("""
        SELECT new com.smartcampus.backend.dto.parking.ParkingPositionDTO(
            p.id, p.ident, p.nom, p.taType, p.latitude, p.longitude)
        FROM Parking p
        WHERE p.taType = :taType
        """)
    List<ParkingPositionDTO> findPositionsByTaType(String taType);

    // Comptage groupe par type - 1 seule requete SQL, pas de boucle Java
    @Query("SELECT p.taType, COUNT(p) FROM Parking p GROUP BY p.taType")
    List<Object[]> countGroupedByTaType();
}