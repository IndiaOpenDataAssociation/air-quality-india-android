package com.idn0phl3108ed43d22s30.pojo;

import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jimish on 4/7/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HourlyData {
    private static final String TAG = "HourlyData";

    @JsonProperty(ApiKeys.PAYLOAD_LABEL)
    private Payload payloadData;

    @JsonProperty(ApiKeys.MANAGE_DEVICE_ID)
    private String deviceId;

    @JsonProperty(ApiKeys.DEVICES_AQI)
    private double aqi;

    public HourlyData(Payload payloadData, String deviceId, double aqi) {
        this.payloadData = payloadData;
        this.deviceId = deviceId;
        this.aqi = aqi;
    }

    public HourlyData(){}

    public Payload getPayloadData() {
        return payloadData;
    }

    public void setPayloadData(Payload payloadData) {
        this.payloadData = payloadData;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getAqi() {
        return aqi;
    }

    public void setAqi(double aqi) {
        this.aqi = aqi;
    }
}
