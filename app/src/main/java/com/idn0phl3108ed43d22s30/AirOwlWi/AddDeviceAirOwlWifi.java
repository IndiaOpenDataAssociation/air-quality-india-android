package com.idn0phl3108ed43d22s30.AirOwlWi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.idn0phl3108ed43d22s30.Ui.ManageDevice;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.services.WifiMonitorService;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceAirOwlWifi extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddDeviceAirOwlWifi";

    private Button mbtnConnect;
    private Button skipButton;
    private Spinner mairOwlid;

    private WifiMonitorService wifiMonitorService;
    private List<ScanResult> wifiScanResults;
    private List<String> wifiSSIDs;
    private WifiConfiguration conf;
    private String networkSSID = "";
    private int flag = 1;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_air_owl_wifi);

        //initialize wifi monitor service to get avaliable wifi connections
        wifiMonitorService = new WifiMonitorService(this);
        wifiSSIDs = new ArrayList<String>();

        Bundle extras = getIntent().getExtras();
        if(extras!=null){

        }

        ConfigView();
        getToolbar();
        getScannedWifiAddresses();

    }

    private void getScannedWifiAddresses() {
        wifiScanResults = wifiMonitorService.getAvailableWifiAddrs();
        if (wifiScanResults != null) {
            for (int i = 0; i < wifiScanResults.size(); i++) {
                if (wifiScanResults.get(i).SSID !=null && !(wifiScanResults.get(i).SSID.trim().equals(""))) {
                    wifiSSIDs.add(wifiScanResults.get(i).SSID);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_padding, wifiSSIDs);
            mairOwlid.setAdapter(adapter);
        } else {
            Log.i(TAG, "NO wifi available in range");
        }
    }

    private void ConfigView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        mbtnConnect = (Button) findViewById(R.id.btnConnect);
        skipButton = (Button) findViewById(R.id.btnSkip);
        mbtnConnect.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        mairOwlid = (Spinner) findViewById(R.id.airOwlid);
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
        if(v == mbtnConnect){
            Connect();
        } else if(v == skipButton){
            startManageDevice();
        }

    }

    private void Connect() {
        //TODO connect funnction
        boolean flag = isWifiOn();
        if(flag){
            initWifiConfig();
            connectToSelectedNetwork();
        } else {
            Toast.makeText(this, "Please turn your wifi on!", Toast.LENGTH_SHORT).show();
        }

    }

    public void initWifiConfig() {
        networkSSID = mairOwlid.getSelectedItem().toString();
        conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
    }

    private boolean isWifiOn(){
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()){
            return true;
        } else {
            return false;
        }
    }




    public void connectToSelectedNetwork() {
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                startAnotherActivity();
                break;
            }
        }
    }



    public void startAnotherActivity() {
        Intent air = new Intent(getApplicationContext(), AddDeviceAirOwlWifiSelect.class);
        air.putExtra(ApiKeys.MANAGE_DEVICE_ID, networkSSID);
        air.putExtra(ApiKeys.SELECT_WIFI_SSIDS, wifiSSIDs.toArray());
        startActivity(air);
    }

    private void startManageDevice(){
        Intent intent = new Intent(this, ManageDevice.class);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_ID, mairOwlid.getSelectedItem().toString());
        intent.putExtra(ApiKeys.MANAGE_DEVICE_SSID, "@#@");
        intent.putExtra(ApiKeys.MANAGE_DEVICE_PASSWORD, "@#@");
        intent.putExtra(ApiKeys.MANAGE_DEVICE_NAME, "@#@");
        intent.putExtra(ApiKeys.MANAGE_DEVICE_TYPE, ApiKeys.AIROWL_WI_TYPE);
        startActivity(intent);
    }

}
