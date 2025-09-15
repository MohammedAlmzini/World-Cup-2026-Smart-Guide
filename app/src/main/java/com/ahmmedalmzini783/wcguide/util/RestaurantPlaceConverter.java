package com.ahmmedalmzini783.wcguide.util;

import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantPlaceConverter {

    public static List<Place> convertRestaurantsToPlaces(List<Restaurant> restaurants) {
        List<Place> places = new ArrayList<>();
        
        if (restaurants != null) {
            for (Restaurant restaurant : restaurants) {
                Place place = convertRestaurantToPlace(restaurant);
                if (place != null) {
                    places.add(place);
                }
            }
        }
        
        return places;
    }

    public static Place convertRestaurantToPlace(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }

        Place place = new Place();
        
        // Basic information
        place.setId(restaurant.getId());
        place.setName(restaurant.getName());
        place.setDescription(restaurant.getDescription());
        place.setKind("restaurant");
        
        // Location information
        place.setCountry(restaurant.getCountry());
        place.setCity(restaurant.getCity());
        place.setLat(restaurant.getLat());
        place.setLng(restaurant.getLng());
        place.setAddress(restaurant.getAddress());
        
        // Rating information
        place.setAvgRating(restaurant.getRating());
        
        // Convert price range string to price level int
        int priceLevel = 2; // Default to $$
        String priceRange = restaurant.getPriceRange();
        if (priceRange != null) {
            switch (priceRange) {
                case "$": priceLevel = 1; break;
                case "$$": priceLevel = 2; break;
                case "$$$": priceLevel = 3; break;
                case "$$$$": priceLevel = 4; break;
            }
        }
        place.setPriceLevel(priceLevel);
        
        // Images
        List<String> images = new ArrayList<>();
        if (restaurant.getMainImageUrl() != null && !restaurant.getMainImageUrl().isEmpty()) {
            images.add(restaurant.getMainImageUrl());
        }
        place.setImages(images);
        
        // Amenities (use facilities)
        place.setAmenities(restaurant.getFacilities());
        
        // Additional information specific to restaurants
        // We can use the description field to include cuisine type and price range
        StringBuilder enhancedDescription = new StringBuilder();
        if (restaurant.getDescription() != null) {
            enhancedDescription.append(restaurant.getDescription());
        }
        
        if (restaurant.getCuisineType() != null) {
            enhancedDescription.append("\n\nنوع المطبخ: ").append(restaurant.getCuisineType());
        }
        
        if (restaurant.getPriceRange() != null) {
            enhancedDescription.append("\nنطاق الأسعار: ").append(restaurant.getPriceRange());
        }
        
        if (restaurant.isHasDelivery()) {
            enhancedDescription.append("\n✓ خدمة التوصيل متوفرة");
        }
        
        if (restaurant.isHasReservation()) {
            enhancedDescription.append("\n✓ قبول الحجوزات");
        }
        
        if (restaurant.getCapacity() > 0) {
            enhancedDescription.append("\nالسعة: ").append(restaurant.getCapacity()).append(" شخص");
        }
        
        place.setDescription(enhancedDescription.toString());
        
        return place;
    }

    public static Restaurant convertPlaceToRestaurant(Place place) {
        if (place == null || !"restaurant".equals(place.getKind())) {
            return null;
        }

        Restaurant restaurant = new Restaurant();
        
        // Basic information
        restaurant.setId(place.getId());
        restaurant.setName(place.getName());
        restaurant.setDescription(place.getDescription());
        
        // Location information
        restaurant.setCountry(place.getCountry());
        restaurant.setCity(place.getCity());
        restaurant.setLat(place.getLat());
        restaurant.setLng(place.getLng());
        restaurant.setAddress(place.getAddress());
        
        // Rating information
        restaurant.setRating(place.getAvgRating());
        
        // Convert price level int to price range string
        String priceRange = "$$"; // Default
        int priceLevel = place.getPriceLevel();
        switch (priceLevel) {
            case 1: priceRange = "$"; break;
            case 2: priceRange = "$$"; break;
            case 3: priceRange = "$$$"; break;
            case 4: priceRange = "$$$$"; break;
        }
        restaurant.setPriceRange(priceRange);
        
        // Images
        if (place.getImages() != null && !place.getImages().isEmpty()) {
            restaurant.setMainImageUrl(place.getImages().get(0));
        }
        
        // Facilities
        restaurant.setFacilities(place.getAmenities());
        
        // Default values for restaurant-specific fields
        restaurant.setCuisineType("عام");
        restaurant.setHasDelivery(false);
        restaurant.setHasReservation(false);
        restaurant.setCapacity(0);
        
        return restaurant;
    }
}
