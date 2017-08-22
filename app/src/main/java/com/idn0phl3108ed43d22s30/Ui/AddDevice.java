package com.idn0phl3108ed43d22s30.Ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idn0phl3108ed43d22s30.Polludrone.AddDevicePolludrone;
import com.idn0phl3108ed43d22s30.AirOwlCell.AddDeviceAirOwlCell;
import com.idn0phl3108ed43d22s30.LoginRegister.LoginActivity;
import com.idn0phl3108ed43d22s30.MainActivity;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.pojo.PublicDevices;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class AddDevice extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddDevice";

    private Toolbar toolbar;
    private ImageView polludron, airOwl3G, airOwlWi, breathI;

    private String selectedDeviceType;
    private SharedPreferences sharedPreferences;

    private RelativeLayout mRelativeLayout;

    private static SQLiteDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());
        initConfigView();
        OnClick();
        getToolbar();
    }

    private void getToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Devices");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String privateData = dbHelper.readPrivateDataValue().toString();
                if (privateData == null && (privateData.isEmpty())) {
                    finish(); //If user is login first time and press back button without adding device then app will be closed
                } else {
                    goToDashBoard();
                }
            }
        });
    }

    private void OnClick() {
        breathI.setOnClickListener(this);
        airOwl3G.setOnClickListener(this);
        polludron.setOnClickListener(this);
        airOwlWi.setOnClickListener(this);
    }

    private void initConfigView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        breathI = (ImageView) findViewById(R.id.imgBreath_i);
        airOwlWi = (ImageView) findViewById(R.id.imgairOwlWifi);
        polludron = (ImageView) findViewById(R.id.imgPolludrone);
        airOwl3G = (ImageView) findViewById(R.id.imgairOwlCell);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayoutAddDevices);

    }

    private void startDeviceActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v == breathI) {
            intent = new Intent(this, BreathiWelcomeScreen.class);
            // intent = new Intent(this, AddDeviceBreathi.class);
            selectedDeviceType = ApiKeys.BREATH_I_TYPE;

        } else if (v == airOwl3G) {
            intent = new Intent(this, AddDeviceAirOwlCell.class);
            selectedDeviceType = ApiKeys.AIROWL_3G_TYPE;
        } else if (v == airOwlWi) {
            if (isWifiOn()) {
                intent = new Intent(this, TutorialAirOwlWelcome.class);
                selectedDeviceType = ApiKeys.AIROWL_WI_TYPE;

            } else {
                intent = null;
                selectedDeviceType = ApiKeys.AIROWL_WI_TYPE;
            }

        } else if (v == polludron) {
            intent = new Intent(this, AddDevicePolludrone.class);
            selectedDeviceType = ApiKeys.POLLUDRON_TYPE;
        } else {
            intent = null;
        }
        if (checkLoggedIn()) {
            if (selectedDeviceType.equals(ApiKeys.AIROWL_WI_TYPE) && intent == null) {
                Snackbar.make(mRelativeLayout, "Please turn your wifi on!", Snackbar.LENGTH_LONG).show();
//                Toast.makeText(this, "Please turn your wifi on!", Toast.LENGTH_SHORT).show();
            } else {
                startDeviceActivity(intent);
            }

        } else {
            startLoginActivity();
        }
    }

    private boolean checkLoggedIn() {
        sharedPreferences = this.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean loggedIn = sharedPreferences.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false);
        return loggedIn;
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(ApiKeys.SHARED_DEVICE_TYPE, selectedDeviceType);
        startActivity(intent);
    }

    private boolean isWifiOn() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (UserPrefrenceUtils.isLoggedIn(this)) {
            goToDashBoard();
        } else {

        }
    }

    private void goToDashBoard() {
        Intent intent;

        String privateData = dbHelper.readPrivateDataValue().toString();

        if (privateData != null && !(privateData.isEmpty())) {
            intent = new Intent(this, PrivateGlobalActivity.class);
        } else {
            UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, null, getApplicationContext());
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }

}
