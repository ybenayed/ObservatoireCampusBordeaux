package com.smartcampus.backend.service.search;

import tools.jackson.databind.JsonNode;
import com.smartcampus.backend.dto.search.SearchResultDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeocodingService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private static final String USER_AGENT = "ObservatoireCampusApp/1.0 (contact@votre-domaine.fr)";

    private final RestTemplate restTemplate = new RestTemplate();

    public List<SearchResultDTO> search(String query) {
        URI uri = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                .queryParam("q", query)
                .queryParam("format", "json")
                .queryParam("limit", 6)
                .queryParam("addressdetails", 0)
                .queryParam("accept-language", "fr")
                .queryParam("viewbox", "-0.75,44.90,-0.45,44.70")
                .queryParam("bounded", 0)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode[]> response;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }

        JsonNode[] body = response.getBody();
        if (body == null) {
            return List.of();
        }

        List<SearchResultDTO> results = new ArrayList<>();
        for (JsonNode node : body) {
            String displayName = node.path("display_name").asText(null);
            Double lat = node.hasNonNull("lat") ? node.path("lat").asDouble() : null;
            Double lon = node.hasNonNull("lon") ? node.path("lon").asDouble() : null;

            if (displayName != null && !displayName.isBlank() && lat != null && lon != null) {
                String[] parts = splitDisplayName(displayName);
                results.add(new SearchResultDTO(parts[0], parts[1], lat, lon));
            }
        }
        return results;
    }

    private String[] splitDisplayName(String displayName) {
        List<String> parts = new ArrayList<>();
        for (String p : displayName.split(",")) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) {
                parts.add(trimmed);
            }
        }
        if (parts.isEmpty()) {
            return new String[]{displayName, ""};
        }
        String title = parts.get(0);
        String subtitle = String.join(", ", parts.subList(1, Math.min(parts.size(), 3)));
        return new String[]{title, subtitle};
    }
}