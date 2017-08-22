package com.idn0phl3108ed43d22s30.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimish on 25/6/16.
 */

public class City {

    private String city;
    private List<Area> areas;

    public City(){}

    public City(String city){
        this.city = city;
        this.areas = new ArrayList<Area>();
    }

    public City(String city, List<Area> areas) {
        this.areas = areas;
        this.city = city;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
