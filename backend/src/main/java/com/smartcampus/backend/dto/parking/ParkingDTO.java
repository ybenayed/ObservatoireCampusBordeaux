package com.smartcampus.backend.dto.parking;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingDTO {
    private Long id;
    private String ident;
    private String nom;
    private String adresse;
    private String exploit;
    private String taType;
    private String type;
    private Double latitude;
    private Double longitude;
    private Integer npTotal;
    private Integer npPmr;
    private Integer npVle;
    private String url;
}