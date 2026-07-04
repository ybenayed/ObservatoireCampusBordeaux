package com.smartcampus.backend.service.parking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.parking.ParkingDTO;
import com.smartcampus.backend.dto.parking.ParkingCountDTO;
import com.smartcampus.backend.dto.parking.ParkingPositionDTO;  
import com.smartcampus.backend.entity.parking.Parking;
import com.smartcampus.backend.mapper.parking.ParkingMapper;
import com.smartcampus.backend.repository.parking.ParkingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private final ParkingMapper parkingMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private static final String BASE_URL =
        "https://opendata.bordeaux-metropole.fr/api/explore/v2.1/catalog/datasets/st_park_p/records";

    // ??? LECTURE

    public List<ParkingDTO> getAllParking() {
        return parkingRepository.findAll().stream().map(parkingMapper::toDTO).toList();
    }

    public ParkingDTO getParkingById(Long id) {
        return parkingRepository.findById(id)
                .map(parkingMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Parking introuvable : " + id));
    }

    // ??? IMPORT DEPUIS L'API BORDEAUX METROPOLE (idempotent, comme CampusService)

    public int importParkingFromApi() {
        int imported = 0;
        int offset = 0;
        int totalCount;

        do {
            String url = BASE_URL + "?limit=100&offset=" + offset;
            String rawJson = restTemplate.getForObject(url, String.class);

            try {
                JsonNode root = objectMapper.readTree(rawJson);
                totalCount = root.path("total_count").asInt(0);

                for (JsonNode record : root.path("results")) {
                    String ident = record.path("ident").asText(null);
                    if (ident == null || parkingRepository.existsByIdent(ident)) {
                        continue; // idempotent : on ne reimporte pas un parking deja connu
                    }

                    Parking parking = buildParkingFromRecord(record);
                    parkingRepository.save(parking);
                    imported++;
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur import parking offset=" + offset, e);
            }

            offset += 100;
        } while (offset < totalCount);

        log.info("Import parking termine : {} nouveaux parkings", imported);
        return imported;
    }

    private Parking buildParkingFromRecord(JsonNode record) {
        JsonNode geo = record.path("geo_point_2d");
        Double lat = geo.hasNonNull("lat") ? geo.get("lat").asDouble() : null;
        Double lon = geo.hasNonNull("lon") ? geo.get("lon").asDouble() : null;

        Point location = null;
        if (lat != null && lon != null) {
            location = geometryFactory.createPoint(new Coordinate(lon, lat));
        }

        return Parking.builder()
                .ident(record.path("ident").asText(null))
                .nom(record.path("nom").asText(null))
                .adresse(record.path("adresse").asText(null))
                .exploit(record.path("exploit").asText(null))
                .taType(record.path("ta_type").asText(null))
                .type(record.path("type").asText(null))
                .latitude(lat)
                .longitude(lon)
                .location(location)
                .npTotal(record.hasNonNull("np_total") ? record.get("np_total").asInt() : null)
                .npPmr(record.hasNonNull("np_pmr") ? record.get("np_pmr").asInt() : null)
                .npVle(record.hasNonNull("np_vle") ? record.get("np_vle").asInt() : null)
                .url(record.path("url").asText(null))
                .build();
    }

        // ??? POSITIONS POUR LA CARTE

    public List<ParkingPositionDTO> getAllPositions() {
        return parkingRepository.findAllPositions();
    }

    public List<ParkingPositionDTO> getPositionsByType(String taType) {
        return parkingRepository.findPositionsByTaType(taType);
    }

    // ??? COMPTAGE PAR TYPE

    public List<ParkingCountDTO> getCountByType() {
        return parkingRepository.countGroupedByTaType().stream()
                .map(row -> new ParkingCountDTO((String) row[0], (Long) row[1]))
                .toList();
    }
}