package com.smartcampus.backend.mapper.station;

import com.smartcampus.backend.dto.station.StationVDTO;
import com.smartcampus.backend.entity.station.StationV;
import org.springframework.stereotype.Component;

@Component
public class StationVMapper {

    public StationVDTO toDTO(StationV station) {
        return StationVDTO.builder()
                .id(station.getId())
                .stationId(station.getStationId())
                .nom(station.getNom())
                .adresse(station.getAdresse())
                .capacite(station.getCapacite())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .build();
    }
}