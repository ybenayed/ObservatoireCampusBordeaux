package com.smartcampus.backend.service.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.station.PassageTBDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassageTBService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String MONITORING_URL =
        "https://bdx.mecatran.com/utw/ws/siri/2.0/bordeaux/stop-monitoring.json" +
        "?AccountKey=opendata-bordeaux-metropole-flux-gtfs-rt&MonitoringRef={stopId}";

    // Meme API pour bus et tram - seul le stopId change
    public List<PassageTBDTO> getNextPassages(String stopId) {
        String url = UriComponentsBuilder.fromUriString(MONITORING_URL)
            .buildAndExpand(stopId)
            .toUriString();

        String rawJson = restTemplate.getForObject(url, String.class);
        List<PassageTBDTO> passages = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode deliveries = root.path("Siri").path("ServiceDelivery").path("StopMonitoringDelivery");
            if (!deliveries.isArray() || deliveries.isEmpty()) return passages;

            for (JsonNode visit : deliveries.get(0).path("MonitoredStopVisit")) {
                JsonNode journey = visit.path("MonitoredVehicleJourney");
                JsonNode call = journey.path("MonitoredCall");

                String aimed = call.path("AimedArrivalTime").asText(null);
                String expected = call.path("ExpectedArrivalTime").asText(null);

                Long retardSecondes = null;
                if (aimed != null && expected != null) {
                    retardSecondes = Duration.between(Instant.parse(aimed), Instant.parse(expected)).getSeconds();
                }

                passages.add(PassageTBDTO.builder()
                        .ligne(journey.path("LineRef").path("value").asText(null))
                        .direction(firstValue(journey.path("DirectionName")))
                        .destination(firstValue(journey.path("DestinationName")))
                        .heureTheorique(aimed)
                        .heurePrevue(expected)
                        .retardSecondes(retardSecondes)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur recuperation passages pour " + stopId, e);
        }

        return passages;
    }

    private String firstValue(JsonNode arrayNode) {
        if (arrayNode.isArray() && !arrayNode.isEmpty()) {
            return arrayNode.get(0).path("value").asText(null);
        }
        return null;
    }
}