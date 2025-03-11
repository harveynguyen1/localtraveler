package com.example.localtraveler.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localtraveler.databinding.ActivitySummaryBinding;
import com.example.localtraveler.models.Helper;

public class SummaryActivity extends AppCompatActivity {
    // View binding for the activity
    ActivitySummaryBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivitySummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve data from the intent
        long days = getIntent().getLongExtra("days", 0);
        String name = getIntent().getStringExtra("name");

        // Set the text views with the retrieved data
        binding.tvDays.setText(":  " + days);
        binding.tvResturants.setText(":  " + Helper.iternityModel.getRestaurantList().size());
        binding.tvVenues.setText(":  " + Helper.iternityModel.getVenueLists().size());

        // Set click listener for the "View on Map" button
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MapsActivity
                startActivity(new Intent(SummaryActivity.this, MapsActivity.class));
            }
        });

        // Set click listener for the "Track Itinerary" button
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SummaryActivity.this, TrackActivity.class));
            }
        });
    }
}