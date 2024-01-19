package com.openclassrooms.tourguide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.openclassrooms.tourguide.dto.AttractionDto;
import com.openclassrooms.tourguide.dto.GetNearByAttractionsResponseDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.mockito.ArgumentCaptor;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import tripPricer.Provider;

public class TestTourGuideService {

    @Test
    public void getUserLocation() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();
        assertTrue(visitedLocation.userId.equals(user.getUserId()));
    }

    @Test
    public void addUser() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrivedUser = tourGuideService.getUser(user.getUserName());
        User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

        tourGuideService.tracker.stopTracking();

        assertEquals(user, retrivedUser);
        assertEquals(user2, retrivedUser2);
    }

    @Test
    public void getAllUsers() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();

        tourGuideService.tracker.stopTracking();

        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void trackUser() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        tourGuideService.tracker.stopTracking();

        assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Test
    public void getNearbyAttractions() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);

        tourGuideService.tracker.stopTracking();

        assertEquals(5, attractions.size());
    }

    @Test
    public void getNearbyAttractionsResponseDto() {
        GpsUtil gpsUtil = new GpsUtil();
        InternalTestHelper.setInternalUserNumber(0);
        RewardsService rewardsServiceMock = mock(RewardsService.class);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsServiceMock);
        ArgumentCaptor<Attraction> attractionCaptor = ArgumentCaptor.forClass(Attraction.class);
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction userLocation = new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D);
        Attraction attraction1 = new Attraction("San Diego Zoo", "San Diego", "CA", 32.735317, -117.149048);
        Attraction attraction2 = new Attraction("Joshua Tree National Park", "Joshua Tree National Park", "CA", 33.881866, -115.90065);
        Attraction attraction3 = new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689, -115.510399);
        Attraction attraction4 = new Attraction("Kartchner Caverns State Park", "Benson", "AZ", 31.837551, -110.347382);
        when(rewardsServiceMock.getRewardPoints(attractionCaptor.capture(), eq(user)))
                .thenReturn(100)
                .thenReturn(200)
                .thenReturn(300)
                .thenReturn(400)
                .thenReturn(500);
        when(rewardsServiceMock.getDistance(any(), any())).thenCallRealMethod();

        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), userLocation, Date.from(Instant.now()));

        GetNearByAttractionsResponseDto responseDto = tourGuideService.getNearByAttractionsResponseDto(visitedLocation, user);

        GetNearByAttractionsResponseDto expectedResponseDto = GetNearByAttractionsResponseDto.builder()
                .userLocation(userLocation)
                .attractions(List.of(
                        AttractionDto.builder()
                                .attractionName("Disneyland")
                                .location(userLocation)
                                .userDistance(0)
                                .rewardPoints(100)
                                .build(),
                        AttractionDto.builder()
                                .attractionName("San Diego Zoo")
                                .location(attraction1)
                                .userDistance(87)
                                .rewardPoints(200)
                                .build(),
                        AttractionDto.builder()
                                .attractionName("Joshua Tree National Park")
                                .location(attraction2)
                                .userDistance(116)
                                .rewardPoints(300)
                                .build(),
                        AttractionDto.builder()
                                .attractionName("Mojave National Preserve")
                                .location(attraction3)
                                .userDistance(165)
                                .rewardPoints(400)
                                .build(),
                        AttractionDto.builder()
                                .attractionName("Kartchner Caverns State Park")
                                .location(attraction4)
                                .userDistance(460)
                                .rewardPoints(500)
                                .build()
                ))
                .build();

        tourGuideService.tracker.stopTracking();

        assertThat(responseDto).usingRecursiveComparison().isEqualTo(expectedResponseDto);
    }

    @Disabled
    @Test
    public void getTripDeals() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Provider> providers = tourGuideService.getTripDeals(user);

        tourGuideService.tracker.stopTracking();

        assertEquals(10, providers.size());
    }

}
