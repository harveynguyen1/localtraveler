package com.example.localtraveler.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localtraveler.R;
import com.example.localtraveler.activities.TrackActivity;
import com.example.localtraveler.client.DatabaseHelper;
import com.example.localtraveler.databinding.ItemSavedIternaryBinding;
import com.example.localtraveler.models.Helper;
import com.example.localtraveler.models.IternityModel;
import com.example.localtraveler.models.Restaurant;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SaveIternaryAdapter extends RecyclerView.Adapter<SaveIternaryAdapter.ViewHolder> {

    // List of IternityModel objects representing saved itineraries
    private List<IternityModel> restaurantList;

    // Context of the activity where the adapter is used
    private Context context;
    DatabaseHelper databaseHelper;

    // Constructor to initialize the adapter with context and restaurant list
    public SaveIternaryAdapter(Context context, List<IternityModel> restaurantList) {
        this.restaurantList = restaurantList;
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    // Inflates the layout item_saved_iternary and returns a new instance of ViewHolder
    @Override
    public SaveIternaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout for each row
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_iternary, parent, false);
        return new ViewHolder(view);
    }

    // Binds the itinerary data to the views in each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the itinerary model for the current position
        IternityModel iternityModel = restaurantList.get(position);

        // Set the city name and duration in the respective TextViews
        holder.binding.title.setText(iternityModel.getCityName());
        holder.binding.tvDuration.setText(iternityModel.getStartDate() + "-" + iternityModel.getEndDate());

        // Create a combined list of restaurant and venue images
        List<Restaurant> imageUrlList = new ArrayList<>();
        imageUrlList.addAll(iternityModel.getRestaurantList());
        imageUrlList.addAll(iternityModel.getVenueLists());

        // Set the adapter to the RecyclerView to display images
        ImageAdapter adapter = new ImageAdapter(imageUrlList);
        holder.binding.rvImages.setAdapter(adapter);

        // Set a long click listener to show a delete confirmation dialog
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Show delete confirmation dialog when item is long clicked
                showDeleteConfirmationDialog(iternityModel);
                return false;
            }
        });

        // Set a click listener to open the TrackActivity with the selected itinerary details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the selected itinerary model to a helper class for easy access
                Helper.iternityModel = iternityModel;

                // Create an intent to start the TrackActivity
                Intent intent = new Intent(context, TrackActivity.class);

                // Pass the "save" flag as false to the intent
                intent.putExtra("save", false);

                // Start the TrackActivity
                context.startActivity(intent);
            }
        });
    }

    // Displays a confirmation dialog to delete an itinerary
    private void showDeleteConfirmationDialog(IternityModel model) {

        // Create and show a MaterialAlertDialogBuilder for delete confirmation
        new MaterialAlertDialogBuilder(context)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this item?")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User clicked the "Cancel" button, so dismiss the dialog
                    dialog.dismiss();
                })
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove the model from the restaurant list
                    restaurantList.remove(model);

                    // Delete the itinerary from the database
                    databaseHelper.deleteItinerary(model.getId());

                    // Notify the adapter that the data set has changed
                    notifyDataSetChanged();
                })
                .show();
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // ViewHolder class to hold references to the views in each item
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Binding object for item_saved_iternary layout
        ItemSavedIternaryBinding binding;

        // Constructor to initialize the views
        public ViewHolder(View view) {
            super(view);

            // Bind the view to the binding object
            binding = ItemSavedIternaryBinding.bind(view);
        }
    }
}