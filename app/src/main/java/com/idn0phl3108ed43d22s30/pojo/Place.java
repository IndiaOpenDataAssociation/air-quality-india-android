package com.idn0phl3108ed43d22s30.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimish on 25/6/16.
 */

public class Place {
    private static final String TAG = "Place";

    private String country;
    private List<City> cities;

    public Place(String country, List<City> cities) {
        this.country = country;
        this.cities = cities;
    }

    public Place(String country){
        this.country = country;
        this.cities = new ArrayList<City>();
    }

    public Place(){}

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<City> getCities() {
        return cities;
    }



    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
