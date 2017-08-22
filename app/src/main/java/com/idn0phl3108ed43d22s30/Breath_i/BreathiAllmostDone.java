package com.idn0phl3108ed43d22s30.Breath_i;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idn0phl3108ed43d22s30.AirOwlWi.AirOwlId;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;

public class BreathiAllmostDone extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button mBtnDone;
    private TextView mInfo;
    private RelativeLayout mRelativeLayout;

    private String deviceId, breathiWifiSSID, airOwlWifiPass, deviceName, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathi_allmost_done);
        Bundle extras = getIntent().getExtras();
        configView();
        if (extras != null) {
            deviceId = extras.getString(ApiKeys.MANAGE_DEVICE_ID);
            breathiWifiSSID = extras.getString(ApiKeys.MANAGE_DEVICE_SSID);
            airOwlWifiPass = extras.getString(ApiKeys.MANAGE_DEVICE_PASSWORD);
            deviceName = extras.getString(ApiKeys.MANAGE_DEVICE_NAME);
            type = extras.getString(ApiKeys.MANAGE_DEVICE_TYPE);
        } else {
            alertDialog();
        }
        setupToolbar();
    }

    //start Private global activity...
    private void StartHomeActivity() {
        Intent intent = new Intent(this, PrivateGlobalActivity.class);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_SSID, breathiWifiSSID);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_PASSWORD, airOwlWifiPass);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_ID, deviceId);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_TYPE, type);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_NAME, deviceName);
        startActivity(intent);
    }

    private void configView() {
        mBtnDone = (Button) findViewById(R.id.btnDone);
        mInfo = (TextView) findViewById(R.id.LblBreathi);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relMainAllmostDoneBreathi);
    }

    private void setupToolbar() {
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
            StartHomeActivity();
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
                BreathiAllmostDone.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BreathiAllmostDone.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.errorTittle);
        builder.setMessage(R.string.errorDetail1);
        builder.setNegativeButton("TRY AGIAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent start = new Intent(getApplicationContext(), AirOwlId.class);
                startActivity(start);
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void snakBarError() {
        Snackbar.make(mRelativeLayout, R.string.errorUnableToSendData, Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setActionTextColor(Color.parseColor("#00b3bf"))
                .show();

    }
}
