package com.smartcampus.backend.repository.freevehicle;

import com.smartcampus.backend.entity.freevehicle.VehicleTypeFV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleTypeFVRepository extends JpaRepository<VehicleTypeFV, Long> {
    Optional<VehicleTypeFV> findByVehicleTypeId(String vehicleTypeId);
    boolean existsByVehicleTypeId(String vehicleTypeId);
}