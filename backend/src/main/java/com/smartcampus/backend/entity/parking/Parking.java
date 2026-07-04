package com.smartcampus.backend.entity.parking;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "parking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ident;      // ex: CUBPK92

    private String nom;
    private String adresse;
    private String exploit;    // gestionnaire, ex: METPARK

    private String taType;     // PAYANT_TARIF_HORAIRE / PAYANT_TARIF_PARC_RELAIS / ...
    private String type;       // SURFACE / SILO / ENTERRE / MIXTE

    private Double latitude;
    private Double longitude;

    // PostGIS - pour les futures requetes spatiales (ST_DWithin, ST_Contains...)
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    private Integer npTotal;   // capacite totale declaree
    private Integer npPmr;     // places handicapees
    private Integer npVle;     // places velo

    private String url;
}