package com.example.localtraveler.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localtraveler.R;
import com.example.localtraveler.databinding.ActivityAddCityctivityBinding;
import com.example.localtraveler.models.Helper;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddCityActivity extends AppCompatActivity {
    // View binding for the activity
    ActivityAddCityctivityBinding binding;

    // PlacesClient for interacting with Google Places API
    private PlacesClient placesClient;

    // Variables to store selected place details
    String name, address;
    LatLng latLng;
    // Request code for place autocomplete
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityAddCityctivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the Places API
        String apiKey = getString(R.string.google_map_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Set the current date as the default start and end dates
        binding.tvStart.setText(getCurrentDate());
        binding.tvEnd.setText(getCurrentDate());

        // Set the welcome message with the user's name
        binding.tvUser.setText(getString(R.string.welcome) + " " + Helper.user.getName());

        // Set the search button click listener to start the place search
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlaces();
            }
        });

        // Set click listeners for the start and end date TextViews to show date picker dialogs
        binding.tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.tvStart);
            }
        });

        binding.tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.tvEnd);
            }
        });

        // Set the create button click listener to validate and start the itinerary creation process
        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCityActivity.this, MainActivity.class);
                if (name == null) {
                    Toast.makeText(AddCityActivity.this, "Please select city first", Toast.LENGTH_SHORT).show();
                    return;
                }

                String start = binding.tvStart.getText().toString();
                String end = binding.tvEnd.getText().toString();
                long difference = calculateDaysDifference(start, end);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (difference < 1) {
                        Toast.makeText(AddCityActivity.this, "Start date must be earlier than the end date.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Start the MainActivity with the itinerary details
                intent.putExtra("name", name);
                intent.putExtra("start", start);
                intent.putExtra("end", end);
                intent.putExtra("date", difference);
                intent.putExtra("lat", latLng.latitude);
                intent.putExtra("lon", latLng.longitude);
                startActivity(intent);
            }
        });

        // Set the back button click listener to navigate to the login activity
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddCityActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

        // Set the saved button click listener to navigate to the saved itineraries activity
        binding.btnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddCityActivity.this, SavedItineraryActivity.class));
            }
        });

    }

    // Launch the place autocomplete activity to search for places
    private void searchPlaces() {
        // Specify the fields to return
        // Use Google Places API
        // https://developers.google.com/maps/documentation/places/web-service/search-find-place
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Start the autocomplete intent
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry("CA")
                .build(AddCityActivity.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    // Show a date picker dialog to select a date
    private void showDatePickerDialog(TextView dateTextView) {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new DatePickerDialog instance and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddCityActivity.this, (view, year1, monthOfYear, dayOfMonth) -> {
            // Set the selected date on the TextView
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth);

            // Format the date in dd/MM/yyyy format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate.getTime());
            if (dateTextView.getId() == binding.tvEnd.getId()) {
                String startDate = binding.tvStart.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (calculateDaysDifference(startDate, formattedDate) < 1) {
                        Toast.makeText(AddCityActivity.this, "Start date must be earlier than the end date.", Toast.LENGTH_SHORT).show();
                    } else {
                        dateTextView.setText(formattedDate);
                    }
                }
            } else {
                dateTextView.setText(formattedDate);

            }


        }, year, month, day);

        // Set the minimum date to the current date
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    // Handle the result from the place autocomplete activity
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Use Google Places API Autocomplete
                // https://developers.google.com/maps/documentation/places/android-sdk/autocomplete
                Place place = Autocomplete.getPlaceFromIntent(data);
                name = place.getName();
                address = place.getAddress();
                latLng = place.getLatLng();
                String[] parts = address.split(", ");
                if (parts.length > 2) {
                    binding.btnSearch.setText(parts[0] + ", " + parts[1]);
                } else {
                    binding.btnSearch.setText(address);
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Autocomplete", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation
            }
        }
    }

    // Calculate the difference in days between two dates
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long calculateDaysDifference(String dateStr1, String dateStr2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate date1 = LocalDate.parse(dateStr1, formatter);
            LocalDate date2 = LocalDate.parse(dateStr2, formatter);
            return ChronoUnit.DAYS.between(date1, date2);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + e.getMessage());
            return -1;
        }
    }

    // Get the current date in dd/MM/yyyy format
    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

}
