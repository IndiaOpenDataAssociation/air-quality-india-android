package com.idn0phl3108ed43d22s30.AirOwlWi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.idn0phl3108ed43d22s30.Ui.ManageDevice;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.services.WifiMonitorService;

import java.util.ArrayList;
import java.util.List;


public class AddDeviceAirOwlWifiSelect extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddDeviceAirOwlWifiSelect";

    private static final String OPENED_FROM = "openedFrom";

    private Toolbar toolbar;
    private Button mbtnConnect;

    private Spinner airOwlWifiSelect;

    private String deviceId;

    private EditText airOwlWifiSSID;
    private EditText airOwlWifiPass;
    private CheckBox showPassCheckbox;

    private List<String> wifiSSIDs;

    private String[] wifiSSID;
    private ConnectionDetector connectionDetector;

    private WifiMonitorService wifiMonitorService;
    private List<ScanResult> wifiScanResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_air_owl_wifi_select);

        connectionDetector = new ConnectionDetector(this);
        Bundle extras = getIntent().getExtras();
        wifiSSIDs = new ArrayList<String>();
        if(extras != null){
            deviceId = extras.getString(ApiKeys.MANAGE_DEVICE_ID);
            wifiSSID = extras.getStringArray(ApiKeys.SELECT_WIFI_SSIDS);
        }
        wifiMonitorService = new WifiMonitorService(this);
        ConfigView();
        getToolbar();
        getScannedWifiAddresses();
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

    @SuppressLint("LongLogTag")
    private void getScannedWifiAddresses(){
        wifiScanResults = wifiMonitorService.getAvailableWifiAddrs();
        if(wifiScanResults != null){
            for(int i=0; i<wifiScanResults.size(); i++){
                if (wifiScanResults.get(i).SSID !=null && !(wifiScanResults.get(i).SSID.trim().equals(""))) {
                    wifiSSIDs.add(wifiScanResults.get(i).SSID);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_padding, wifiSSIDs);
            airOwlWifiSelect.setAdapter(adapter);
        } else {
            Log.i(TAG, "NO wifi available in range");
        }
    }

    private String getCurrentSSID(){
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    private void ConfigView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        mbtnConnect = (Button) findViewById(R.id.btnConnect);
        mbtnConnect.setOnClickListener(this);
        airOwlWifiSelect = (Spinner) findViewById(R.id.airOwlWifiSelect);

        //airOwlWifiSSID = (EditText) findViewById(R.id.airOwlWifiSSID);
        airOwlWifiPass = (EditText) findViewById(R.id.airOwlWifiPass);
        showPassCheckbox = (CheckBox) findViewById(R.id.checkbox);
        showPassCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    airOwlWifiPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    airOwlWifiPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    private void Connect() {
        String currentSSID = getCurrentSSID();
        currentSSID = removeChar(currentSSID,0);
        currentSSID = removeChar(currentSSID, currentSSID.length()-1);
        if(deviceId.equals(currentSSID)){
            if(connectionDetector.isConnectingToMobileData()){
                Toast.makeText(this, "Please turn off your mobile data and try again!", Toast.LENGTH_LONG).show();
            } else {
                sendTCPRequestToAirowl();
            }

        } else {
            connectToSelectedNetwork(deviceId);
        }
    }
    public String removeChar(String str, Integer n) {
        String front = str.substring(0, n);
        String back = str.substring(n+1, str.length());
        return front + back;
    }

    public void connectToSelectedNetwork(String currentSSID) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + currentSSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + currentSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                if(connectionDetector.isConnectingToMobileData()){
                    Toast.makeText(this, "Please turn off your mobile data and try again!", Toast.LENGTH_LONG).show();
                } else {
                    sendTCPRequestToAirowl();
                }

                break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        Connect();
    }

    public void sendTCPRequestToAirowl(){
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
                            startManageDeviceActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to send data, Please try again",
                                    Toast.LENGTH_LONG).show();
                            loading.dismiss();
                            Log.d(TAG, "Get Consumer Info Returned Null");
                        }

                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Unable to send data, Please try again",
                                Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        Log.e(TAG, "Exception occurred with details : " + ex.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String error = volleyError.toString();
                    if(error.endsWith("ok")){
                        loading.dismiss();
                        Log.i(TAG, "Airowl request sent.");
                        startManageDeviceActivity();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to send data, Please try again",
                                Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        Log.d(TAG, "airowl request failed with volleyError "+volleyError.toString());
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAIROWLData(){
        String ssid = airOwlWifiSelect.getSelectedItem().toString();
        String pass = airOwlWifiPass.getText().toString().trim();

        String returnString = Constants.HTTP_PRE + Constants.AIROWL_IP_ADDRESS + ":" + Constants.AIROWL_PORT_NUMBER +
                Constants.AIROWL_URL_SSID + ssid + Constants.AIROWL_URL_PASS + pass;
        return returnString;
    }

    private void startManageDeviceActivity(){
        Intent intent = new Intent(this, ManageDevice.class);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_ID, deviceId);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_SSID, airOwlWifiSelect.getSelectedItem().toString());
        intent.putExtra(ApiKeys.MANAGE_DEVICE_PASSWORD, airOwlWifiPass.getText().toString());
        intent.putExtra(ApiKeys.MANAGE_DEVICE_NAME, "");
        intent.putExtra(ApiKeys.MANAGE_DEVICE_TYPE, ApiKeys.AIROWL_WI_TYPE);
        startActivity(intent);

    }

}
