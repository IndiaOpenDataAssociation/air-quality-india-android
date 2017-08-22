package com.idn0phl3108ed43d22s30.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.Ui.AddCity;
import com.idn0phl3108ed43d22s30.Ui.AddDevice;

/**
 * Created by Rutul on 16-06-2016.
 */
public class MyDevicesAdd extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyDeviceAdd";

    private Toolbar toolbar;
    private ImageView mChooseCity, mAddDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_devices);
        ConfigView();
        SetToolbar();
    }

    private void SetToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("  My Devices");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_logo_icon));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ConfigView() {

        mChooseCity = (ImageView) findViewById(R.id.imgChooseCity);
        mAddDevices = (ImageView) findViewById(R.id.imgAddMyDevice);


        //Click Listener

        mChooseCity.setOnClickListener(this);
        mAddDevices.setOnClickListener(this);

        //AddDevice
    }

    @Override
    public void onClick(View v) {
        if (v == mChooseCity) {
            chooseCity();
        } else if (v == mAddDevices) {
            addDevice();
        }
    }


    private void chooseCity() {
        startCityListActivity();
    }

    private void startCityListActivity() {
        Intent choose = new Intent(this, AddCity.class);
        startActivity(choose);
    }


    private void addDevice() {
        Intent add = new Intent(this, AddDevice.class);
        startActivity(add);
    }
}
