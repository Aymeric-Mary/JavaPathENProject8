package com.openclassrooms.tourguide.mapper;

import com.openclassrooms.tourguide.dto.AttractionDto;
import com.openclassrooms.tourguide.dto.GetNearByAttractionsResponseDto;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AttractionMapper {

    GetNearByAttractionsResponseDto toGetNearByAttractionsResponseDto(Location userLocation, List<Attraction> attractions);

    @Mapping(target = "userDistance", ignore = true)
    @Mapping(target = "rewardPoints", ignore = true)
    @Mapping(target = "location", ignore = true)
    AttractionDto toAttractionDto(Attraction attraction);


}
