package com.example.localtraveler.models;

import java.util.List;

public class IternityModel {
    long id;
    String cityName, startDate, endDate;
    List<Restaurant> restaurantList;
    List<Restaurant> venueLists;
    long userId;

    public IternityModel(String cityName, String startDate, String endDate, List<Restaurant> restaurantList, List<Restaurant> venueLists) {
        this.cityName = cityName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.restaurantList = restaurantList;
        this.venueLists = venueLists;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public List<Restaurant> getVenueLists() {
        return venueLists;
    }

    public void setVenueLists(List<Restaurant> venueLists) {
        this.venueLists = venueLists;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
