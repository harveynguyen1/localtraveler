package com.example.localtraveler.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localtraveler.activities.MapsActivity;
import com.example.localtraveler.R;
import com.example.localtraveler.databinding.CardItemBinding;
import com.example.localtraveler.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;


public class IternityAdapter extends RecyclerView.Adapter<IternityAdapter.RestaurantViewHolder> {

    // List of venues
    private List<Restaurant> venuesList;

    // Constructor
    public IternityAdapter(List<Restaurant> venuesList) {
        this.venuesList = venuesList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public IternityAdapter.RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new IternityAdapter.RestaurantViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull IternityAdapter.RestaurantViewHolder holder, int position) {
        Restaurant venue = venuesList.get(position);
        Context context = holder.itemView.getContext();
        holder.binding.title.setText(venue.getName());
        holder.binding.address.setText(venue.getAddress());
        holder.binding.restaurantRating.setRating(venue.getRating());
        String postalCode = "";
        if (venue.getPostalCode() == null) {
            postalCode = "N/A";
        } else {
            postalCode = venue.getPostalCode();
        }

        holder.binding.postalCode.setText("Postal Code: " + postalCode);
        if (!venue.getImageUrl().isEmpty()) {
            Picasso.get().load(venue.getImageUrl()).into(holder.binding.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("lat", venue.getLat());
                intent.putExtra("lon", venue.getLon());
                intent.putExtra("restaurant", venue);
                context.startActivity(intent);
            }
        });
    }

    // Return the number of venues
    @Override
    public int getItemCount() {
        return venuesList.size();
    }

    // View holder class
    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        CardItemBinding binding;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardItemBinding.bind(itemView);
        }
    }

}
