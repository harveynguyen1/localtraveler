package com.example.localtraveler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localtraveler.R;
import com.example.localtraveler.databinding.ItemRestaurantBinding;
import com.example.localtraveler.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.RestaurantViewHolder> {

    // List of venues
    private List<Restaurant> venuesList;

    // Constructor
    public PagerAdapter(List<Restaurant> venuesList) {
        this.venuesList = venuesList;
    }

    // Create new views
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant venue = venuesList.get(position);
        holder.binding.restaurantName.setText(venue.getName());
        holder.binding.restaurantAddress.setText(venue.getAddress());
        holder.binding.restaurantRating.setRating(venue.getRating());
        if (!venue.getImageUrl().isEmpty()) {
            // Load image using Picasso
            // https://square.github.io/picasso/
            Picasso.get().load(venue.getImageUrl()).into(holder.binding.restaurantImage);
        }
    }

    // Return the number of items
    @Override
    public int getItemCount() {
        return venuesList.size();
    }

    // ViewHolder class
    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        ItemRestaurantBinding binding;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRestaurantBinding.bind(itemView);
        }
    }
}
