package com.idn0phl3108ed43d22s30.Polludrone;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.idn0phl3108ed43d22s30.Ui.ManageDevice;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;

/**
 * Created by Rutul on 01-07-2016.
 */
public class AddDevicePolludrone extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddDeviceAirOwlCell";

    private Toolbar toolbar;
    private Button mbtnConnect;
    private EditText mSerial;
    private Context context = this;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_add_device_polludrone);
        ConfigView();
        getToolbar();
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

    private void ConnectionCheck() {
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            Connect();
        } else {
            final Dialog dialog = new Dialog(context);
            dialog.setTitle("Fail to connect");
            dialog.setContentView(R.layout.alert_activity_nointernet);
            ImageView image = (ImageView) dialog.findViewById(R.id.imgAlertNoInternet);
            image.setImageResource(R.drawable.alert_nointernet);

            dialog.show();
        }

    }

    private void Connect() {
        startManageDevice();
    }

    private void startManageDevice(){
        Intent intent = new Intent(this, ManageDevice.class);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_ID, ApiKeys.POLLUDRON_PRE_KEY + mSerial.getText().toString());
        intent.putExtra(ApiKeys.MANAGE_DEVICE_SSID, "@#@");
        intent.putExtra(ApiKeys.MANAGE_DEVICE_PASSWORD, "@#@");
        intent.putExtra(ApiKeys.MANAGE_DEVICE_NAME, "");
        intent.putExtra(ApiKeys.MANAGE_DEVICE_TYPE,ApiKeys.POLLUDRON_TYPE);
        startActivity(intent);
    }

    private void ConfigView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        mSerial = (EditText) findViewById(R.id.etSerialPolludrone);
        mbtnConnect = (Button) findViewById(R.id.btnConnectPolludrone);
        mbtnConnect.setOnClickListener(this);
        cd = new ConnectionDetector(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
            ConnectionCheck();
    }


}
