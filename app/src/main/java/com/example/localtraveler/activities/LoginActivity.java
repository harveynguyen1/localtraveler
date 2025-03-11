package com.example.localtraveler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localtraveler.client.DatabaseHelper;
import com.example.localtraveler.databinding.ActivityLoginBinding;
import com.example.localtraveler.models.Helper;
import com.example.localtraveler.models.User;

public class LoginActivity extends AppCompatActivity {
    // View binding for accessing UI elements
    ActivityLoginBinding binding;
    // Database helper for database operations
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using view binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Set the login button click listener
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get email and password from input fields
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();

                // Check if email and password fields are filled
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Check user credentials in the database
                    User user = databaseHelper.checkUser(email, password);
                    if (user != null) {
                        // Store the logged-in user in the Helper class
                        Helper.user = user;
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Navigate to another activity, e.g., HomeActivity
                        Intent intent = new Intent(LoginActivity.this, AddCityActivity.class);
                        intent.putExtra("username", user.getName());
                        intent.putExtra("email", user.getEmail());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // Set the register button click listener to navigate to RegisterActivity
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}