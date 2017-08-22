package com.idn0phl3108ed43d22s30.pojo;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jimish on 28/6/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicDevices {

    private static final String TAG = "PUblicDevice";

    @JsonProperty(ApiKeys.DEVICES_LOC)
    private String loc;

    @JsonProperty(ApiKeys.DEVICES_TYPE)
    private String type;

    @JsonProperty(ApiKeys.DEVICES_LABEL)
    private String label;

    @JsonProperty(ApiKeys.DEVICES_DEVICEID)
    private String deviceId;

    @JsonProperty(ApiKeys.DEVICES_LATITUDE)
    private long latitude;

    @JsonProperty(ApiKeys.DEVICES_LONGITUDE)
    private long longitude;

    @JsonProperty(ApiKeys.DEVICES_CITY)
    private String city;

    @JsonProperty(ApiKeys.DEVICES_COUNTRY)
    private String country;

    @JsonProperty(ApiKeys.DEVICES_AQI)
    private int aqi;

    @JsonProperty(ApiKeys.DEVICES_TIME)
    private long time;

    @JsonProperty(ApiKeys.DEVICES_TEMP)
    private int temp;

    @JsonProperty(ApiKeys.DEVICES_HUM)
    private int hum;

    @JsonProperty(ApiKeys.PAYLOAD_LABEL)
    private Payload payload;


    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(ApiKeys.DEVICES_LOC, this.getLoc());
            jsonObject.put(ApiKeys.DEVICES_TYPE, this.getType());
            jsonObject.put(ApiKeys.DEVICES_LATITUDE, this.getLatitude());
            jsonObject.put(ApiKeys.DEVICES_LATITUDE, this.getLongitude());
            jsonObject.put(ApiKeys.DEVICES_DEVICEID, this.getDeviceId());
            jsonObject.put(ApiKeys.DEVICES_LABEL, this.getLabel());
            jsonObject.put(ApiKeys.DEVICES_PRIVATE, 0);
            jsonObject.put(ApiKeys.DEVICES_AQI, this.getAqi());
            jsonObject.put(ApiKeys.DEVICES_TIME, this.getTime());
            jsonObject.put(ApiKeys.DEVICES_COUNTRY, this.getCountry());
            jsonObject.put(ApiKeys.DEVICES_CITY, this.getCity());
            jsonObject.put(ApiKeys.DEVICES_TEMP, this.getTemp());
            jsonObject.put(ApiKeys.DEVICES_HUM, this.getHum());
            jsonObject.put(ApiKeys.DEVICES_SERIAL, this.getType()+":"+this.getDeviceId());
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }
        return jsonObject;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getHum() {
        return hum;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

    public PublicDevices(){}

    public PublicDevices(String loc, String type, String label, String deviceId, long latitude, long longitude, String city,
                   String country, int aqi, long time){
        this.loc = loc;
        this.type = type;
        this.label = label;
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.country = country;
        this.aqi = aqi;
        this.time = time;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

