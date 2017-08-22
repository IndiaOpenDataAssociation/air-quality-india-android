package com.idn0phl3108ed43d22s30.Ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.idn0phl3108ed43d22s30.AirOwlWi.AddDeviceAirOwlWifi;
import com.idn0phl3108ed43d22s30.AirOwlWi.WifiConnectAirOwl;
import com.idn0phl3108ed43d22s30.Breath_i.AddDeviceWifiSelectBreathi;
import com.idn0phl3108ed43d22s30.LoginRegister.LoginActivity;
import com.idn0phl3108ed43d22s30.MainActivity;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
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
import static android.view.View.GONE;


public class ManageDevice extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ManageDevice";

    private Toolbar toolbar;
    private EditText mDeviceName, mChooseWifi, mWifiPass;
    private Button mSavebtn, changeWifiSettings, locationBtn;
    private CheckBox mCheckBox;

    private String wifiSSID, wifiPass, deviceId, deviceType, deviceName, locLocal;


    private double latitude;
    private double longitude;
    private Location location;
    private String city;
    private String state;
    private String country;
    List<Address> addresses;

    private TextView txtLatitude;
    private TextView txtLongitude;

    private static SQLiteDatabaseHelper dbHelper;
    private boolean isPut;

    private ProgressDialog loading;

    private GoogleApiClient googleApiClient;

    private ConnectionDetector connectionDetector;
    private ProgressDialog wifiConnectingLoader;

    private WifiMonitorService wifiMonitorService;
    private List<ScanResult> wifiScanResults;
    private WifiConfiguration conf;
    private String networkSSID = "";

    private RelativeLayout mRelativeLayout;

    private int WEP_SHARED_PREF_KEY = 99239372;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);
        Bundle extras = getIntent().getExtras();

        dbHelper = new SQLiteDatabaseHelper(this);
        getToolbar();
        ConfigView();
        if (extras != null) {
            deviceId = extras.getString(ApiKeys.MANAGE_DEVICE_ID);
            deviceName = extras.getString(ApiKeys.MANAGE_DEVICE_NAME);

            isPut = true;
            mDeviceName.setText(deviceName);

            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

            }

            connectionDetector = new ConnectionDetector(this);
        }
    }

    private boolean isInternetCheck() {
        ConnectionDetector connectionDetector;
        connectionDetector = new ConnectionDetector(this);
        if (connectionDetector.isConnectingToInternet()) {
            return true;
        } else {
            return false;
        }
    }


    private void showNoInternetToast() {
        Snackbar.make(mRelativeLayout, R.string.errorInternet, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    private void ConfigView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayoutManageDevice);
        mDeviceName = (EditText) findViewById(R.id.etDeviceName);
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        mSavebtn = (Button) findViewById(R.id.btnSave);
        changeWifiSettings = (Button) findViewById(R.id.btnConfigure);
        changeWifiSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWifiSettingsScreen();
            }
        });
        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetCheck()) {
                    if (isLocationEnabled(getApplicationContext())) {
                        if (location != null) {
                            fetchLocalAddress();
                            Validate();
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
        });
    }

    private void updateLatestObj(JSONObject jsonObject) {
        JSONObject dbObj = dbHelper.readLatestPrivateObj();
        String key;
        try {
            for (int i = 0; i < jsonObject.names().length(); i++) {
                key = jsonObject.names().getString(i);
                dbObj.put(key, jsonObject.get(key));
            }
            dbHelper.updateLatestPrivateObj(dbObj);
            dbHelper.updatePrivateData(dbObj);
            Snackbar.make(mRelativeLayout, R.string.updatedDevice, Snackbar.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startPrivateActivity();
                }
            }, 3000);

        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        }

    }

    private void startWifiSettingsScreen() {
        Intent intent = new Intent(this, WifiConnectAirOwl.class);
        intent.putExtra(ApiKeys.DEVICES_DEVICEID, deviceId);
        startActivity(intent);
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
        String _temp = mDeviceName.getText().toString();

        if (_temp == null || _temp.isEmpty() || _temp.length() < 4) {
            Snackbar.make(mRelativeLayout, R.string.blankDeviceName, Snackbar.LENGTH_LONG).show();
        } else {
            updateDevice();

        }
    }


    private JSONObject getJsonData() {
        Device device = new Device();
        device.setLabel(mDeviceName.getText().toString());
        device.setLatitude((long) latitude);
        device.setLongitude((long) longitude);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ApiKeys.DEVICES_LABEL, device.getLabel());
            jsonObject.put(ApiKeys.DEVICES_LOC, locLocal);
            jsonObject.put(ApiKeys.DEVICES_CITY, city);
            jsonObject.put(ApiKeys.DEVICES_COUNTRY, country);
            jsonObject.put(ApiKeys.DEVICES_DEVICEID, deviceId);
            jsonObject.put(ApiKeys.DEVICES_LATITUDE, latitude);
            jsonObject.put(ApiKeys.DEVICES_LONGITUDE, longitude);
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

    private void updateDevice() {
        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);
        JSONObject jsonObject = getJsonData();
        ECAPIHelper.updateDevice(deviceId, this, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    if (response.has("deviceId")) {
                        updateLatestObj(response);
                        loading.dismiss();
                    } else {
                        loading.dismiss();
                        Snackbar.make(mRelativeLayout, R.string.failedUpdateDevice, Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    loading.dismiss();
                    Log.e(TAG, "Exception occurred with details :" + response.toString());
                    Snackbar.make(mRelativeLayout, R.string.failedUpdateDevice, Snackbar.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.e(TAG, "Volley error occurred with details :" + error.toString());
                Snackbar.make(mRelativeLayout, R.string.failedUpdateDevice, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void startPrivateActivity() {
        Intent intent = new Intent(this, PrivateGlobalActivity.class);
        startActivity(intent);
    }

    private void getToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Device");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            txtLatitude.setText(String.valueOf(location.getLatitude()));
            txtLongitude.setText(String.valueOf(location.getLongitude()));
            fetchLocalAddress();
        } else {
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}