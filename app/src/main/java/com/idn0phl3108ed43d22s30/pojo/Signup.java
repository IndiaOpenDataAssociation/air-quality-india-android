package com.idn0phl3108ed43d22s30.pojo;

import android.util.Log;

import com.idn0phl3108ed43d22s30.etc.ApiKeys;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jimish on 30/6/16.
 */

public class Signup {
    private static final String TAG = "Signup";

    private String email;
    private String password;
    private String username;

    public Signup(String email, String password) {
        this.email = email;
        this.password = password;
        this.username = email;
    }

    public Signup() {
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(ApiKeys.EMAIL_LABEL, this.getEmail());
            jsonObject.put(ApiKeys.PASSWORD_LABEL, this.getPassword());
            jsonObject.put(ApiKeys.USERNAME_LABEL, this.getUsername());
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }
        return jsonObject;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
