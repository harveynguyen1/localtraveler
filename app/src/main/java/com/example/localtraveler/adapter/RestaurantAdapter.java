package com.example.localtraveler.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.localtraveler.activities.MapsActivity;
import com.example.localtraveler.R;
import com.example.localtraveler.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    // List of Restaurants to be displayed in the RecyclerView
    private List<Restaurant> restaurantList;

    // Context of the activity where the adapter is used
    private Context context;

    // Constructor to initialize the adapter with context and restaurant list
    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        this.context = context;
    }

    // Method to update the list of restaurants and notify the adapter
    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Restaurant> list) {
        this.restaurantList = list;
        notifyDataSetChanged();
    }

    // Inflates the layout list_items and returns a new instance of ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);
        return new ViewHolder(view);
    }

    // Binds the restaurant data to the views in each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.name.setText(restaurant.getName());
        holder.address.setText(restaurant.getAddress());
        holder.priceLevel.setText(restaurant.getPriceLevel());
        holder.ratingBar.setRating(restaurant.getRating());
        if (!restaurant.getImageUrl().isEmpty()) {
            // Load image using Picasso
            // https://square.github.io/picasso/
            Picasso.get().load(restaurant.getImageUrl()).into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("lat", restaurant.getLat());
                intent.putExtra("lon", restaurant.getLon());
                intent.putExtra("restaurant", restaurant);
                context.startActivity(intent);
            }
        });
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // ViewHolder class to hold references to the views in each item
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address, priceLevel;
        public ImageView imageView;
        public RatingBar ratingBar;

        // Constructor to initialize the views
        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.restaurant_name);
            address = view.findViewById(R.id.restaurant_address);
            priceLevel = view.findViewById(R.id.restaurant_price);
            imageView = view.findViewById(R.id.restaurant_image);
            ratingBar = view.findViewById(R.id.restaurant_rating);
        }
    }
}
