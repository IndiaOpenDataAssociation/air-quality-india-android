package com.idn0phl3108ed43d22s30.etc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.pojo.Device;
import com.idn0phl3108ed43d22s30.pojo.PublicDevices;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserPrefrenceUtils {

    private static final String NOTIFICATION_KEY = "";

    private static final String PREFS_NAME = "OIZOM_APP_PREFS";
    private static final String USER_PRES_NAME = "OIZOM_USER_PREFS";
    private static final String TAG = "UserPreferenceUtils";

    private static final String DEVICES_PREFS_NAME = "devicesPreferences";
    private static final String DEVICES_ARRAY = "globalDevicesArray";
    private static final String DEVICE_NOTIFICATION_PREFERENCE = "deviceNotification";


    public static JSONArray getDevicesArray(Context context) {
        SharedPreferences settings = context.getSharedPreferences(DEVICES_PREFS_NAME, Context.MODE_PRIVATE);
        try {
            String deviceArray = settings.getString(DEVICES_ARRAY, null);
            if (deviceArray != null) {
                JSONArray jsonArray = new JSONArray(deviceArray);
                return jsonArray;
            } else {
                return null;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    public static void updateCurrentDeviceArray(JSONArray jsonArray, Context context) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = context.getSharedPreferences(DEVICES_PREFS_NAME, Context.MODE_PRIVATE);

        editor = settings.edit();
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (jsonArray != null) {
                if (jsonArray.length() > 0) {
                    //   List<Device> devices = new ArrayList<Device>();
                    JSONArray currentDevices = getDevicesArray(context);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        for (int j = 0; j < currentDevices.length(); j++) {
                            Device toCheckDevice = mapper.readValue(jsonArray.get(i).toString(), Device.class);
                            Device toCheckWithDevice = mapper.readValue(currentDevices.get(j).toString(), Device.class);
                            if (toCheckWithDevice.getDeviceId().equals(toCheckDevice.getDeviceId())) {
                                currentDevices.put(j, jsonArray);
                            }
                        }
                    }
                    editor.putString(DEVICES_ARRAY, currentDevices.toString());
                    editor.commit();
                } else {
                    Toast.makeText(context, "Problem ", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JsonParseException e) {
            Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        } catch (JsonMappingException e) {
            Log.e(TAG, "JSON Mapping Exception occurred with details current  : " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "IO Excpetion occurred with details : " + e.toString());
        }
    }

    public static void saveUserIdToLoginPrefs(String userId, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.LOGGEDIN_SHARED_PREF, true);
        editor.putString(Constants.SHARED_ID, userId);
        editor.commit();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false);
    }

    public static void seekBarValue(int Progress, Context context) {
        SharedPreferences.Editor editor;
        SharedPreferences saveValue = context.getSharedPreferences(DEVICE_NOTIFICATION_PREFERENCE, Context.MODE_PRIVATE);

        editor = saveValue.edit();
        editor.putInt(NOTIFICATION_KEY, Progress);
        editor.commit();

    }

    public static void addDeviceToArray(JSONObject jsonObject, Context context) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = context.getSharedPreferences(DEVICES_PREFS_NAME, Context.MODE_PRIVATE);

        editor = settings.edit();
        try {
            JSONArray jsonArray;
            String devicesArrayStr = settings.getString(DEVICES_ARRAY, null);
            if (devicesArrayStr != null) {
                jsonArray = new JSONArray(devicesArrayStr);
                if (jsonArray.length() > 0) {
                    jsonArray.put(jsonObject);
                } else {
                    jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                }
            } else {
                jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
            }
            editor.putString(DEVICES_ARRAY, jsonArray.toString());
            editor.commit();
        } catch (JSONException e) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            editor.putString(DEVICES_ARRAY, jsonArray.toString());
            editor.commit();
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        }
    }

    public static boolean deviceDelete(JSONObject jsonObject, Context context) {
        SQLiteDatabaseHelper dbHelper = null;
        SharedPreferences.Editor editor = null;
        SharedPreferences settings = context.getSharedPreferences(DEVICES_PREFS_NAME, Context.MODE_PRIVATE);
        int flag = 1;
        int temp = -1;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String devicesArrayStr = settings.getString(DEVICES_ARRAY, null);
            if (devicesArrayStr != null) {
                JSONArray jsonArray = new JSONArray(devicesArrayStr);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray innerJsonArray = jsonArray.getJSONArray(i);
                        Log.i(TAG, "JSONArray" + innerJsonArray.toString());
                        PublicDevices device = mapper.readValue(jsonArray.get(i).toString(), PublicDevices.class);
                        if (device.getDeviceId().equals(jsonObject.getString("deviceId"))) {
                            JSONObject jo = innerJsonArray.getJSONObject(0);
                            flag = 0;
                            temp = i;
                            dbHelper.updateUserValue(jo);
                        }
                    }
                    if (temp != -1) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            jsonArray.remove(temp);
                            editor = settings.edit();
                            editor.putString(DEVICES_ARRAY, jsonArray.toString());

                            editor.commit();
                            JSONObject jo = jsonArray.getJSONObject(0);
                            dbHelper.updateUserValue(jo);
                        }
                    }
                    if (flag == 1) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with detail " + e.toString());
            return false;
        } catch (JsonParseException e) {
            Log.e(TAG, "JSON Parse Exception occurred with detail " + e.toString());
            return false;
        } catch (JsonMappingException e) {
            Log.e(TAG, "JSON Mapping Exception occurred with detail " + e.toString());
            return false;
        } catch (IOException e) {
            Log.e(TAG, "JSON IOException occurred with detail " + e.toString());
            return false;
        }
    }

    public static boolean deviceRegistered(JSONObject jsonObject, Context context) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = context.getSharedPreferences(DEVICES_PREFS_NAME, Context.MODE_PRIVATE);
        int flag = 1;

        try {
            ObjectMapper mapper = new ObjectMapper();
            String devicesArrayStr = settings.getString(DEVICES_ARRAY, null);
            if (devicesArrayStr != null) {
                JSONArray jsonArray = new JSONArray(devicesArrayStr);
                if (jsonArray.length() > 0) {
                    List<PublicDevices> devices = new ArrayList<PublicDevices>();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        PublicDevices device = mapper.readValue(jsonArray.get(i).toString(), PublicDevices.class);
                        if (device.getDeviceId().equals(jsonObject.getString("deviceId"))) {
                            flag = 0;
                        }
                    }
                    if (flag == 1) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (JSONException e) {
            return false;
        } catch (JsonParseException e) {
            return false;
        } catch (JsonMappingException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

    }

    public static ArrayList<JSONObject> getJsonArray(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        //Get Arraylist from Shared Preference
        ArrayList<JSONObject> pendingObjects = new ArrayList<JSONObject>();
        ObjectMapper mapper = new ObjectMapper();
        String requestString = settings.getString(key, null);
        if (requestString != null) {
            try {
                pendingObjects =
                        mapper.readValue(requestString, mapper.getTypeFactory().constructCollectionType(ArrayList.class, JSONObject.class));
            } catch (IOException e) {
                Log.e(TAG, "IOException occurred with detail: " + e.toString());
            }
        }

        return pendingObjects;
    }

    public static void putJsonArray(ArrayList<JSONObject> jsonObjectArrayList, String key, Context context) {
        //Store in Shared Preference
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        ObjectMapper mapper = new ObjectMapper();
        JSONArray jsonObjectJSONArray = new JSONArray(jsonObjectArrayList);
        try {
            editor.putString(key, mapper.writeValueAsString(jsonObjectJSONArray.toString()));
            editor.commit();
        } catch (JsonProcessingException e) {
            Log.e(TAG, "JsonProcessingException occurred with detail: " + e.toString());
        }
    }

    public static ArrayList<String> getStringArray(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        //Get Arraylist from Shared Preference
        ArrayList<String> pendingObjects = new ArrayList<String>();
        ObjectMapper mapper = new ObjectMapper();
        String requestString = settings.getString(key, null);
        if (requestString != null) {
            try {
                pendingObjects =
                        mapper.readValue(requestString, mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class));
            } catch (IOException e) {
                Log.e(TAG, "IOException occurred with detail: " + e.toString());
            }
        }

        return pendingObjects;
    }

    public static void putStringArray(ArrayList<String> arrayList, String key, Context context) {
        //Store in Shared Preference
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String value = mapper.writeValueAsString(arrayList);
            editor.putString(key, value);
            editor.commit();
        } catch (JsonProcessingException e) {
            Log.e(TAG, "JsonProcessingException occurred with detail: " + e.toString());
        }
    }

    public static void remove(String key, Context context) {
        //Remove from Shared Preference
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void putJsonValue(String key, String value, Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public static void saveValueToUserSharedPrefs(String key, String value, Context context) {
        SharedPreferences userSettings;
        SharedPreferences.Editor editor;

        userSettings = context.getSharedPreferences(USER_PRES_NAME, Context.MODE_PRIVATE);
        editor = userSettings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValueFromUserSharedPrefs(String key, Context context) {
        SharedPreferences userSettings;

        userSettings = context.getSharedPreferences(USER_PRES_NAME, Context.MODE_PRIVATE);
        String returnString = userSettings.getString(key, null);

        return returnString;
    }

    public static String getJsonValue(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        ArrayList<JSONObject> pendingObjects = new ArrayList<JSONObject>();
        ObjectMapper mapper = new ObjectMapper();
        String requestString = settings.getString(key, null);
        return requestString;
    }
}
