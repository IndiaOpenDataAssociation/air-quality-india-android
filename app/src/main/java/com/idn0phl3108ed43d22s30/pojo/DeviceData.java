package com.idn0phl3108ed43d22s30.pojo;

/**
 * Created by jimish on 27/6/16.
 */

public class DeviceData {
    private static final String TAG = "DeviceData";

    private String deviceId;
    private int aqi;
    private String status;

    public DeviceData(String deviceId, int aqi, String status) {
        this.deviceId = deviceId;
        this.aqi = aqi;
        this.status = status;
    }

    public DeviceData(){}

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
