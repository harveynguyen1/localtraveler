package com.example.localtraveler.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.localtraveler.R;
import com.example.localtraveler.adapter.RestaurantAdapter;
import com.example.localtraveler.databinding.ActivityMainBinding;
import com.example.localtraveler.models.Helper;
import com.example.localtraveler.models.IternityModel;
import com.example.localtraveler.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<Restaurant> list = new ArrayList<>();
    ArrayList<Restaurant> venuesList = new ArrayList<>();
    RestaurantAdapter adapter;
    RestaurantAdapter venuesAdapter;
    LatLng location;
    String apikey;
    ProgressDialog progressDialog;
    List<Restaurant> finalRestaurants = new ArrayList<>();
    List<Restaurant> finalVenues = new ArrayList<>();
    long diff;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.app_name);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        // Get data from the intent
        double lat = getIntent().getDoubleExtra("lat", 0.0);
        double lon = getIntent().getDoubleExtra("lon", 0.0);
        diff = getIntent().getLongExtra("date", 0);
        location = new LatLng(lat, lon);
        String name = getIntent().getStringExtra("name");
        String start = getIntent().getStringExtra("start");
        String end = getIntent().getStringExtra("end");

        // Set text views
        binding.tvCity.setText(name);
        binding.tvDuration.setText("" + diff);

        // Get Google Maps API key
        apikey = getString(R.string.google_map_key);

        // Set up RecyclerViews
        binding.rvRestaurants.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvVenues.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize adapters
        adapter = new RestaurantAdapter(MainActivity.this, finalRestaurants);
        venuesAdapter = new RestaurantAdapter(MainActivity.this, finalVenues);
        binding.rvRestaurants.setAdapter(adapter);
        binding.rvVenues.setAdapter(venuesAdapter);

        // Show progress dialog and fetch data
        progressDialog.show();
        getNearbyRestaurants(location);
        getNearbyPlaces(location, name);

        // Set button click listener to refresh data
        binding.button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                progressDialog.show();
                list.clear();
                venuesList.clear();
                finalRestaurants.clear();
                finalVenues.clear();
                adapter.notifyDataSetChanged();
                venuesAdapter.notifyDataSetChanged();
                getNearbyPlaces(location, name);
                getNearbyRestaurants(location);
            }
        });

        // Set button click listener to show summary
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IternityModel iternityModel = new IternityModel(name, start, end, finalRestaurants, finalVenues);
                iternityModel.setUserId(Helper.user.getId());
                Helper.iternityModel = iternityModel;
                Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
                intent.putExtra("days", diff);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }

    // Fetches nearby restaurants using Google Places API
    private void getNearbyRestaurants(LatLng location) {
        // Use OkHttp for network operation
        // https://square.github.io/okhttp
        OkHttpClient client = new OkHttpClient();
        String placeUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location.latitude + "," + location.longitude +
                "&radius=15000&type=restaurant&key=" + apikey;

        // Build the request
        Request request = new Request.Builder().url(placeUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray results = json.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);
                            // Get Parameter field results from Google Places API Details
                            // https://developers.google.com/maps/documentation/places/web-service/details
                            String name = result.getString("name");
                            String address = result.getString("vicinity");
                            String placeId = result.getString("place_id");
                            String imageUrl = "";
                            if (result.has("photos")) {
                                // Use Google Places API to get photos
                                // https://developers.google.com/maps/documentation/places/web-service/photos
                                imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                                        result.getJSONArray("photos").getJSONObject(0).getString("photo_reference") +
                                        "&key=" + apikey;
                            }
                            String priceLevel = result.optString("price_level", "N/A");
                            float rating = result.has("rating") ? (float) result.getDouble("rating") : 0;

                            // Get latitude and longitude
                            JSONObject geometry = result.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            double latitude = location.getDouble("lat");
                            double longitude = location.getDouble("lng");

                            list.add(new Restaurant(name, address, imageUrl, priceLevel, rating, latitude, longitude, placeId, getPostalCode(MainActivity.this, latitude, longitude)));
                        }
                        runOnUiThread(() -> {
                            finalRestaurants = selectRandomObjects(list, (int) diff);
                            adapter.setList(finalRestaurants);
                        });
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Fetches nearby places using Google Places API
    private void getNearbyPlaces(LatLng location, String cityName) {
        // Use OkHttp for network operation
        // https://square.github.io/okhttp
        OkHttpClient client = new OkHttpClient();
        String placeUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location.latitude + "," + location.longitude +
                "&radius=1500&type=tourist_attraction&key=" + apikey; // Remove 'type' to get various places
        String CityplaceUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=tourist+attractions+in+" +
                cityName + "&key=" + apikey;
        Request request = new Request.Builder().url(CityplaceUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray results = json.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);
                            String name = result.getString("name");
                            String placeId = result.getString("place_id");
                            String address = "N/A";

                            if (result.has("formatted_address")) {
                                address = result.getString("formatted_address");
                            }
                            String imageUrl = "";
                            if (result.has("photos")) {
                                // Use Google Places API to get photos
                                // https://developers.google.com/maps/documentation/places/web-service/photos
                                imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                                        result.getJSONArray("photos").getJSONObject(0).getString("photo_reference") +
                                        "&key=" + apikey;
                            }
                            String priceLevel = result.has("price_level") ? result.getString("price_level") : "N/A";
                            float rating = result.has("rating") ? (float) result.getDouble("rating") : 0;

                            // Get latitude and longitude
                            JSONObject geometry = result.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            double latitude = location.getDouble("lat");
                            double longitude = location.getDouble("lng");

                            venuesList.add(new Restaurant(name, address, imageUrl, priceLevel, rating, latitude, longitude, placeId, getPostalCode(MainActivity.this, latitude, longitude)));
                        }
                        runOnUiThread(() -> {
                            finalVenues = selectRandomObjects(venuesList, (int) diff);
                            venuesAdapter.setList(finalVenues);
                        });
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
            }
        });
    }

    // Retrieves the postal code for the given latitude and longitude
    private String getPostalCode(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getPostalCode();
            } else {
                return "N/A";
            }
        } catch (IOException e) {
//            Toast.makeText(MainActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "N/A";
        }
    }

    // Selects a specified number of random objects from the original list.
    public static List<Restaurant> selectRandomObjects(List<Restaurant> originalList, int days) {
        int numberOfObjectsToSelect = 2 * days;

        // Ensure we don't try to select more objects than available in the list
        if (numberOfObjectsToSelect > originalList.size()) {
            numberOfObjectsToSelect = originalList.size();
        }

        // Create a copy of the original list to avoid modifying it
        List<Restaurant> copyOfOriginalList = new ArrayList<>(originalList);

        // Shuffle the list to get random objects
        Collections.shuffle(copyOfOriginalList, new Random());

        // Return the sublist of the first 'numberOfObjectsToSelect' elements
        return copyOfOriginalList.subList(0, numberOfObjectsToSelect);
    }
}