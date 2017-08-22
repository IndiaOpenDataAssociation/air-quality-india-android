package com.idn0phl3108ed43d22s30.pojo;

import android.util.Log;

import com.idn0phl3108ed43d22s30.etc.ApiKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimish on 21/6/16.
 */

public class Login {
    private static final String TAG = "LOGIN";
    private String email;
    private String password;

    public Login(){}

    public Login(String email, String password){
        this.email = email;
        this.password = password;
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(ApiKeys.USERNAME_LABEL, this.getEmail());
            jsonObject.put(ApiKeys.EMAIL_LABEL, this.getEmail());
            jsonObject.put(ApiKeys.PASSWORD_LABEL, this.getPassword());
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }
        return jsonObject;
    }

    public Map<String, String> getInParams(){
        Map<String, String> params = new HashMap<String, String>();
        params.put(ApiKeys.EMAIL_LABEL, this.getEmail());
        params.put(ApiKeys.PASSWORD_LABEL, this.getPassword());
        return params;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
