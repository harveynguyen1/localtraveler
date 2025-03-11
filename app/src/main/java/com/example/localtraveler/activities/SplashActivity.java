package com.example.localtraveler.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.localtraveler.R;
import com.example.localtraveler.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    // View binding for the activity
    ActivitySplashBinding binding;

    // Animation objects for the splash screen
    Animation fromTop, fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load animations from resources
        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);

        // Set animations to the views
        binding.ivTop.setAnimation(fromTop);
        binding.ivBottom.setAnimation(fromBottom);

        // Start a new thread to display the splash screen for a specified duration
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // Sleep for 3 seconds
                    sleep(3000);

                    // Start the LoginActivity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                    // Finish the current activity
                    finish();
                } catch (InterruptedException e) {
                    // Display an error message if interrupted
                    Toast.makeText(SplashActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
                }
            }
        };
        thread.start();
    }
}