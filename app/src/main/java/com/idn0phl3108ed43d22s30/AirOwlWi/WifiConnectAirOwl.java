package com.idn0phl3108ed43d22s30.AirOwlWi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Api;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.Ui.ManageDevice;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class WifiConnectAirOwl extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "WifiConnectAirOwl";
    public EditText mWifiName;
    public RelativeLayout mRelMain;
    public TextView mSkip, txtStep, mLblID;
    public Button mBtnNext;
    public Toolbar toolbar;
    public ShowHidePasswordEditText mWifiPassword;
    private String deviceId;
    private List<String> wifiSSIDs;

    private String[] wifiSSID;
    private ConnectionDetector connectionDetector;

    private static SQLiteDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect_air_owl);
        dbHelper = new SQLiteDatabaseHelper(this);
        connectionDetector = new ConnectionDetector(this);
        Bundle extras = getIntent().getExtras();
        wifiSSIDs = new ArrayList<String>();
        if (extras != null) {
            deviceId = extras.getString(ApiKeys.MANAGE_DEVICE_ID);
//            wifiSSID = extras.getStringArray(ApiKeys.SELECT_WIFI_SSIDS);
        }

        configView();
        clickEvent();
        setUpToolbar();
    }


    private String getCurrentSSID() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
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
        mBtnNext.setOnClickListener(this);
        mLblID.setOnClickListener(this);
        txtStep.setOnClickListener(this);
        mSkip.setOnClickListener(this);
    }

    private void configView() {
        Log.d(TAG, deviceId.toString());

        mWifiName = (EditText) findViewById(R.id.etwifiName);
        mWifiPassword = (ShowHidePasswordEditText) findViewById(R.id.etWifipassword);
        mWifiPassword.setImeActionLabel(getString(R.string.next), EditorInfo.IME_ACTION_DONE);
        mWifiPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = true;
                    Connect();
                }
                return handled;
            }
        });
        mLblID = (TextView) findViewById(R.id.LblairOwlid);
        mSkip = (TextView) findViewById(R.id.txtSkip);
        mSkip.setPaintFlags(mSkip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mBtnNext = (Button) findViewById(R.id.btnNext);
        txtStep = (TextView) findViewById(R.id.txtStep);
        mRelMain = (RelativeLayout) findViewById(R.id.relMain);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnNext) {
            Connect();
        } else if (v == mLblID) {
            setOverlay();
        } else if (v == txtStep) {
            setOverlay2();
        } else if (v == mSkip) {
            setSkipEvent();
        }
    }

    private void setSkipEvent() {
        if(dbHelper.readPrivateDataValue()!=null){
            fetchDeviceArray();
        }
        Intent skip = new Intent(getApplicationContext(), PrivateGlobalActivity.class);
        startActivity(skip);
        Log.e(TAG, "Skip button press");
    }

    private void fetchDeviceArray(){
        final ProgressDialog loading = ProgressDialog.show(this, "Fetching device...", "Please wait...", false, false);
        ECAPIHelper.getDevicesListArray(this, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    if (response != null) {
                        loading.dismiss();
                        dbHelper.updatePrivateDataArray(response);
                        dbHelper.updateLatestPrivateObj(response.getJSONObject(0));
                    } else {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    loading.dismiss();
                    Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error occurred with details : " + error.toString());
            }
        });
    }

    private void Connect() {
        String currentSSID = getCurrentSSID();
        currentSSID = removeChar(currentSSID, 0);
        currentSSID = removeChar(currentSSID, currentSSID.length() - 1);
        if (deviceId.equals(currentSSID)) {
            if (connectionDetector.isConnectingToMobileData()) {
                Snackbar.make(mRelMain, R.string.errorTurnoffMobileData, Snackbar.LENGTH_LONG).show();
            } else {
                sendTCPRequestToAirowl();
            }

        } else {
            sendTCPRequestToAirowl();
            Log.d(TAG, "error in TCP Requst");
            //  connectToSelectedNetwork(deviceId);
        }
    }

    public String removeChar(String str, Integer n){
        String front = str.substring(0, n);
        String back = str.substring(n + 1, str.length());
        return front + back;
    }

    public String getAIROWLData(){
        String ssid = mWifiName.getText().toString();
        String pass = mWifiPassword.getText().toString().trim();

        String returnString = Constants.HTTP_PRE + Constants.AIROWL_IP_ADDRESS + ":" + Constants.AIROWL_PORT_NUMBER +
                Constants.AIROWL_URL_SSID + ssid + Constants.AIROWL_URL_PASS + pass;
        return returnString;
    }


    public void sendTCPRequestToAirowl() {
        try {
            final ProgressDialog loading = ProgressDialog.show(this, "Sending data to Owl...", "Please wait...", false, false);

            ECAPIHelper.placeAirOwlRequest(getAIROWLData(), this, new Response.Listener<String>() {
                @Override
                @SuppressLint("LongLogTag")
                public void onResponse(String jsonObject) {
                    try {
                        if (jsonObject != null) {
                            loading.dismiss();
                            Log.i(TAG, "Airowl request sent.");
                            lastStep();
                        } else {
                            snakBarError();
                            loading.dismiss();
                            Log.d(TAG, "Get Consumer Info Returned Null");
                        }

                    } catch (Exception ex) {
                        snakBarError();
                        loading.dismiss();
                        Log.e(TAG, "Exception occurred with details : " + ex.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String error = volleyError.toString();
                    if (error.endsWith("ok")) {
                        loading.dismiss();
                        Log.i(TAG, "Airowl request sent.");
                        lastStep();
                    } else {
                        snakBarError();
                        loading.dismiss();
                        Log.d(TAG, "airowl request failed with volleyError " + volleyError.toString());
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void snakBarError() {
        Snackbar.make(mRelMain, R.string.errorUnableToSendData, Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Connect();
                    }
                })
                .setActionTextColor(Color.parseColor("#00b3bf"))
                .show();
    }

    private void lastStep() {
        Intent last = new Intent(this, AlmostThererAirOwl.class);
        last.putExtra(ApiKeys.MANAGE_DEVICE_ID, deviceId);
        startActivity(last);
    }

    private void setOverlay2() {

        txtStep.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                showOverlay2();
                txtStep.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;

            }


        });
    }

    private void showOverlay2() {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Dialog dialog = new Dialog(this, 0);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tutorial_screen3);


        RelativeLayout relOverlay = (RelativeLayout) dialog.findViewById(R.id.relOverlay);
        relOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiConnectAirOwl.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setOverlay() {
        txtStep.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                showOverlay();
                txtStep.getViewTreeObserver().removeOnPreDrawListener(this);
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
        dialog.setContentView(R.layout.tutorial_screen2);


        RelativeLayout relOverlay = (RelativeLayout) dialog.findViewById(R.id.relOverlay);
        relOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiConnectAirOwl.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
