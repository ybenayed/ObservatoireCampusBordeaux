package com.smartcampus.backend.dto.parking;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingCountDTO {
    private String taType;
    private Long count;
}