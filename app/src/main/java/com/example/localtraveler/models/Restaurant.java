package com.example.localtraveler.models;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String name;
    private String address;
    private String imageUrl;
    private String priceLevel;
    private String placeId;
    float rating;
    double lat;
    double lon;
    private String website;
    private String openingHours;
    private String postalCode;

    public Restaurant(String name, String address, String imageUrl, String priceLevel) {
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.priceLevel = priceLevel;
    }

    public Restaurant(String name, String address, String imageUrl, String priceLevel, float rating, double lat, double lon, String placeId, String postalCode) {
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.priceLevel = priceLevel;
        this.rating = rating;
        this.lat = lat;
        this.lon = lon;
        this.placeId = placeId;
        this.postalCode = postalCode;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(String priceLevel) {
        this.priceLevel = priceLevel;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
