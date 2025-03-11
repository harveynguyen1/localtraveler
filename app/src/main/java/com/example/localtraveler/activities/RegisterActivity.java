package com.example.localtraveler.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localtraveler.client.DatabaseHelper;
import com.example.localtraveler.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    // View binding for the activity
    ActivityRegisterBinding binding;

    // Database helper instance for managing database operations
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using view binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Set up the register button click listener
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get input values from the user
                String username = binding.etUser.getText().toString().trim();
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();

                // Check if any field is empty
                // https://developer.android.com/reference/android/text/TextUtils
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Attempt to register the user
                    boolean isInserted = databaseHelper.registerUser(username, email, password);
                    if (isInserted) {
                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        // Close the activity after successful registration
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
}