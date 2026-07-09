package com.smartcampus.backend.service.parking;

import com.smartcampus.backend.dto.parking.ParkingDynamicDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache memoire simple, cle = ident du parking.
 * Suffisant en mono-instance. Si l'appli scale horizontalement,
 * remplacer par Redis pour que toutes les instances partagent
 * la meme donnee fraiche.
 */
@Component
public class ParkingRealtimeCache {

    private final Map<String, ParkingDynamicDTO> cache = new ConcurrentHashMap<>();

    public void put(ParkingDynamicDTO dto) {
        cache.put(dto.getIdent(), dto);
    }

    public ParkingDynamicDTO get(String ident) {
        return cache.get(ident);
    }

    public Map<String, ParkingDynamicDTO> getAll() {
        return cache;
    }
}