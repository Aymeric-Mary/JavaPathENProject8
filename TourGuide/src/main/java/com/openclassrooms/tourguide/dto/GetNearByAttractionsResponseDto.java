package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetNearByAttractionsResponseDto {
    private Location userLocation;
    private List<AttractionDto> attractions;
}
