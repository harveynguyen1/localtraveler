package com.example.localtraveler.activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localtraveler.R;
import com.example.localtraveler.models.Restaurant;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaceDetailsActivity extends AppCompatActivity {
    private ImageView placeImage, btnBack;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placeRating;
    private TextView placePriceLevel;
    private TextView placeWebsite;
    private TextView placeOpeningHours;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        placeImage = findViewById(R.id.place_image);
        placeName = findViewById(R.id.place_name);
        placeAddress = findViewById(R.id.place_address);
        placeRating = findViewById(R.id.place_rating);
        placePriceLevel = findViewById(R.id.place_price_level);
        placeWebsite = findViewById(R.id.place_website);
        placeOpeningHours = findViewById(R.id.place_opening_hours);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.app_name);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        // Set back button click listener to finish the activity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Pass the restaurant object via intent
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        if (restaurant != null) {
            // Fetch restaurant details using place ID
            fetchRestaurantDetails(restaurant.getPlaceId());
        }
    }

    // Fetches the details of a restaurant using Google Places API
    private void fetchRestaurantDetails(String id) {
        progressDialog.show();
        // Use OkHttp for network operation
        // https://square.github.io/okhttp
        OkHttpClient client = new OkHttpClient();
        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + id + "&key=" + getString(R.string.google_map_key);

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        // Parse JSON response
                        JSONObject json = new JSONObject(responseData);
                        JSONObject result = json.getJSONObject("result");

                        // Extract restaurant details from JSON
                        String name = result.getString("name");
                        String address = result.getString("vicinity");
                        String imageUrl;
                        if (result.has("photos")) {
                            // Use Google Places API to get photos
                            // https://developers.google.com/maps/documentation/places/web-service/photos
                            imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                                    result.getJSONArray("photos").getJSONObject(0).getString("photo_reference") +
                                    "&key=" + getString(R.string.google_map_key); // Google Maps API Key
                        } else {
                            imageUrl = "";
                        }
                        String priceLevel = result.optString("price_level", "N/A");
                        float rating = result.has("rating") ? (float) result.getDouble("rating") : 0;
                        String website = result.has("website") ? result.getString("website") : "N/A";
                        String openingHours = result.has("opening_hours") ? result.getJSONObject("opening_hours").getJSONArray("weekday_text").join("\n") : "N/A";

                        // Update UI on the main thread
                        runOnUiThread(() -> {
                            placeName.setText(name);
                            placeAddress.setText(address);
                            placeRating.setText(rating + "⭐");
                            placePriceLevel.setText(priceLevel);
                            placeWebsite.setText(website);
                            placeOpeningHours.setText(openingHours);

                            if (!imageUrl.isEmpty()) {
                                Picasso.get().load(imageUrl).into(placeImage);
                            } else {
                                placeImage.setImageResource(R.drawable.img_travel);
                            }
                        });

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        });

    }

}
