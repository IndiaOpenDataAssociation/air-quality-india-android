package com.idn0phl3108ed43d22s30.AirOwlWi;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.Device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AirOwlId extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "AirOwlID";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Toolbar toolbar;
    private TextView mLabel;
    private EditText mAirOwlId;
    private Button mBtnNext;
    private RelativeLayout mRelativeLayout;
    public String airOwlid = "";

    private double latitude;
    private double longitude;
    private Location location;

    List<Address> addresses;
    private GoogleApiClient googleApiClient;
    private ConnectionDetector connectionDetector;
    private ProgressDialog wifiConnectingLoader;

    private static SQLiteDatabaseHelper dbHelper;


    private ProgressDialog loading;
    private String locLocal, city, country;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_owl_id);

        progressDialog = new ProgressDialog(this);

        configView();
        clickEvent();
        setUpToolbar();

        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());

        // to take location permission..
        if (!checkPermission()) {
            requestPermission();
        }


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        connectionDetector = new ConnectionDetector(this);

    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Device");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void clickEvent() {
        mLabel.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
    }

    private void configView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relMainAirOwlId);
        mLabel = (TextView) findViewById(R.id.LblairOwlid);
        mAirOwlId = (EditText) findViewById(R.id.etairOwlid);
        String AirOwlId = mAirOwlId.getText().toString();

        mBtnNext = (Button) findViewById(R.id.btnNext);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnNext) {

            if (isInternetCheck()) {
                if (isLocationEnabled(this)) {
                    if (location != null) {
                        fetchLocalAddress();
                        Validate();
                    } else {
                        Snackbar.make(mRelativeLayout, R.string.fetchingLocation, Snackbar.LENGTH_LONG).show();

                        progressDialog.setMessage("Fetching Location...");
                        progressDialog.show();
                        googleApiClient.reconnect();
                    }
                } else {
                    Snackbar.make(mRelativeLayout, R.string.errorLocation, Snackbar.LENGTH_LONG).show();
                    requestToTurnOnLocationServices();
                }
            } else {
                Snackbar.make(mRelativeLayout, R.string.errorInternet, Snackbar.LENGTH_LONG).show();
            }
        } else if (v == mLabel) {
            setOverlay();
        }

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                Log.e(TAG, "settings not found exception occurred with details : " + e.toString());
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    private void Validate() {
        String _temp = mAirOwlId.getText().toString();
        airOwlid = _temp.trim().toLowerCase();

        if (_temp == null || _temp.isEmpty() || _temp.length() < 4) {
            Snackbar.make(mRelativeLayout, R.string.errorAirOwlId, Snackbar.LENGTH_LONG).show();
//            Toast.makeText(this, R.string.errorAirOwlId, Toast.LENGTH_LONG).show();
        } else {
            registerDevice();

        }
    }

    private void startNewctivity() {
        Intent newMainScreen = new Intent(getApplicationContext(), WifiConnectAirOwl.class);
        newMainScreen.putExtra(ApiKeys.MANAGE_DEVICE_ID, airOwlid);
        startActivity(newMainScreen);
    }


    private String getCurrentSSID() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        ssid = removeChar(ssid, 0);
        ssid = removeChar(ssid, ssid.length() - 1);
        return ssid;
    }


    public String removeChar(String str, Integer n) {
        String front = str.substring(0, n);
        String back = str.substring(n + 1, str.length());
        return front + back;
    }

    //get JSON data
    private JSONObject getJsonData() {
        Device device = new Device();
        device.setLabel(airOwlid);
        device.setLatitude((long) latitude);
        device.setLongitude((long) longitude);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ApiKeys.DEVICES_LABEL, device.getLabel());
            jsonObject.put(ApiKeys.DEVICES_LOC, locLocal);
            jsonObject.put(ApiKeys.DEVICES_CITY, city);
            jsonObject.put(ApiKeys.DEVICES_COUNTRY, country);
            jsonObject.put(ApiKeys.DEVICES_DEVICEID, ApiKeys.AIROWL_PRE_KEY + airOwlid);
            jsonObject.put(ApiKeys.DEVICES_TYPE, ApiKeys.AIROWL_WI_TYPE);
            jsonObject.put(ApiKeys.DEVICES_PRIVATE, 0);
            jsonObject.put(ApiKeys.DEVICES_LATITUDE, latitude);
            jsonObject.put(ApiKeys.DEVICES_LONGITUDE, longitude);
            jsonObject.put(ApiKeys.DEVICES_SERIAL, ApiKeys.AIROWL_WI_TYPE + ":" + ApiKeys.AIROWL_PRE_KEY + airOwlid);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        }
        return jsonObject;
    }

    private void fetchLocalAddress() {
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            Log.e(TAG, addresses.toString());
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            city = addresses.get(0).getLocality();
            country = addresses.get(0).getCountryName();
            locLocal = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            Log.i(TAG, "IO Exception occurred with details : " + e.toString());
        }
    }

    // TODO: 12-08-2016 change lollocality to sublocality
    // TODO: 12-08-2016 add handler to fetching data for 2 min and redirct to mian activity ..

    private void fetchDeviceArray() {
        ECAPIHelper.getDevicesListArray(this, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response != null) {
                        dbHelper.updatePrivateDataArray(response);
                        dbHelper.updateLatestPrivateObj(response.getJSONObject(0));
                        dbHelper.updateLatestPrivateObjDevieId(ApiKeys.AIROWL_PRE_KEY + airOwlid);
                    } else {
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error occurred with details : " + error.toString());
            }
        });
    }


    private void registerDevice() {

        final ProgressDialog loading = ProgressDialog.show(this, "Registering device...", "Please wait...", false, false);
        final JSONObject jsonObject = getJsonData();
        ECAPIHelper.registerDevice(this, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response != null) {
                                int status = 0;
                                status = response.getInt("status");
                                if (status == 201) {
                                    loading.dismiss();
                                    new Thread(new Runnable() {
                                        public void run() {
                                            fetchDeviceArray();
                                        }
                                    }).start();
                                    startNewctivity();
                                } else {
                                    Log.e(TAG, "Something went wrong:" + response.toString());
                                    loading.dismiss();
                                }
                            } else {
                                loading.dismiss();
                                Log.e(TAG, "Exception occurred with details :" + response.toString());
                            }
                        } catch (JSONException e) {
                            loading.dismiss();
                            Log.e(TAG, "JSON Exception occurred with details :" + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        final NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {

                            switch (response.statusCode) {

                                case 502:
                                    snackBarError();
                                    break;

                                case 500:
                                    Snackbar.make(mRelativeLayout, "Please check your net connection and try again !!", Snackbar.LENGTH_LONG).show();
                                    break;
                                case 409:
                                    Snackbar.make(mRelativeLayout, "Device already registered. Redirecting...", Snackbar.LENGTH_SHORT).show();
                                    //Thread for delay between two activity...
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            Intent newMainScreen = new Intent(getApplicationContext(), WifiConnectAirOwl.class);
                                            newMainScreen.putExtra(ApiKeys.MANAGE_DEVICE_ID, airOwlid);
                                            startActivity(newMainScreen);
                                        }
                                    }, 2000);
                                    break;
                                default:
                                    Snackbar.make(mRelativeLayout, "Server error .. Please try after sometimes..", Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                            loading.dismiss();

                        }
                        Log.e(TAG, "VOlley error occurred with details :" + error.toString());
                    }


                }

        );
    }

    private void snackBarError() {
        Snackbar.make(mRelativeLayout, R.string.errorGenral, Snackbar.LENGTH_LONG).show();
    }

    private void requestToTurnOnLocationServices() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.gps_not_found_title);  // GPS not found
        builder.setMessage(R.string.gps_not_found_message); // Want to enable?
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.create().show();
        return;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);

        if (location != null) {
            if (loading != null) {
                loading.dismiss();
            }
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            // fetchLocalAddress();
        } else {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                Snackbar.make(mRelativeLayout, "Location issue from device, please restart the app", Snackbar.LENGTH_LONG).show();
            }
            //googleApiClient.reconnect();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } else {
            //checkDataConnectionAndLocation();
        }

    }

    private boolean isInternetCheck() {
        ConnectionDetector connectionDetector;
        connectionDetector = new ConnectionDetector(this);
        if (connectionDetector.isConnectingToInternet()) {
            if (mAirOwlId.equals(getCurrentSSID())) {
                if (connectionDetector.isConnectingToMobileData()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean isLocationAvailable() {
        if (latitude == 0 && longitude == 0) {
            googleApiClient.reconnect();
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // To show setOverlay on click ...
    private void setOverlay() {
        mLabel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                showOverlay();
                mLabel.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;

            }
        });
    }

    private void showOverlay() {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Dialog dialog = new Dialog(this, 0);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tutorial_screen1);

        RelativeLayout relOverlay = (RelativeLayout) dialog.findViewById(R.id.relOverlay);
        relOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AirOwlId.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    // to check location permission

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }

    // to request permission

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(getApplicationContext(), "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Snackbar.make(mRelativeLayout, "Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();

                } else {

                    Snackbar.make(mRelativeLayout, "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

}
