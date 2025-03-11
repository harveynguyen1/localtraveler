package com.example.localtraveler.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.localtraveler.adapter.IternityAdapter;
import com.example.localtraveler.client.DatabaseHelper;
import com.example.localtraveler.databinding.ActivityTrackBinding;
import com.example.localtraveler.models.Helper;
import com.example.localtraveler.models.Restaurant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends AppCompatActivity {
    // View binding for the activity
    ActivityTrackBinding binding;

    // List to hold the restaurant and venue data
    List<Restaurant> list = new ArrayList<>();

    // Database helper instance for managing database operations
    DatabaseHelper databaseHelper;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityTrackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Check if extras are passed in the intent, if so, hide the save button
        if (getIntent().getExtras() != null) {
            binding.btnSave.setVisibility(View.GONE);
        }

        // Clear the list and add restaurants and venues from the itinerary model
        list.clear();
        list.addAll(Helper.iternityModel.getRestaurantList().subList(0, Helper.iternityModel.getRestaurantList().size()));
        list.addAll(Helper.iternityModel.getVenueLists().subList(0, Helper.iternityModel.getVenueLists().size()));

        // Set the city name and duration in the respective text views
        binding.tvDuration.setText("" + calculateDaysDifference(Helper.iternityModel.getStartDate(), Helper.iternityModel.getEndDate()));
        binding.tvCity.setText(Helper.iternityModel.getCityName());
        binding.rvResturants.setLayoutManager(new LinearLayoutManager(this));

        // Set up the RecyclerView with a linear layout manager
        IternityAdapter adapter = new IternityAdapter(list);
        binding.rvResturants.setAdapter(adapter);

        // Set click listener for the back button to finish the activity
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set click listener for the save button to save the itinerary data and start SaveItineraryActivity
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.setData(Helper.iternityModel);
                startActivity(new Intent(TrackActivity.this, SaveItineraryActivity.class));
            }
        });
    }

    // Calculates the difference in days between two dates
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long calculateDaysDifference(String dateStr1, String dateStr2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
        try {
            LocalDate date1 = LocalDate.parse(dateStr1, formatter);
            LocalDate date2 = LocalDate.parse(dateStr2, formatter);
            return ChronoUnit.DAYS.between(date1, date2);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + e.getMessage());
            return -1;
        }
    }
}