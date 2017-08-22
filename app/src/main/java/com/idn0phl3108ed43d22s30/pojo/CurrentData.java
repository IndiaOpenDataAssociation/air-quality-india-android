package com.idn0phl3108ed43d22s30.pojo;

import android.util.Log;

import com.idn0phl3108ed43d22s30.etc.ApiKeys;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jimish on 27/6/16.
 */

public class CurrentData {
    private static final String TAG = "CurrentData";
    private String[] devices;
    private String userId;

    public CurrentData(String[] devices, String userId) {
        this.devices = devices;
        this.userId = userId;
    }

    public CurrentData(){}

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(ApiKeys.CUR_DATA_DEVICES, this.getDevices());
            //jsonObject.put(ApiKeys.CUR_DATA_USERID, this.getUserId());
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }
        return jsonObject;
    }

    public String[] getDevices() {
        return devices;
    }

    public void setDevices(String[] devices) {
        this.devices = devices;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
