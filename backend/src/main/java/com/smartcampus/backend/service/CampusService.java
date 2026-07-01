package com.smartcampus.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.CampusDTO;
import com.smartcampus.backend.entity.Campus;
import com.smartcampus.backend.repository.CampusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampusService {

    private final CampusRepository campusRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // ─── LECTURE 

    public List<CampusDTO> getAllCampus() {
        return campusRepository.findAll().stream().map(this::toDTO).toList();
    }

    public CampusDTO getCampusById(Long id) {
        return campusRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Campus introuvable : " + id));
    }

    // ─── IMPORT DEPUIS FICHIER LOCAL

    public CampusDTO importCampusFromLocalFile() {
        String resourcePath = "data/campus-perimeter.json";

        return campusRepository.findByName("Campus Bordeaux")
                .map(this::toDTO)
                .orElseGet(() -> {
                    try (InputStream is = new ClassPathResource(resourcePath).getInputStream()) {
                        JsonNode root = objectMapper.readTree(is);
                        String name = root.path("name").asText();
                        String city = root.path("city").asText();
                        JsonNode coordsNode = root.path("coordinates");

                        List<Coordinate> coords = new ArrayList<>();
                        for (JsonNode pt : coordsNode) {
                            double lon = pt.get(0).asDouble();
                            double lat = pt.get(1).asDouble();
                            coords.add(new Coordinate(lon, lat));
                        }

                        if (!coords.get(0).equals2D(coords.get(coords.size() - 1))) {
                            coords.add(new Coordinate(coords.get(0)));
                        }

                        LinearRing ring = geometryFactory.createLinearRing(coords.toArray(new Coordinate[0]));
                        Polygon polygon = geometryFactory.createPolygon(ring);

                        if (!polygon.isValid()) {
                            log.warn("Polygone local invalide, correction buffer(0)");
                            polygon = (Polygon) polygon.buffer(0);
                        }

                        Point centroid = polygon.getCentroid();

                        Campus campus = new Campus();
                        campus.setName(name);
                        campus.setCity(city);
                        campus.setCenterLat(centroid.getY());
                        campus.setCenterLng(centroid.getX());
                        campus.setPolygon(polygon);
                        campus.setPerimeterMeters(calculatePerimeterMeters(polygon.getExteriorRing().getCoordinates()));
                        campus.setImportedAt(LocalDateTime.now());

                        log.info("Sauvegarde campus local : {} ({} points)", name, polygon.getNumPoints());
                        return toDTO(campusRepository.save(campus));

                    } catch (Exception e) {
                        throw new RuntimeException("Erreur import fichier local campus-perimeter.json", e);
                    }
                });
    }

    // ─── PÉRIMÈTRE 

    private double calculatePerimeterMeters(Coordinate[] coords) {
        double total = 0;
        for (int i = 0; i < coords.length - 1; i++) {
            total += haversine(coords[i].getY(), coords[i].getX(),
                               coords[i + 1].getY(), coords[i + 1].getX());
        }
        return total;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // ─── DTO 

    private CampusDTO toDTO(Campus campus) {
        List<double[]> polygonCoords = new ArrayList<>();
        if (campus.getPolygon() != null) {
            for (Coordinate c : campus.getPolygon().getCoordinates()) {
                polygonCoords.add(new double[]{c.getX(), c.getY()});
            }
        }
        return CampusDTO.builder()
                .id(campus.getId())
                .name(campus.getName())
                .city(campus.getCity())
                .centerLat(campus.getCenterLat())
                .centerLng(campus.getCenterLng())
                .perimeterMeters(campus.getPerimeterMeters())
                .polygonCoordinates(polygonCoords)
                .importedAt(campus.getImportedAt())
                .build();
    }
}