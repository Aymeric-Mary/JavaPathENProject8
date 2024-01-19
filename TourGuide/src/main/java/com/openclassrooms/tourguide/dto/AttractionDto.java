package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AttractionDto {
    private String attractionName;
    private Location location;
    private long userDistance;
    private Integer rewardPoints;
}
