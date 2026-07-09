package com.smartcampus.backend.service.parking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.parking.ParkingDynamicDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingRealtimeService {

    private final ParkingRealtimeCache realtimeCache;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL =
        "https://opendata.bordeaux-metropole.fr/api/explore/v2.1/catalog/datasets/st_park_p/records";

    @Scheduled(fixedRate = 150_000) // 2 min 30
    public void refreshDynamicData() {
        int updated = 0;
        int offset = 0;
        int totalCount = 0;

        try {
            do {
                String url = BASE_URL + "?limit=100&offset=" + offset;
                String rawJson = restTemplate.getForObject(url, String.class);
                JsonNode root = objectMapper.readTree(rawJson);
                totalCount = root.path("total_count").asInt(0);

                for (JsonNode record : root.path("results")) {
                    String ident = record.path("ident").asText(null);
                    if (ident == null) continue;

                    realtimeCache.put(buildDynamicDTO(record, ident));
                    updated++;
                }
                offset += 100;
            } while (offset < totalCount);

            log.info("Cache temps réel parking rafraîchi : {} entrées", updated);
        } catch (Exception e) {
            log.error("Erreur rafraîchissement temps réel parking global", e);
        }
    }

    public ParkingDynamicDTO fetchLive(String ident) {
        String url = BASE_URL + "?where=ident=\"" + ident + "\"&limit=1";
        try {
            String rawJson = restTemplate.getForObject(url, String.class);
            JsonNode results = objectMapper.readTree(rawJson).path("results");
            if (results.isEmpty()) return null;

            JsonNode record = results.get(0);
            ParkingDynamicDTO dto = buildDynamicDTO(record, ident);
            realtimeCache.put(dto);
            return dto;
        } catch (Exception e) {
            log.error("Erreur fetch live direct pour le parking ident={}", ident, e);
            return null;
        }
    }

    private ParkingDynamicDTO buildDynamicDTO(JsonNode record, String ident) {
        return ParkingDynamicDTO.builder()
                .ident(ident)
                .etat(record.path("etat").asText(null))
                .libre(record.hasNonNull("libres") ? record.get("libres").asInt() : null)
                .totalTempsReel(record.hasNonNull("total") ? record.get("total").asInt() : null)
                .connecte(record.hasNonNull("connecte") && record.get("connecte").asInt() == 1)
                .mdate(parseMdate(record.path("mdate").asText(null)))
                .fetchedAt(OffsetDateTime.now())
                .build();
    }

    private OffsetDateTime parseMdate(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        try {
            return OffsetDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("mdate illisible : {}", raw);
            return null;
        }
    }
}