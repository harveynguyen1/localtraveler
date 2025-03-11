package com.example.localtraveler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.localtraveler.adapter.SaveIternaryAdapter;
import com.example.localtraveler.client.DatabaseHelper;
import com.example.localtraveler.databinding.ActivitySavedItineraryBinding;
import com.example.localtraveler.models.IternityModel;

import java.util.ArrayList;
import java.util.List;

public class SavedItineraryActivity extends AppCompatActivity {

    // View binding for the activity
    ActivitySavedItineraryBinding binding;

    // Database helper instance for managing database operations
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivitySavedItineraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve all saved itineraries from the database
        List<IternityModel> list = new ArrayList<>();
        list.addAll(databaseHelper.getAllItineraries());

        // Set up RecyclerView with a linear layout manager
        binding.rvData.setLayoutManager(new LinearLayoutManager(this));
        SaveIternaryAdapter adapter = new SaveIternaryAdapter(this, list);
        binding.rvData.setAdapter(adapter);

        // Show a message if no itineraries are saved
        if (list.isEmpty()) {
            binding.textView2.setVisibility(View.VISIBLE);
        }

        // Set click listener for the "Create New" button
        binding.btnCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedItineraryActivity.this, AddCityActivity.class));
                finishAffinity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Start AddCityActivity and finish the current activity
        startActivity(new Intent(SavedItineraryActivity.this, AddCityActivity.class));
        finish();
    }
}