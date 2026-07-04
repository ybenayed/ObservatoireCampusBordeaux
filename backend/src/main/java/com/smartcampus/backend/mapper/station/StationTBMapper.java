package com.smartcampus.backend.mapper.station;

import com.smartcampus.backend.dto.station.StationTBDTO;
import com.smartcampus.backend.entity.station.StationTB;
import org.springframework.stereotype.Component;

@Component
public class StationTBMapper {

    public StationTBDTO toDTO(StationTB station) {
        return StationTBDTO.builder()
                .id(station.getId())
                .stopId(station.getStopId())
                .nom(station.getNom())
                .stopAreaRef(station.getStopAreaRef())
                .mode(station.getMode())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .lines(station.getLines())
                .build();
    }
}