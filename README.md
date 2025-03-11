# LocalTraveler - Your Personalized Travel Itinerary Planner

## Overview

LocalTraveler is an Android application designed to help users plan their travel itineraries efficiently. Users can specify a destination city, travel dates, and preferred duration, and the app will suggest a personalized itinerary with nearby restaurants and tourist attractions. The app leverages the Google Maps Platform APIs to provide rich location-based experiences.

## Features

*   **City Selection:** Users can input the name of the city they wish to visit.
*   **Date and Duration:** Specify travel dates and the number of days for the trip.
*   **Nearby Recommendations:** The app fetches nearby restaurants and tourist attractions using the Google Maps Places API.
*   **Personalized Itinerary:** It generates a custom itinerary based on the user's selected city and trip duration.
*   **Save Itineraries:** Users can save their created itineraries for future reference.
*   **User-Friendly Interface:** A clean and intuitive design makes it easy to navigate and use.
*   **Show summary**: In the SummaryActivity, the user can see the city and all the restaurants and attractions selected by the application.
* **Show saved itineraries**: In the SavedItineraryActivity, the user can see all the itineraries saved in the application.

## Technologies Used

*   **Android Jetpack:**
    *   `AppCompat`: For backward compatibility.
    *   `RecyclerView`: For efficient display of lists of restaurants and attractions.
    *   `ViewBinding`: For easy and type-safe access to views.
*   **Google Maps Platform APIs:**
    *   **Directions API:** to obtain the routes.
    *   **Places API:**
        *   **Nearby Search:** to find nearby restaurants and attractions.
        *   **Text Search:** to find tourist attractions by city.
        *   **Place Photos:** to fetch images of the places.
*   **OkHttp:** For making network requests to the Google Maps APIs.
*   **JSON:** For parsing the responses from the Google Maps APIs.
*   **SQLite:** for save the itineraries created.
* **Geocoder**: for get postalCode from location.

## Setup Instructions

1.  **Clone the Repository:**

2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open an Existing Project" and choose the cloned directory.

3.  **Add Google Maps API Key:**
    *   Obtain an API key from the Google Cloud Platform Console for the following APIs:
        *   Maps SDK for Android
        *   Places API
    *   Replace `YOUR_API_KEY` in the `getRoutes` method of `MapsApiClient` class, in `apikey` variable in the `MainActivity` and in the `string.xml` file in value `google_map_key`.

4.  **Build and Run:**
    *   Build the project (`Build > Make Project`).
    *   Run the app on an emulator or a physical Android device.

## Project Structure

*   `com.example.localtraveler`
    *   `adapter`: Contains RecyclerView adapters (e.g., `RestaurantAdapter`, `SaveIternaryAdapter`).
    *   `client`: Contains class for client database.
    *   `databinding`: Contains ViewBinding classes.
    *   `models`: Data classes representing Restaurants, Itineraries.
    *   `MainActivity.kt`: The main activity of the application, responsible for displaying restaurants and tourist attractions.
    *   `AddCityctivity`: to obtain the city and time, so the application can search.
    *   `SavedItineraryActivity`: Show all saved itineraries.
    *   `MapsApiClient`: contains functions to use Google Maps APIs.
    *   `SummaryActivity`: Show the city and all the restaurants and attractions selected by the application.
    *   `Helper`: Helper class to get user and iternityModel.
    *   `ThanksActivity`: Show a thanks message after finish the game.
    *   `DetailActivity`: Show a game, so the user can unlock a new
