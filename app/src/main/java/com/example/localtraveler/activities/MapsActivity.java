package com.example.localtraveler.activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.localtraveler.R;
import com.example.localtraveler.adapter.OffsetItemDecoration;
import com.example.localtraveler.adapter.PagerAdapter;
import com.example.localtraveler.client.MapsApiClient;
import com.example.localtraveler.models.Helper;
import com.example.localtraveler.models.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.localtraveler.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    LatLng location;
    Marker userMarker, destinationMArker;
    List<Restaurant> list = new ArrayList<>();
    int currentPosition = 0;
    private Restaurant restaurant;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this activity
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up back button to finish the activity
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Retrieve data from the intent
        double lat = getIntent().getDoubleExtra("lat", 0.0);
        double lon = getIntent().getDoubleExtra("lon", 0.0);
        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        location = new LatLng(lat, lon);

        // If latitude is zero, load the itinerary data
        if (lat == 0.0) {
            list.clear();
            list.addAll(Helper.iternityModel.getRestaurantList().subList(0, Helper.iternityModel.getRestaurantList().size()));
            list.addAll(Helper.iternityModel.getVenueLists().subList(0, Helper.iternityModel.getVenueLists().size()));
            location = new LatLng(list.get(0).getLat(), list.get(0).getLon());
            restaurant = list.get(0);

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the map asynchronously
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("MapsActivity", "onMapReady: ");
        mMap = googleMap;
        getLastKnownLocation();
        setUpViewPager();

        // Remove the previous destination marker if it exists
        if (destinationMArker != null) {
            destinationMArker.remove();
        }

        // Add a new marker for the destination
        destinationMArker = mMap.addMarker(new MarkerOptions().title("Destination")
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromResource(R.drawable.marker_icon))));

    }

    // Converts a drawable resource to a Bitmap
    private Bitmap getBitmapFromResource(int resourceId) {
        return BitmapFactory.decodeResource(getResources(), resourceId);
    }

    // Sets up the ViewPager for displaying venues
    private void setUpViewPager() {
        PagerAdapter adapter = new PagerAdapter(list);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float offset = position * (-30);  // Adjust the offset as needed
                page.setTranslationX(offset);
            }
        });

        // Add item decoration for offset
        binding.viewPager.addItemDecoration(new OffsetItemDecoration(30));

        // Set up page change callback
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (currentPosition != position) {
                    showVenueOnMap(position);
                }
            }
        });

        // Set up click listener for the next button
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, PlaceDetailsActivity.class);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            }
        });
    }

    // Shows the selected venue on the map
    private void showVenueOnMap(int currentPosition) {
        location = new LatLng(list.get(currentPosition).getLat(), list.get(currentPosition).getLon());

        // Remove the previous destination marker if it exists
        if (destinationMArker != null) {
            destinationMArker.remove();
        }

        // Update the restaurant and add a new marker for the selected venue
        restaurant = list.get(currentPosition);
        destinationMArker = mMap.addMarker(new MarkerOptions().title(list.get(currentPosition).getName())
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromResource(R.drawable.marker_icon))));

        requestDirections(location);
    }

    // Gets the last known location of the user
    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        // Get the last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        drawUserLocation(lastKnownLatLng);
                    }
                });

        // Set up location request
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(5)
                .build();

        // Set up location callback
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                LatLng newLatLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                drawUserLocation(newLatLng);

            }
        };

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    // Requests location permissions from the user
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                101);
    }

    // Draws the user's location on the map
    private void drawUserLocation(LatLng latLng) {
        if (mMap == null) {
            return;
        }

        // Add a marker for the user's location
        userMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromResource(R.drawable.marker_icon))));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));

        requestDirections(location);

    }

    // Requests directions from the user's location to the destination
    private void requestDirections(LatLng destination) {

        LatLng origin = new LatLng(userMarker.getPosition().latitude, userMarker.getPosition().longitude);

        MapsApiClient client = new MapsApiClient();
        client.getRoutes(origin, destination, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Something wrong happened " + response);
                }

                String jsonResponse = response.body().string();

                List<LatLng> decodedPath = null;
                try {
                    decodedPath = client.parseRoutesFromResponse(jsonResponse);
                } catch (JSONException e) {
                    Toast.makeText(MapsActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                List<LatLng> finalDecodedPath = decodedPath;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawRoute(finalDecodedPath);
                    }
                });
            }
        });
    }

    //Draws the route on the map
    private void drawRoute(List<LatLng> decodedPath) {
        if (currentPolyline != null) {
            currentPolyline.setPoints(decodedPath); // Update the existing polyline with new points
        } else {
            PolylineOptions options = new PolylineOptions()
                    .addAll(decodedPath)
                    .color(getColor(R.color.colorSecondary))
                    .width(5)
                    .pattern(Arrays.asList(new Dash(10), new Gap(5)));

            currentPolyline = mMap.addPolyline(options); // Create a new polyline
        }
    }

    // Handles the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();
            } else {
                Log.e("MapsActivity", "onRequestPermissionsResult: Permission denied");
            }
        }
    }


}