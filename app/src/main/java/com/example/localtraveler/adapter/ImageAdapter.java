package com.example.localtraveler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localtraveler.R;
import com.example.localtraveler.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    // List of Restaurants containing image URLs
    private List<Restaurant> imageUrlList;

    // Constructor to initialize the adapter with a list of Restaurants
    public ImageAdapter(List<Restaurant> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    // Create a new view holder for the item layout
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    // Bind the image URL to the corresponding ImageView
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Restaurant imageUrl = imageUrlList.get(position);
        if (imageUrl.getImageUrl() != null) {
            if (!imageUrl.getImageUrl().isEmpty()) {
                // Load image using Picasso
                // https://square.github.io/picasso/
                Picasso.get().load(imageUrl.getImageUrl()).into(holder.imageView);
            }
        }
    }

    // Return the number of items in the list
    @Override
    public int getItemCount() {
        return imageUrlList.size();
    }

    // ViewHolder class for the item layout
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
