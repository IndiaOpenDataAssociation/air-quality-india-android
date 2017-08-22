package com.idn0phl3108ed43d22s30.Breath_i;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;

import com.idn0phl3108ed43d22s30.R;

public class AddDeviceBreathiBuyPage extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Context context;
    private Animation  animFadein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_add_device_breathi_buypage);
        ConfigView();
        getToolbar();

        //TODO this is only for testing after testing remove this method




    }

    public void Test(){
        Intent i = new Intent(this,AdddeviceBreathiWifi.class);
        startActivity(i);
    }

    private void getToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buy Now");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ConfigView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);


        //Custom fonts selection
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");

    }

    @Override
    public void onClick(View v) {


    }
}
