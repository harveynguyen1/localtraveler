package com.example.localtraveler.client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.localtraveler.models.Helper;
import com.example.localtraveler.models.IternityModel;
import com.example.localtraveler.models.Restaurant;
import com.example.localtraveler.models.User;

import java.util.ArrayList;
import java.util.List;

// https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "UserDatabase.db";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Table Name for user information
    private static final String TABLE_NAME = "users";

    // Table columns for users table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Table names for itineraries, restaurants, and venues
    private static final String TABLE_ITINERARY = "itinerary";
    private static final String TABLE_RESTAURANT = "restaurant";
    private static final String TABLE_VENUE = "venue";

    // Common columns for itinerary, restaurant, and venue tables
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_POSTAL = "postal";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LON = "lon";

    // Itinerary table columns
    private static final String COLUMN_CITY_NAME = "cityName";
    private static final String COLUMN_START_DATE = "startDate";
    private static final String COLUMN_END_DATE = "endDate";
    private static final String COLUMN_UID = "uid";

    // Restaurant table columns
    private static final String COLUMN_IMAGE_URL = "imageUrl";
    private static final String COLUMN_PRICE_LEVEL = "priceLevel";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_ITINERARY_ID = "itineraryId";
    private static final String COLUMN_PLACE_ID = "place_id";

    // Venue table columns
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_ITINERARY_ID_VENUE = "itineraryId";


    // Create Table SQL Query for users table
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT NOT NULL, "
            + COLUMN_EMAIL + " TEXT NOT NULL, "
            + COLUMN_PASSWORD + " TEXT NOT NULL);";

    // Constructor to initialize the DatabaseHelper
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create users table
        db.execSQL(CREATE_TABLE);

        // Create itinerary table
        String CREATE_TABLE_ITINERARY = "CREATE TABLE " + TABLE_ITINERARY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CITY_NAME + " TEXT,"
                + COLUMN_UID + " INTEGER,"
                + COLUMN_START_DATE + " TEXT,"
                + COLUMN_END_DATE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE_ITINERARY);

        // Create Restaurant table
        String CREATE_TABLE_RESTAURANT = "CREATE TABLE " + TABLE_RESTAURANT + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_POSTAL + " TEXT,"
                + COLUMN_IMAGE_URL + " TEXT,"
                + COLUMN_PRICE_LEVEL + " TEXT,"
                + COLUMN_PLACE_ID + " TEXT,"
                + COLUMN_RATING + " REAL,"
                + COLUMN_LAT + " REAL,"
                + COLUMN_LON + " REAL,"
                + COLUMN_ITINERARY_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_ITINERARY_ID + ") REFERENCES " + TABLE_ITINERARY + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_TABLE_RESTAURANT);

        // Create Venue table
        String CREATE_TABLE_VENUE = "CREATE TABLE " + TABLE_VENUE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_POSTAL + " TEXT,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_PLACE_ID + " TEXT,"
                + COLUMN_IMAGE_URL + " TEXT,"
                + COLUMN_LAT + " REAL,"
                + COLUMN_LON + " REAL,"
                + COLUMN_ITINERARY_ID_VENUE + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_ITINERARY_ID_VENUE + ") REFERENCES " + TABLE_ITINERARY + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_TABLE_VENUE);

    }

    // Called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARY);

        // Create tables again
        onCreate(db);
    }

    // Method to register a new user
    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);

        // Insert the new user data into the users table
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        // Return true if insert was successful, false otherwise
        return result != -1;
    }

    // Method to check user credentials for sign-in
    public User checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_ID};  // Ensure COLUMN_ID is included in columns
        String selection = COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {email, password};

        // Query the users table with the provided email and password
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Retrieve user information from the cursor
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                long uId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                cursor.close();
                db.close();

                // Create a User object with the retrieved information
                User user = new User(username, userEmail, password);
                user.setId(uId);
                return user;
            } else {
                cursor.close();
            }
        }
        db.close();
        // Return null if no user was found with the provided credentials
        return null;
    }

    // Insert Itinerary along with its associated restaurants and venues
    public void setData(IternityModel model) {
        long id = insertItinerary(model);
        // Insert each restaurant associated with the itinerary
        for (Restaurant restaurant : model.getRestaurantList()) {
            insertRestaurant(restaurant, id);
        }
        // Insert each venue associated with the itinerary
        for (Restaurant restaurant : model.getVenueLists()) {
            insertVenue(restaurant, id);
        }
    }

    // Method to insert an itinerary into the itinerary table
    public long insertItinerary(IternityModel itinerary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_NAME, itinerary.getCityName());
        values.put(COLUMN_START_DATE, itinerary.getStartDate());
        values.put(COLUMN_END_DATE, itinerary.getEndDate());
        values.put(COLUMN_UID, itinerary.getUserId());
        values.put(COLUMN_END_DATE, itinerary.getEndDate());
        // Insert the itinerary and return its ID
        return db.insert(TABLE_ITINERARY, null, values);
    }

    // Method to insert a restaurant into the restaurant table
    public long insertRestaurant(Restaurant restaurant, long itineraryId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, restaurant.getName());
        values.put(COLUMN_ADDRESS, restaurant.getAddress());
        values.put(COLUMN_POSTAL, restaurant.getPostalCode());
        values.put(COLUMN_IMAGE_URL, restaurant.getImageUrl());
        values.put(COLUMN_PLACE_ID, restaurant.getPlaceId());
        values.put(COLUMN_PRICE_LEVEL, restaurant.getPriceLevel());
        values.put(COLUMN_RATING, restaurant.getRating());
        values.put(COLUMN_LAT, restaurant.getLat());
        values.put(COLUMN_LON, restaurant.getLon());
        values.put(COLUMN_ITINERARY_ID, itineraryId);

        // Insert the restaurant and return its ID
        return db.insert(TABLE_RESTAURANT, null, values);
    }

    // Method to insert a venue into the venue table
    public long insertVenue(Restaurant venue, long itineraryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, venue.getName());
        values.put(COLUMN_ADDRESS, venue.getAddress());
        values.put(COLUMN_POSTAL, venue.getPostalCode());
        values.put(COLUMN_PLACE_ID, venue.getPlaceId());
        values.put(COLUMN_LAT, venue.getLat());
        values.put(COLUMN_LON, venue.getLon());
        values.put(COLUMN_IMAGE_URL, venue.getImageUrl());
        values.put(COLUMN_ITINERARY_ID_VENUE, itineraryId);

        Log.e("VenueURL", "insertVenue: " + venue.getImageUrl());

        // Insert the venue and return its ID
        return db.insert(TABLE_VENUE, null, values);
    }

    // Method to retrieve all itineraries for the current user
    public List<IternityModel> getAllItineraries() {
        // Initialize an empty list to store the itineraries
        List<IternityModel> itineraries = new ArrayList<>();

        // Select query to get all itineraries for the current user
        String selectQuery = "SELECT * FROM " + TABLE_ITINERARY + " WHERE " + COLUMN_UID + " = " + Helper.user.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Retrieve the itinerary details from the cursor
                IternityModel itinerary = new IternityModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE)),
                        getRestaurantsByItineraryId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))),
                        getVenuesByItineraryId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)))
                );
                // Set the itinerary ID from the cursor
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                itinerary.setId(id);
                itineraries.add(itinerary);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itineraries;
    }

    // Method to retrieve all restaurants for the current user
    public List<Restaurant> getAllRestaurants() {

        List<Restaurant> restaurants = new ArrayList<>();

        // Select query to get all restaurants for the current user's itineraries
        String selectQuery = "SELECT r.* FROM " + TABLE_RESTAURANT + " r " +
                "JOIN " + TABLE_ITINERARY + " i ON r." + COLUMN_ITINERARY_ID + " = i." + COLUMN_ID +
                " WHERE i." + COLUMN_UID + " = " + Helper.user.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Retrieve the restaurant details from the cursor
                Restaurant restaurant = new Restaurant(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE_LEVEL)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LAT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTAL))
                );
                restaurants.add(restaurant);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return restaurants;
    }

    // Method to retrieve all restaurants associated with a specific itinerary ID
    public List<Restaurant> getRestaurantsByItineraryId(long itineraryId) {

        List<Restaurant> restaurants = new ArrayList<>();
        // Select query to get all restaurants for the specified itinerary ID
        String selectQuery = "SELECT * FROM " + TABLE_RESTAURANT + " WHERE " + COLUMN_ITINERARY_ID + " = " + itineraryId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Retrieve the restaurant details from the cursor
                Restaurant restaurant = new Restaurant(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE_LEVEL)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LAT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTAL))

                );
                restaurants.add(restaurant);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return restaurants;
    }

    // Method to retrieve all venues associated with a specific itinerary ID
    public List<Restaurant> getVenuesByItineraryId(long itineraryId) {

        List<Restaurant> venues = new ArrayList<>();
        // Select query to get all venues for the specified itinerary ID
        String selectQuery = "SELECT * FROM " + TABLE_VENUE + " WHERE " + COLUMN_ITINERARY_ID_VENUE + " = " + itineraryId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Retrieve the venue details from the cursor
                Restaurant venue = new Restaurant(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
                        "", 5f,
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LAT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTAL))
                );
                venues.add(venue);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return venues;
    }

    // Method to delete an itinerary by its ID
    public void deleteItinerary(long id) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the itinerary from the itinerary table
        db.delete(TABLE_ITINERARY, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
