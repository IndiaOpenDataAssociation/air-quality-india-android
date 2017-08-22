package com.idn0phl3108ed43d22s30.pojo;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jimish on 25/6/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device implements ClusterItem {

    private static final String TAG = "Device";
    private LatLng mPosition;

    @JsonProperty(ApiKeys.DEVICES_LOC)
    private String loc;

    @JsonProperty(ApiKeys.DEVICES_TYPE)
    private String type;

    @JsonProperty(ApiKeys.DEVICES_LABEL)
    private String label;

    @JsonProperty(ApiKeys.DEVICES_DEVICEID)
    private String deviceId;

    @JsonProperty(ApiKeys.DEVICES_LATITUDE)
    private double latitude;

    @JsonProperty(ApiKeys.DEVICES_LONGITUDE)
    private double longitude;

    @JsonProperty(ApiKeys.DEVICES_CITY)
    private String city;

//    @JsonProperty(ApiKeys.DEVICES_STATE)
//    private String state;

    @JsonProperty(ApiKeys.DEVICES_COUNTRY)
    private String country;

    @JsonProperty(ApiKeys.DEVICES_AQI)
    private int aqi;

    @JsonProperty(ApiKeys.DEVICES_TIME)
    private long time;

    @JsonProperty(ApiKeys.DEVICES_PRIVATE)
    private int privateFlag;

    @JsonProperty(ApiKeys.DEVICES_TEMP)
    private int temp;

    @JsonProperty(ApiKeys.DEVICES_HUM)
    private int hum;

    @JsonProperty(ApiKeys.PAYLOAD_LABEL)
    private Payload payload;


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

    public Device(String deviceId) {
        this.deviceId = deviceId;
    }

    public JSONObject toObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ApiKeys.DEVICES_DEVICEID, this.getDeviceId());

        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        }
        return jsonObject;
    }

    public Device() {

    }

    //For cluster
    public Device(double latitude, double longitude, String city, int aqi, String label) {
        mPosition = new LatLng(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.aqi = aqi;
        this.label = label;
    }

    public Device(String loc, String type, String label, String deviceId, double latitude, double longitude, String city,
                  String state, String country, int aqi, long time, int privateFlag) {
        this.loc = loc;
        this.type = type;
        this.label = label;
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        //this.state = state;
        this.country = country;
        this.aqi = aqi;
        this.time = time;
        this.privateFlag = privateFlag;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }

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

    public int getPrivateFlag() {
        return privateFlag;
    }

    public void setPrivateFlag(int privateFlag) {
        this.privateFlag = privateFlag;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }


    public String dateFormat() {
        Date date = new Date(getTime() * 1000);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        String timeString = simpleDateFormat.format(date);
        return timeString;
    }

    public String dateFormateSmall() {
        Date date = new Date(getTime() * 1000);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE , d MMM HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        String timeString = simpleDateFormat.format(date);
        return timeString;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
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
            jsonObject.put(ApiKeys.DEVICES_SERIAL, this.getType() + ":" + this.getDeviceId());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        }
        return jsonObject;
    }



    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}