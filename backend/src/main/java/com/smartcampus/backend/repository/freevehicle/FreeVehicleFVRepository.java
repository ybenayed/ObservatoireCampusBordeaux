package com.smartcampus.backend.repository.freevehicle;

import com.smartcampus.backend.entity.freevehicle.FreeVehicleFV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface FreeVehicleFVRepository extends JpaRepository<FreeVehicleFV, Long> {
    Optional<FreeVehicleFV> findByBikeId(String bikeId);
    boolean existsByBikeId(String bikeId);
    List<FreeVehicleFV> findByVehicleTypeId(String vehicleTypeId);
    @Query("""
        SELECT v.vehicleTypeId, COUNT(v)
        FROM FreeVehicleFV v
        GROUP BY v.vehicleTypeId
        """)
    List<Object[]> countVehiclesGroupedByType();
}