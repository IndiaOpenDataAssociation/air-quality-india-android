package com.idn0phl3108ed43d22s30.Breath_i;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.idn0phl3108ed43d22s30.R;

public class AdddeviceBreathiWifi extends AppCompatActivity implements View.OnClickListener {

    private Button mbtnConnect;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddevice_breathi_wifi);
        ConfigView();
        getToolbar();

        //Connect button event
        mbtnConnect.setOnClickListener(this);


    }

    private void ConfigView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        mbtnConnect = (Button) findViewById(R.id.btnConnect);
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
        if (v == mbtnConnect) {
            Coonect();
        }
    }

    private void Coonect() {
        Toast.makeText(AdddeviceBreathiWifi.this, "This is on click event", Toast.LENGTH_SHORT).show();
    }
}
