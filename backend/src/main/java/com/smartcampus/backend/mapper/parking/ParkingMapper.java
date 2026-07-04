package com.smartcampus.backend.mapper.parking;

import com.smartcampus.backend.dto.parking.ParkingDTO;
import com.smartcampus.backend.entity.parking.Parking;
import org.springframework.stereotype.Component;

@Component
public class ParkingMapper {

    public ParkingDTO toDTO(Parking parking) {
        return ParkingDTO.builder()
                .id(parking.getId())
                .ident(parking.getIdent())
                .nom(parking.getNom())
                .adresse(parking.getAdresse())
                .exploit(parking.getExploit())
                .taType(parking.getTaType())
                .type(parking.getType())
                .latitude(parking.getLatitude())
                .longitude(parking.getLongitude())
                .npTotal(parking.getNpTotal())
                .npPmr(parking.getNpPmr())
                .npVle(parking.getNpVle())
                .url(parking.getUrl())
                .build();
    }
}