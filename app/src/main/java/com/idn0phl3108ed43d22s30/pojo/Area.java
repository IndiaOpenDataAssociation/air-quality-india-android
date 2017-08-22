package com.idn0phl3108ed43d22s30.pojo;

/**
 * Created by jimish on 25/6/16.
 */

public class Area {

    private String area;
    private PublicDevices device;

    public Area(){

    }

    public Area(String area, PublicDevices device){
        this.area = area;
        this.device = device;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public PublicDevices getDevice() {
        return device;
    }

    public void setDevice(PublicDevices device) {
        this.device = device;
    }
}
