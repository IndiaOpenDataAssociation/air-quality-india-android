package com.idn0phl3108ed43d22s30;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;


public class NotifiactionActivity extends AppCompatActivity {
    private static final String DEVICES_PREFS_NAME = "devicesPreferences";
    private TextView txt;
    Toolbar toolbar;
    private SeekBar seekBar;
    private Switch mIndoor, mOutdoor, mBoth, mSmartNotification, mMorningReport, mEvening;
    private TextView mLow, mMedium, mHigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiaction);

//        txt = (TextView) findViewById(R.id.txtAqi);
//        icon_manager = new Icon_Manager();
//        txt.setTypeface(Icon_Manager.get_icons("fonts/aqi.ttf", this));
        ConfigView();
        setUpToolbar();
        SwitchEvents();
        seekbarEvent();

    }

    private void seekbarEvent() {
        final int flag = 0;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                UserPrefrenceUtils.seekBarValue(progress, getApplicationContext());
                if (progress == 0 || progress <= 40 || flag == 0) {
                    // Toast.makeText(getApplicationContext(), "LOW", Toast.LENGTH_SHORT).show();

                    mLow.setTextColor(Color.YELLOW);
                } else if (progress == 41 || progress <= 50) {
                    // Toast.makeText(getApplicationContext(), "MEDIUM", Toast.LENGTH_SHORT).show();
                    mMedium.setTextColor(Color.GREEN);
                } else if (progress == 51 || progress <= 100) {
                    mHigh.setTextColor(Color.RED);
                    //Toast.makeText(getApplicationContext(), "HIGH", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void SwitchEvents() {
        //Indoor
        mIndoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(NotifiactionActivity.this, "Switch indoor is on", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NotifiactionActivity.this, "Switch indoor is off", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Outdoor
        mOutdoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(NotifiactionActivity.this, "Switch outdoor is on", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NotifiactionActivity.this, "Switch outdoor is off", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Both
        mBoth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(NotifiactionActivity.this, "Switch both is on", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NotifiactionActivity.this, "Switch both is off", Toast.LENGTH_LONG).show();
                }
            }
        });
        //SmartNotification
        mSmartNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(NotifiactionActivity.this, "Switch smart notification is on", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NotifiactionActivity.this, "Switch smart notification is off", Toast.LENGTH_LONG).show();
                }
            }
        });
        //MorningRport
        mMorningReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(NotifiactionActivity.this, "Switch morning report is on", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NotifiactionActivity.this, "Switch morning report is off", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Evening
        mEvening.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(NotifiactionActivity.this, "Switch evening is on", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NotifiactionActivity.this, "Switch evening is off", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void ConfigView() {


        seekBar = (SeekBar) findViewById(R.id.seekBar);
        mIndoor = (Switch) findViewById(R.id.switchIndor);
        mOutdoor = (Switch) findViewById(R.id.switchOutdoor);
        mBoth = (Switch) findViewById(R.id.switchBoth);
        mSmartNotification = (Switch) findViewById(R.id.switchSmartNotification);
        mMorningReport = (Switch) findViewById(R.id.switchMorningReport);
        mEvening = (Switch) findViewById(R.id.switchEvening);
        mLow = (TextView) findViewById(R.id.txtLow);
        mMedium = (TextView) findViewById(R.id.txtMedium);
        mHigh = (TextView) findViewById(R.id.txtHigh);
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("NOTIFICATION");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
