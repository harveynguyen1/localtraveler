package com.example.localtraveler.client;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MapsApiClient {

    // OkHttpClient instance for making network requests
    private final OkHttpClient client = new OkHttpClient();

    // Method to fetch routes from Google Maps API
    public void getRoutes(LatLng origin, LatLng destination, Callback callback) {
        String apiKey = "API_KEY";
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving" +
                "&key=" + apiKey;

        // Build the HTTP request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Enqueue the request to be executed asynchronously
        client.newCall(request).enqueue(callback);
    }

    // Method to parse routes from the JSON response.
    public List<LatLng> parseRoutesFromResponse(String jsonResponse) throws JSONException {
        List<LatLng> decodedPath = new ArrayList<>();

        // Convert the response string to a JSON object
        JSONObject json = new JSONObject(jsonResponse);

        // Extract the "routes" array from the JSON object
        JSONArray routes = json.getJSONArray("routes");

        // Check if there are any routes in the response
        if (routes.length() > 0) {

            // Get the first route in the array
            JSONObject route = routes.getJSONObject(0);

            // Get the overview polyline object from the route
            JSONObject polyline = route.getJSONObject("overview_polyline");

            // Get the encoded points string from the polyline
            String points = polyline.getString("points");

            // Decode the encoded points to get the list of LatLng points
            decodedPath = decodePolyline(points);
        }

        return decodedPath;
    }

    // Method to decode an encoded polyline string into a list of LatLng points.
    private List<LatLng> decodePolyline(String encodedPolyline) {
        List<LatLng> polylinePoints = new ArrayList<>();
        int currentIndex = 0, polylineLength = encodedPolyline.length();
        int currentLat = 0, currentLng = 0;

        // Loop through the encoded string to decode each point
        while (currentIndex < polylineLength) {
            int shift = 0, result = 0;
            int currentChar;
            // Decode latitude
            do {
                currentChar = encodedPolyline.charAt(currentIndex++) - 63;
                result |= (currentChar & 0x1f) << shift;
                shift += 5;
            } while (currentChar >= 0x20);
            int deltaLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            currentLat += deltaLat;

            shift = 0;
            result = 0;
            // Decode longitude
            do {
                currentChar = encodedPolyline.charAt(currentIndex++) - 63;
                result |= (currentChar & 0x1f) << shift;
                shift += 5;
            } while (currentChar >= 0x20);
            int deltaLng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            currentLng += deltaLng;

            // Convert the decoded values to latitude and longitude
            double latitude = currentLat / 1e5;
            double longitude = currentLng / 1e5;

            // Add the LatLng point to the list
            polylinePoints.add(new LatLng(latitude, longitude));
        }

        return polylinePoints;
    }
}
