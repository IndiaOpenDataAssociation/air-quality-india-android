package com.idn0phl3108ed43d22s30.Breath_i;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.Ui.ManageDevice;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.services.WifiMonitorService;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceWifiSelectBreathi extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddDeviceWifiSelectBreathi.class.getSimpleName();

    private static final String OPENED_FROM = "openedFrom";

    private Toolbar toolbar;
    private Button mbtnConnect;
    private TextView mtxtSkip, LblairOwlid, txtStep;

    private RelativeLayout mRelativeLayout;
    private ImageView mImg;

    private Spinner airOwlWifiSelect;

    private String deviceId;

    private EditText breathiWifiSSID;
    private EditText airOwlWifiPass;

    private List<String> wifiSSIDs;

    private String[] wifiSSID;
    private ConnectionDetector connectionDetector;

    private WifiMonitorService wifiMonitorService;
    private List<ScanResult> wifiScanResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_wifi_select_breathi);

        connectionDetector = new ConnectionDetector(this);
        Bundle extras = getIntent().getExtras();
        wifiSSIDs = new ArrayList<String>();

        if (extras != null) {
            deviceId = extras.getString(ApiKeys.MANAGE_DEVICE_ID);
            Log.e(TAG, "Serial number of Breath-i" + deviceId.toString());
            // wifiSSID = extras.getStringArray(ApiKeys.SELECT_WIFI_SSIDS);
        }
        wifiMonitorService = new WifiMonitorService(this);
        ConfigView();
        getToolbar();
        //  getScannedWifiAddresses();
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

    private String getCurrentSSID() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    private void ConfigView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        mbtnConnect = (Button) findViewById(R.id.btnConnectBreathi);
        mbtnConnect.setOnClickListener(this);
//        airOwlWifiSelect = (Spinner) findViewById(R.id.breathiWifiSelect);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayoutBreathi);
        breathiWifiSSID = (EditText) findViewById(R.id.breathiWifiSSID);
        mImg = (ImageView) findViewById(R.id.wifiImg);
        mtxtSkip = (TextView) findViewById(R.id.txtSkipBreathi);
        mtxtSkip.setPaintFlags(mtxtSkip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mtxtSkip.setOnClickListener(this);
        //airOwlWifiSSID = (EditText) findViewById(R.id.airOwlWifiSSID);
        txtStep = (TextView) findViewById(R.id.txtStep);
        txtStep.setOnClickListener(this);
        LblairOwlid = (TextView) findViewById(R.id.LblairOwlid);
        LblairOwlid.setOnClickListener(this);
        airOwlWifiPass = (EditText) findViewById(R.id.breathiWifiPass);
    }

    private void Connect() {
        String currentSSID = getCurrentSSID();
        currentSSID = removeChar(currentSSID, 0);
        currentSSID = removeChar(currentSSID, currentSSID.length() - 1);
        if (deviceId.equals(currentSSID)) {
            if (connectionDetector.isConnectingToMobileData()) {
                Snackbar.make(mRelativeLayout, "Please turn off your mobile data and try again!", Snackbar.LENGTH_LONG).show();
            } else {
                sendTCPRequestToBreathi();
            }

        } else {
            Snackbar.make(mRelativeLayout, "Unable to send data", Snackbar.LENGTH_LONG).show();
            Log.d(TAG, "Get Consumer Info Returned Null");
            // connectToSelectedNetwork(deviceId);
        }
    }

    public String removeChar(String str, Integer n) {
        String front = str.substring(0, n);
        String back = str.substring(n + 1, str.length());
        return front + back;
    }

    @Override
    public void onClick(View v) {
        if (v == mbtnConnect) {
            Connect();
        } else if (v == mtxtSkip) {
            skipEvent();
        } else if (v == txtStep) {
            setOverlay();
        } else if (v == LblairOwlid) {
            setOverlay2();
        }
    }

    private void skipEvent() {
        // TODO: 16-08-2016 Skip evnet function ..
        Intent skipIntent = new Intent(getApplicationContext(), PrivateGlobalActivity.class);
        startActivity(skipIntent);
    }

    public void sendTCPRequestToBreathi() {
        try {
            final ProgressDialog loading = ProgressDialog.show(this, "Sending data to Breath-i", "Please wait...", false, false);

            ECAPIHelper.placeAirOwlRequest(getBreathIData(), this, new Response.Listener<String>() {
                @Override
                @SuppressLint("LongLogTag")
                public void onResponse(String jsonObject) {
                    try {
                        if (jsonObject != null) {
                            loading.dismiss();
                            Log.i(TAG, "breathi request sent.");
                            lastStep();
                            startManageDeviceActivity();
                        } else {
                            Snackbar.make(mRelativeLayout, "Unable to send data, Please try again",
                                    Snackbar.LENGTH_LONG).show();
                            loading.dismiss();
                            Log.d(TAG, "Get Consumer Info Returned Null");
                        }

                    } catch (Exception ex) {
                        Snackbar.make(mRelativeLayout, "Unable to send data, Please try again",
                                Snackbar.LENGTH_LONG).show();
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
                        Log.i(TAG, "breathi request sent.");
                        startManageDeviceActivity();
                    } else {
                        Snackbar.make(mRelativeLayout, "Unable to send data, Please try again",
                                Snackbar.LENGTH_LONG).show();
                        loading.dismiss();
                        Log.d(TAG, "breathi request failed with volleyError " + volleyError.toString());
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lastStep() {
        // TODO: 16-08-2016  last step intent put extra check plz
        Intent last = new Intent(this, AddDeviceWifiSelectBreathi.class);
        last.putExtra(ApiKeys.MANAGE_DEVICE_SSID, breathiWifiSSID.getText().toString());
        last.putExtra(ApiKeys.MANAGE_DEVICE_PASSWORD, airOwlWifiPass.getText().toString());
        last.putExtra(ApiKeys.MANAGE_DEVICE_NAME, "");
        last.putExtra(ApiKeys.MANAGE_DEVICE_TYPE, ApiKeys.BREATH_I_TYPE);
        startActivity(last);
    }

    public String getBreathIData() {
        String ssid = breathiWifiSSID.getText().toString();
        String pass = airOwlWifiPass.getText().toString().trim();
        String returnString = Constants.HTTP_PRE + Constants.AIROWL_IP_ADDRESS +
                Constants.AIROWL_URL_SSID + ssid + Constants.AIROWL_URL_PASS + pass;
        return returnString;
    }

    private void startManageDeviceActivity() {
        Intent intent = new Intent(this, BreathiAllmostDone.class);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_ID, deviceId);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_SSID, breathiWifiSSID.getText().toString());
        intent.putExtra(ApiKeys.MANAGE_DEVICE_PASSWORD, airOwlWifiPass.getText().toString());
        intent.putExtra(ApiKeys.MANAGE_DEVICE_NAME, "");
        intent.putExtra(ApiKeys.MANAGE_DEVICE_TYPE, ApiKeys.BREATH_I_TYPE);
        startActivity(intent);
    }


    //To set overlay of second tutorial
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
        dialog.setContentView(R.layout.tutorial_breathi_screen3);


        RelativeLayout relOverlay = (RelativeLayout) dialog.findViewById(R.id.relOverlay);
        relOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDeviceWifiSelectBreathi.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //To set overlay of first tutorial
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
        dialog.setContentView(R.layout.tutorial_breathi_screen2);


        RelativeLayout relOverlay = (RelativeLayout) dialog.findViewById(R.id.relOverlay);
        relOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDeviceWifiSelectBreathi.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
