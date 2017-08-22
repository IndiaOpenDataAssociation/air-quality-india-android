package com.idn0phl3108ed43d22s30.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;
import java.util.Set;

/**
 * Created by jimish on 17/6/16.
 */

public class WifiMonitorService {

    private static final String TAG = "WifiMonitorService";

    private Context context;
    private BroadcastReceiver wifiReceiver;
    private Set<String> currentScanMacAddresses;
    private long lastScannedTimeStamp;
    private Intent currentIntent;

    public WifiMonitorService(Context context){
        this.context = context;
    }

    public List<ScanResult> getAvailableWifiAddrs() {
        Log.i(TAG, "requesting for available wifi addresses");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiResults = wifiManager.getScanResults();
        return wifiResults;
    }

}
