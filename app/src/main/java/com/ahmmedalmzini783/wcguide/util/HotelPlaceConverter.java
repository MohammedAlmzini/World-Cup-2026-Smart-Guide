package com.ahmmedalmzini783.wcguide.util;

import com.ahmmedalmzini783.wcguide.data.model.Hotel;
import com.ahmmedalmzini783.wcguide.data.model.Place;

import java.util.ArrayList;
import java.util.List;

public class HotelPlaceConverter {
    
    /**
     * Convert Hotel to Place for backward compatibility
     */
    public static Place convertHotelToPlace(Hotel hotel) {
        if (hotel == null) return null;
        
        return new Place(
            hotel.getId(),
            "hotel", // kind
            hotel.getName(),
            hotel.getCountry(),
            hotel.getCity(),
            hotel.getAddress() != null ? hotel.getAddress() : hotel.getLocation(),
            hotel.getLat(),
            hotel.getLng(),
            createImagesList(hotel),
            hotel.getRating(),
            0, // ratingCount - can be calculated if needed
            calculatePriceLevel(hotel),
            hotel.getDescription(),
            createAmenitiesList(hotel)
        );
    }
    
    /**
     * Convert list of Hotels to list of Places
     */
    public static List<Place> convertHotelsToPlaces(List<Hotel> hotels) {
        if (hotels == null) return new ArrayList<>();
        
        List<Place> places = new ArrayList<>();
        for (Hotel hotel : hotels) {
            Place place = convertHotelToPlace(hotel);
            if (place != null) {
                places.add(place);
            }
        }
        return places;
    }
    
    /**
     * Create images list from hotel data
     */
    private static List<String> createImagesList(Hotel hotel) {
        List<String> images = new ArrayList<>();
        
        // Add main image
        if (hotel.getMainImageUrl() != null && !hotel.getMainImageUrl().trim().isEmpty()) {
            images.add(hotel.getMainImageUrl());
        }
        
        // Add additional images
        if (hotel.getAdditionalImages() != null) {
            for (Hotel.AdditionalImage additionalImage : hotel.getAdditionalImages()) {
                if (additionalImage.getImageUrl() != null && !additionalImage.getImageUrl().trim().isEmpty()) {
                    images.add(additionalImage.getImageUrl());
                }
            }
        }
        
        return images;
    }
    
    /**
     * Create amenities list from hotel facilities and services
     */
    private static List<String> createAmenitiesList(Hotel hotel) {
        List<String> amenities = new ArrayList<>();
        
        // Add services
        if (hotel.getServices() != null) {
            amenities.addAll(hotel.getServices());
        }
        
        // Add facilities
        if (hotel.getFacilities() != null) {
            amenities.addAll(hotel.getFacilities());
        }
        
        // Add world cup services
        if (hotel.getWorldCupServices() != null) {
            amenities.addAll(hotel.getWorldCupServices());
        }
        
        return amenities;
    }
    
    /**
     * Calculate price level based on room prices
     * 1 = $ (budget), 2 = $$ (moderate), 3 = $$$ (expensive), 4 = $$$$ (luxury)
     */
    private static int calculatePriceLevel(Hotel hotel) {
        if (hotel.getRooms() == null || hotel.getRooms().isEmpty()) {
            return 2; // Default to moderate
        }
        
        double averagePrice = 0;
        int roomCount = 0;
        
        for (Hotel.HotelRoom room : hotel.getRooms()) {
            if (room.getPricePerNight() > 0) {
                averagePrice += room.getPricePerNight();
                roomCount++;
            }
        }
        
        if (roomCount == 0) return 2;
        
        averagePrice = averagePrice / roomCount;
        
        // Price categorization (adjust based on your currency and market)
        if (averagePrice < 100) return 1;
        else if (averagePrice < 300) return 2;
        else if (averagePrice < 600) return 3;
        else return 4;
    }
}
