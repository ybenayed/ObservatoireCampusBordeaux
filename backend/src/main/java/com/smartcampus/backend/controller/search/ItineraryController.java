package com.smartcampus.backend.controller.search;

import com.smartcampus.backend.dto.search.ItineraryRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // <-- 1. Ajout de l'URL racine /api
public class ItineraryController {

    private static final Logger log = LoggerFactory.getLogger(ItineraryController.class);

    @PostMapping("/itinerary") // <-- 2. Ajout du / au début
    public ResponseEntity<Void> receiveItinerary(@RequestBody ItineraryRequestDTO request) {
        log.info("=== Nouvel itinéraire reçu ===");
        log.info("Origine      : {} | lat={} lon={}",
                request.getOrigin().getName(),
                request.getOrigin().getLatitude(),
                request.getOrigin().getLongitude());
        log.info("Destination  : {} | lat={} lon={}",
                request.getDestination().getName(),
                request.getDestination().getLatitude(),
                request.getDestination().getLongitude());

        return ResponseEntity.ok().build();
    }
}