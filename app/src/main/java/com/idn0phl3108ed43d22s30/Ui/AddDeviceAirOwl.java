package com.idn0phl3108ed43d22s30.Ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.idn0phl3108ed43d22s30.AirOwlCell.AddDeviceAirOwlCell;
import com.idn0phl3108ed43d22s30.AirOwlWi.AddDeviceAirOwlWifi;
import com.idn0phl3108ed43d22s30.R;

public class AddDeviceAirOwl extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    final Context context = this;

    private ImageView mAirOwlWifi, mAirOwlCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_air_owl);
        ConfigView();
        getToolbar();

    }

    private void getToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
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

    private void ConfigView() {
        mAirOwlWifi = (ImageView) findViewById(R.id.imgairOwlWifi);
        mAirOwlCell = (ImageView) findViewById(R.id.imgairOwlCell);

        mAirOwlCell.setOnClickListener(this);
        mAirOwlWifi.setOnClickListener(this);

    }

    private void AirOwlWifi() {

        Intent airOwlWifi = new Intent(getApplicationContext(), AddDeviceAirOwlWifi.class);
        startActivity(airOwlWifi);

    }

    private void AirOwlCell() {
        Intent airOwlWifi = new Intent(getApplicationContext(), AddDeviceAirOwlCell.class);
        startActivity(airOwlWifi);
    }

    @Override
    public void onClick(View v) {
        if (v == mAirOwlWifi) {
            AirOwlWifi();
        } else if (v == mAirOwlCell) {
            AirOwlCell();
        }
    }


}
