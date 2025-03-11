package com.example.localtraveler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localtraveler.databinding.ActivitySaveItineraryBinding;

public class SaveItineraryActivity extends AppCompatActivity {
    // View binding for the activity
    ActivitySaveItineraryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivitySaveItineraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set click listener for the "Save Itinerary" button
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SavedItineraryActivity with new task and clear task flags
                Intent intent = new Intent(SaveItineraryActivity.this, SavedItineraryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Set click listener for the "Add City" button
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AddCityActivity and finish the current task
                startActivity(new Intent(SaveItineraryActivity.this, AddCityActivity.class));
                finishAffinity();
            }
        });
    }
}