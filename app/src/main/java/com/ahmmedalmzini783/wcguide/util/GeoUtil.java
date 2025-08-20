package com.ahmmedalmzini783.wcguide.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class GeoUtil {

    /**
     * Calculate distance between two points using Haversine formula
     * @param lat1 Latitude of first point
     * @param lng1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lng2 Longitude of second point
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS = 6371; // Earth radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Format distance to readable string
     */
    public static String formatDistance(Context context, double distanceKm) {
        if (distanceKm < 1.0) {
            int meters = (int) (distanceKm * 1000);
            return context.getString(com.ahmmedalmzini783.wcguide.R.string.distance_meters, meters);
        } else if (distanceKm < 10.0) {
            return context.getString(com.ahmmedalmzini783.wcguide.R.string.distance_km_decimal, distanceKm);
        } else {
            int km = (int) Math.round(distanceKm);
            return context.getString(com.ahmmedalmzini783.wcguide.R.string.distance_km, km);
        }
    }

    /**
     * Open Google Maps with directions to a location
     */
    public static void openMapsDirections(Context context, double lat, double lng, String label) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng + "(" + label + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            // Fallback to web browser
            openMapsInBrowser(context, lat, lng, label);
        }
    }

    /**
     * Open Google Maps in web browser
     */
    public static void openMapsInBrowser(Context context, double lat, double lng, String label) {
        String url = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng + "&query_place_id=" + label;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * Open Google Maps with a search query
     */
    public static void openMapsSearch(Context context, String query) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            // Fallback to web browser
            String url = "https://www.google.com/maps/search/" + Uri.encode(query);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }

    /**
     * Check if coordinates are valid
     */
    public static boolean isValidCoordinate(double lat, double lng) {
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }

    /**
     * Get country bounds for filtering
     */
    public static class CountryBounds {
        public static final double[] US_BOUNDS = {24.396308, -125.0, 49.384358, -66.93457}; // minLat, minLng, maxLat, maxLng
        public static final double[] CA_BOUNDS = {41.6751, -141.0, 83.23324, -52.6480987209};
        public static final double[] MX_BOUNDS = {14.5388, -118.4662, 32.7186, -86.7104};

        public static double[] getBounds(String countryCode) {
            switch (countryCode.toUpperCase()) {
                case "US": return US_BOUNDS;
                case "CA": return CA_BOUNDS;
                case "MX": return MX_BOUNDS;
                default: return null;
            }
        }
    }

    /**
     * Check if coordinates are within country bounds
     */
    public static boolean isWithinCountry(double lat, double lng, String countryCode) {
        double[] bounds = CountryBounds.getBounds(countryCode);
        if (bounds == null) return false;

        return lat >= bounds[0] && lat <= bounds[2] && lng >= bounds[1] && lng <= bounds[3];
    }

    /**
     * Get center coordinates for a country
     */
    public static double[] getCountryCenter(String countryCode) {
        switch (countryCode.toUpperCase()) {
            case "US": return new double[]{39.8283, -98.5795}; // Geographic center of US
            case "CA": return new double[]{56.1304, -106.3468}; // Geographic center of Canada
            case "MX": return new double[]{23.6345, -102.5528}; // Geographic center of Mexico
            default: return new double[]{0, 0};
        }
    }

    /**
     * Format coordinates for display
     */
    public static String formatCoordinates(double lat, double lng) {
        return String.format("%.6f, %.6f", lat, lng);
    }
}