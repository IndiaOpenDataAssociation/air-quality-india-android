package com.idn0phl3108ed43d22s30.pojo;

import android.util.Log;

import com.idn0phl3108ed43d22s30.etc.ApiKeys;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jimish on 29/6/16.
 */

public class ChangePassword {
    private static final String TAG = "ChangePassword";

    private String password;
    private String oldPassword;
    private String userId;

    public ChangePassword(String password, String oldPassword, String userId) {
        this.password = password;
        this.oldPassword = oldPassword;
        this.userId = userId;
    }

    public ChangePassword() {
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(ApiKeys.CHANGE_PASSWORD_NEW_PASS, this.getPassword());
            jsonObject.put(ApiKeys.CHANGE_PASSWORD_OLD_PASS, this.getOldPassword());
            jsonObject.put(ApiKeys.USER_ID_LABEL, this.getUserId());
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }
        return jsonObject;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
