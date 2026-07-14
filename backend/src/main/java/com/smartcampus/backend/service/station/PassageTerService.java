package com.smartcampus.backend.service.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.station.PassageTerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Partie DYNAMIQUE : prochains passages en gare (temps reel Navitia/SNCF).
 * JAMAIS persiste en base (donnee volatile par nature).
 *
 * ATTENTION QUOTA : le token SNCF free-tier est limite a 5000 requetes/mois
 * (~166/jour). Un cache par gare avec TTL est donc INDISPENSABLE, pas juste
 * une optimisation. Sans cache, quelques dizaines d'utilisateurs simultanes
 * epuisent le quota en quelques heures.
 */
@Slf4j
@Service
public class PassageTerService {

    @Qualifier("navitiaRestTemplate")
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${navitia.base-url}")
    private String navitiaBaseUrl;

    @Value("${navitia.cache-ttl-seconds:60}")
    private long cacheTtlSeconds;

    private static final DateTimeFormatter NAVITIA_DATETIME =
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private static final String DEPARTURES_TEMPLATE =
        "{baseUrl}/stop_areas/{stopId}/departures?count=10";

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    //const
    public PassageTerService(@Qualifier("navitiaRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private record CacheEntry(List<PassageTerDTO> passages, long expiresAtEpochMs) {
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAtEpochMs;
        }
    }

    public List<PassageTerDTO> getNextPassages(String navitiaStopId) {
        CacheEntry entry = cache.get(navitiaStopId);
        if (entry != null && !entry.isExpired()) {
            return entry.passages();
        }

        ReentrantLock lock = locks.computeIfAbsent(navitiaStopId, k -> new ReentrantLock());
        lock.lock();
        try {
            entry = cache.get(navitiaStopId);
            if (entry != null && !entry.isExpired()) {
                return entry.passages(); // un autre thread a deja rafraichi entre-temps
            }

            List<PassageTerDTO> fresh = fetchFromApi(navitiaStopId);
            cache.put(navitiaStopId, new CacheEntry(fresh,
                    System.currentTimeMillis() + cacheTtlSeconds * 1000));
            return fresh;

        } finally {
            lock.unlock();
        }
    }

    /** Utilise par le scheduler pour prechauffer le cache sans dupliquer la logique de fetch. */
    public void refreshCache(String navitiaStopId) {
        List<PassageTerDTO> fresh = fetchFromApi(navitiaStopId);
        cache.put(navitiaStopId, new CacheEntry(fresh,
                System.currentTimeMillis() + cacheTtlSeconds * 1000));
    }

    private List<PassageTerDTO> fetchFromApi(String navitiaStopId) {
        String url = UriComponentsBuilder.fromUriString(DEPARTURES_TEMPLATE)
                .buildAndExpand(navitiaBaseUrl, navitiaStopId)
                .toUriString();

        List<PassageTerDTO> passages = new ArrayList<>();
        try {
            String rawJson = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(rawJson);

            for (JsonNode dep : root.path("departures")) {
                JsonNode info = dep.path("display_informations");
                JsonNode stopDateTime = dep.path("stop_date_time");

                String aimed = stopDateTime.path("base_departure_date_time").asText(null);
                String expected = stopDateTime.path("departure_date_time").asText(null);
                boolean tempsReel = "realtime".equals(stopDateTime.path("data_freshness").asText(null));

                Long retardSecondes = null;
                if (tempsReel && aimed != null && expected != null) {
                    retardSecondes = computeDelaySeconds(aimed, expected);
                }

                passages.add(PassageTerDTO.builder()
                        .ligne(info.path("code").asText(null))
                        .modeCommercial(info.path("commercial_mode").asText(null))
                        .direction(info.path("direction").asText(null))
                        .destination(info.path("headsign").asText(null))
                        .heureTheorique(aimed)
                        .heurePrevue(expected)
                        .retardSecondes(retardSecondes)
                        .tempsReel(tempsReel)
                        .build());
            }
        } catch (Exception e) {
            log.error("Erreur recuperation passages pour {}", navitiaStopId, e);
            throw new RuntimeException("Erreur recuperation passages pour " + navitiaStopId, e);
        }

        log.info("Passages rafraichis pour {} : {} resultats", navitiaStopId, passages.size());
        return passages;
    }

    private Long computeDelaySeconds(String aimed, String expected) {
        try {
            LocalDateTime aimedDt = LocalDateTime.parse(aimed, NAVITIA_DATETIME);
            LocalDateTime expectedDt = LocalDateTime.parse(expected, NAVITIA_DATETIME);
            return Duration.between(aimedDt, expectedDt).getSeconds();
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}