package com.idn0phl3108ed43d22s30.AirOwlWi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Api;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class AlmostThererAirOwl extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlmostThereAirOwl";

    private Toolbar toolbar;
    private Button mBtnDone;
    private TextView mInfo;
    private RelativeLayout mRelativeLayout;

    private SQLiteDatabaseHelper dbHelper;

    private String wifiSSID, wifiPass, deviceId, deviceType, deviceName, locLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allmost_therer_air_owl);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            deviceId = extras.getString(ApiKeys.MANAGE_DEVICE_ID);
        }

        dbHelper = new SQLiteDatabaseHelper(this);

        configView();
        clickEvent();
        getToolbar();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, PrivateGlobalActivity.class);
        intent.putExtra(ApiKeys.REGISTER_FLAG, true);
        startActivity(intent);
    }

    public static boolean isInternetAvailable(Context context) {
//        ConnectivityManager cm = (ConnectivityManager)context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        if (activeNetwork != null && activeNetwork.isConnected()) {
//            try {
//                URL url = new URL("http://www.google.com/");
//                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
//                urlc.setRequestProperty("User-Agent", "test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1000); // mTimeout is in seconds
//                urlc.connect();
//                if (urlc.getResponseCode() == 200) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } catch (IOException e) {
//                Log.i("warning", "Error checking internet connection", e);
//                return false;
//            }
//        }
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec (command).waitFor() == 0);
        } catch (InterruptedException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

    }

    private void fetchDeviceArray(){
        final ProgressDialog loading = ProgressDialog.show(this, "Fetching device data...", "Please wait...", false, false);

        ECAPIHelper.getDevicesListArray(this, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    if (response != null) {
                        dbHelper.updatePrivateDataArray(response);
                        dbHelper.updateLatestPrivateObj(response.getJSONObject(0));
                        dbHelper.updateLatestPrivateObjDevieId(ApiKeys.AIROWL_PRE_KEY + deviceId);
                        startHomeActivity();
                    } else {
                    }
                    loading.dismiss();
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error occurred with details : " + error.toString());
                loading.dismiss();
            }
        });
    }

    private void configView() {
        mBtnDone = (Button) findViewById(R.id.btnDone);
        mInfo = (TextView) findViewById(R.id.LblairOwlid);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relMainAllmostDone);
    }

    private void clickEvent() {
        mBtnDone.setOnClickListener(this);
    }

    private void getToolbar() {
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


    @Override
    public void onClick(View v) {
        if (v == mBtnDone) {
            if(isInternetAvailable(getApplicationContext())){
                fetchDeviceArray();
            } else {
                Snackbar.make(mRelativeLayout, R.string.nointernetavailable, Snackbar.LENGTH_LONG).show();
            }
        } else if (v == mInfo) {
            setOverlay();
        }
    }

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

    private void showOverlay() {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Dialog dialog = new Dialog(this, 0);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tutorial_screen4);

        RelativeLayout relOverlay = (RelativeLayout) dialog.findViewById(R.id.relOverlay);
        relOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlmostThererAirOwl.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
