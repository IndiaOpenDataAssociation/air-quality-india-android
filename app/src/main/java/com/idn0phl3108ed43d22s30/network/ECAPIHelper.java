package com.idn0phl3108ed43d22s30.network;

import android.content.Context;

import com.android.volley.Response;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Array;


public class ECAPIHelper {
    static final String TAG = ECAPIHelper.class.getSimpleName();

    public static void fetchPrivateDevices(Context context, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context);

        String urlMethod = "/" + userId + "/devices";
        manager.getJsonArray(urlMethod, listener, errorListener);
    }

    public static void placeAirOwlRequest(String url, Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);

        manager.getString(url, listener, errorListener);
    }

    public static void getDevicesList(Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = "/" + UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context) + "/devices";
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);

        manager.get(url, listener, errorListener);
    }

    public static void getDevicesListArray(Context context, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = "/" + UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context) + "/app/private/devices";

        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);

        manager.getJsonArray(url, listener, errorListener);
    }

    public static void signUpRequest(Context context, JSONObject jsonObject, Response.Listener<JSONObject> listener,
                                     Response.ErrorListener errorListener) {
        String urlMethod = "/signup";
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);

        manager.post(urlMethod, jsonObject, listener, errorListener);
    }

    // TODO: 30-08-2016 Device Data update

    public static void deviceData(Context context, JSONObject object, Response.Listener<JSONArray> listener,
                                  Response.ErrorListener errorListener) {
        String urlMethod = ApiKeys.PUBLIC_DEVICES_URL;
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        manager.postJsonArray(urlMethod, object, listener, errorListener);
    }

    public static void getCurrentData(String url, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);

        manager.get(url, listener, errorListener);
    }

    public static void login(JSONObject jsonObject, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);

        String urlMethod = ApiKeys.LOGIN_API;

        manager.post(urlMethod, jsonObject, listener, errorListener);

    }

    public static void fetchDeviceId(Context context, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);

        String urlMethod = ApiKeys.PUBLIC_DEVICES_URL;

        manager.publicGetJsonArray(urlMethod, listener, errorListener);
    }

    public static void fetchPublicAnalytics(String deviceId, Context context, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener){
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        String urlMethod = "/all/public/data/hours/24/"+deviceId;
        manager.publicGetJsonArray(urlMethod, listener, errorListener);
    }


    //Fetch Public analytic data for Weekly
    public static void fetchPublicAnalyticsWeekly(String deviceId, Context context, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener){
        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        String urlMethod = "/analytics/days/7/"+deviceId;
        manager.publicGetJsonArray(urlMethod, listener, errorListener);
    }

    //Private analytics for 24 hours
    public static void fetchHourlyAnalytics(String deviceId, Context context, Response.Listener<JSONArray> listener,
                                            Response.ErrorListener errorListener) {
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context);
        String urlMethod = "/" + userId + "/data/hours/24/" + deviceId;

        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        manager.getJsonArray(urlMethod, listener, errorListener);
    }

    //Public analytics for  weekly
    public static void fetchWeeklyAnalytics(String deviceId, Context context, Response.Listener<JSONArray> listener,
                                            Response.ErrorListener errorListener) {
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context);
        String urlMethod = "/" + userId + "/data/days/7/" + deviceId;

        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        manager.getJsonArray(urlMethod, listener, errorListener);
    }


    public static void registerDevice(Context context, JSONObject jsonObject,
                                      Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context);
        String urlMethod = "/" + userId + "/devices";

        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        manager.post(urlMethod, jsonObject, listener, errorListener);
    }

    public static void updateDevice(String deviceId, Context context, JSONObject jsonObject,
                                    Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context);
        String urlMethod = "/" + userId + "/devices/" + deviceId;

        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        manager.put(urlMethod, jsonObject, listener, errorListener);
    }

    public static void getUserData(Context context, Response.Listener<JSONArray> listener,
                                   Response.ErrorListener errorListener) {
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context);
        String urlMethod = "/users/" + userId;

        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        manager.getJsonArray(urlMethod, listener, errorListener);
    }

    public static void changePassword(Context context, JSONObject jsonObject, Response.Listener<JSONObject> listener,
                                      Response.ErrorListener errorListener) {
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context);
        String urlMethod = "/users/" + userId + "/chpass";

        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        manager.put(urlMethod, jsonObject, listener, errorListener);
    }



    public static void fetchPublicDeviceData(String deviceId, Context context, Response.Listener<JSONArray> listener,
                                            Response.ErrorListener errorListener) {
        String urlMethod = "/all/public/data/cur/"+deviceId;

        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        manager.publicGetJsonArray(urlMethod, listener, errorListener);
    }

    public static void fetchPrivateDeviceData(String deviceId, Context context, Response.Listener<JSONArray> listener,
                                             Response.ErrorListener errorListener) {
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", context);
        String urlMethod = "/" + userId + "/data/devices/" + deviceId;

        ECNetworkingManager manager = ECNetworkingManager.getInstance(context);
        manager.getJsonArray(urlMethod, listener, errorListener);
    }


}