package com.smartcampus.backend.mapper.station;

import com.smartcampus.backend.dto.station.StationTerDTO;
import com.smartcampus.backend.entity.station.StationTer;
import org.springframework.stereotype.Component;

@Component
public class StationTerMapper {

    public StationTerDTO toDTO(StationTer station) {
        return StationTerDTO.builder()
                .id(station.getId())
                .navitiaId(station.getNavitiaId())
                .nom(station.getNom())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .distanceCentreMetres(station.getDistanceCentreMetres())
                .build();
    }
}