package com.idn0phl3108ed43d22s30.Breath_i;

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
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.idn0phl3108ed43d22s30.AirOwlWi.AirOwlId;
import com.idn0phl3108ed43d22s30.AirOwlWi.WifiConnectAirOwl;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.Device;
import com.idn0phl3108ed43d22s30.services.WifiMonitorService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AddDeviceBreathi extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "AddDeviceBrathi";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Toolbar toolbar;
    private Button mbtnConnect;

    private EditText mBreathiSerial;
    public String serial = "";
    private WifiConfiguration conf;

    private TextView mInfo;

    private int flag = 1;
    private String networkSSID = "";
    private RelativeLayout mRelativeLayout;

    private List<Address> addresses;
    private GoogleApiClient googleApiClient;
    private ConnectionDetector connectionDetector;

    private String locLocal, city, country;
    private long latitude;
    private long longitude;
    private Location location;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_add_device_breathi);
        ConfigView();
        getToolbar();

        if (!checkPermission()) {
            requestPermission();
        } else {
            // TODO: 17-08-2016 else loop
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

    private void ConfigView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        mbtnConnect = (Button) findViewById(R.id.btnConnectBreathi);
        mbtnConnect.setOnClickListener(this);
        mBreathiSerial = (EditText) findViewById(R.id.breathiSerial);
        mInfo = (TextView) findViewById(R.id.LblBreathi);
        mInfo.setOnClickListener(this);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayoutAddDeviceBreathi);

    }

    private void getToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Devices");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mbtnConnect) {
            validate();
        }
    }

    private boolean isInternetCheck() {
        ConnectionDetector connectionDetector;
        String breathiId = ApiKeys.BREATHI_PRE_KEY + serial;
        connectionDetector = new ConnectionDetector(this);
        if (connectionDetector.isConnectingToInternet()) {
            if (breathiId.equals(getCurrentSSID())) {
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

    public void startNewctivity() {
        Intent breathIConnectWifi = new Intent(getApplicationContext(), AddDeviceWifiSelectBreathi.class);
        String _temp = mBreathiSerial.getText().toString();
        serial = _temp.trim().toLowerCase();
        Log.e(TAG, "Serial number in AddDeviceBreathi is :" + serial);
        breathIConnectWifi.putExtra(ApiKeys.MANAGE_DEVICE_ID, ApiKeys.BREATHI_PRE_KEY + serial);
        startActivity(breathIConnectWifi);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient != null) {
            googleApiClient.reconnect();
        }
    }

    private void validate() {
        String _temp = mBreathiSerial.getText().toString();
        serial = _temp.trim().toLowerCase();

        if (_temp == null || _temp.isEmpty()) {
            Snackbar.make(mRelativeLayout, R.string.errorAirOwlId, Snackbar.LENGTH_LONG).show();
        } else {
            if (isInternetCheck()) {
                if (isLocationEnabled(this)) {
                    if (location != null) {
                        fetchLocalAddress();
                        isInternetCheck();
                        registerDevice();
                    } else {
                        Snackbar.make(mRelativeLayout, R.string.fetchingLocation, Snackbar.LENGTH_LONG).show();
                        googleApiClient.reconnect();
                    }
                } else {
                    Snackbar.make(mRelativeLayout, R.string.errorLocation, Snackbar.LENGTH_LONG).show();
                    requestToTurnOnLocationServices();
                }
            } else {
                Snackbar.make(mRelativeLayout, R.string.errorInternet, Snackbar.LENGTH_LONG).show();
            }
        }
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
            locLocal = addresses.get(0).getLocality();
        } catch (IOException e) {
            Log.i(TAG, "IO Exception occurred with details : " + e.toString());
        }
    }

    // To show setOverlay on click ...
    private void setOverlay() {
        mInfo.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                showOverlay();
                mInfo.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;

            }
        });
    }

    //to show OverLay
    private void showOverlay() {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Dialog dialog = new Dialog(this, 0);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tutorial_breathi_screen1);

        RelativeLayout relOverlay = (RelativeLayout) dialog.findViewById(R.id.relOverlay);
        relOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDeviceBreathi.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private JSONObject getJsonData() {
        Device device = new Device();
        device.setLabel(locLocal);
        device.setLatitude(latitude);
        device.setLongitude(longitude);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ApiKeys.DEVICES_LABEL, locLocal);
            jsonObject.put(ApiKeys.DEVICES_LOC, locLocal);
            jsonObject.put(ApiKeys.DEVICES_CITY, city);
            jsonObject.put(ApiKeys.DEVICES_COUNTRY, country);
            jsonObject.put(ApiKeys.DEVICES_DEVICEID, ApiKeys.BREATHI_PRE_KEY + serial);
            jsonObject.put(ApiKeys.DEVICES_TYPE, ApiKeys.BREATH_I_TYPE);
            jsonObject.put(ApiKeys.DEVICES_PRIVATE, 1);
            jsonObject.put(ApiKeys.DEVICES_LATITUDE, latitude);
            jsonObject.put(ApiKeys.DEVICES_LONGITUDE, longitude);
            jsonObject.put(ApiKeys.DEVICES_SERIAL, ApiKeys.BREATH_I_TYPE + ":" + ApiKeys.BREATHI_PRE_KEY + serial);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        }
        return jsonObject;
    }

    private void snackBarError() {
        Snackbar.make(mRelativeLayout, R.string.errorGenral, Snackbar.LENGTH_LONG).show();
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

                        NetworkResponse response = error.networkResponse;
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

                                            Intent newMainScreen = new Intent(getApplicationContext(), AddDeviceWifiSelectBreathi.class);
                                            newMainScreen.putExtra(ApiKeys.MANAGE_DEVICE_ID, ApiKeys.BREATHI_PRE_KEY + serial);

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


    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);

        if (location != null) {
            if (loading != null) {
                loading.dismiss();
            }
            latitude = (long) location.getLatitude();
            longitude = (long) location.getLongitude();
            fetchLocalAddress();
        } else {
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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
