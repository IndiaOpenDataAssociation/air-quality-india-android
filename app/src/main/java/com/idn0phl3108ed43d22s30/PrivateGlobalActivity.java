package com.idn0phl3108ed43d22s30;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idn0phl3108ed43d22s30.Animation.ActivitySwitcher;
import com.idn0phl3108ed43d22s30.LoginRegister.LoginActivity;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.Ui.AddCity;
import com.idn0phl3108ed43d22s30.Ui.MyDevices;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.HourlyData;
import com.idn0phl3108ed43d22s30.pojo.Payload;
import com.idn0phl3108ed43d22s30.pojo.PayloadData;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by jimish on 27/6/16.
 */

public class PrivateGlobalActivity extends AppCompatActivity {
    private static final String TAG = "PrivateGlobalActivity";

    private AHBottomNavigation bottomNavigationItem;
    private Toolbar toolbar;
    private JSONArray devicesArray;

    private List<HourlyData> payloadDataList;
    private SharedPreferences sharedPreferences;

    private static SQLiteDatabaseHelper dbHelper;

    private boolean switchFlag, registerFlag;
    private List<LinearLayout> donutLayouts;
    private List<CircularProgressBar> donutViews;
    private List<TextView> textData;

    private TextView textProgresspm10, textProgressco2, textProgressg2co, textProgressnh3, textProgressg5, textProgressh2s, textProgressg7,
            textProgressg8, textProgressg9, textProgresso3, textProgressco, textProgressno2, textProgresssso2, textProgresspm25, textProgressp10, textProgressp2,
            textProgressp3;

    private int avgAqi, g1, g2, g3, g4, g5, g6, g7, g8, g9, o3, co, no2, so2, pm25, pm10, p1, p2, p3;




    private HorizontalScrollView gasesScrollBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());
        switchFlag = false;
        registerFlag = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            switchFlag = extras.getBoolean(ApiKeys.SWITCH_FLAG);
            registerFlag = extras.getBoolean(ApiKeys.REGISTER_FLAG);

            if(UserPrefrenceUtils.getValueFromUserSharedPrefs("userId",getApplicationContext())==null && switchFlag){
                startLoginAcitivity();
            }
        } else {
        }


        fetchData();
    }




    public void swichViewPager(int i) {
        bottomNavigationItem.setCurrentItem(i);
    }

    private void startLoginAcitivity() {
        Intent mydevice = new Intent(this, LoginActivity.class);
        mydevice.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mydevice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mydevice.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mydevice);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    private void fetchData() {

        devicesArray = dbHelper.readPrivateDataValue();
        if (devicesArray != null) {
            try {
                if (!registerFlag) {
                    if (switchFlag) {
                        JSONObject payloadJSON = devicesArray.getJSONObject(0);
                        UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA,
                                payloadJSON.getJSONObject(ApiKeys.PAYLOAD_LABEL).toString(), getApplicationContext());
                    } else {
                        JSONObject payloadJSON = devicesArray.getJSONObject(devicesArray.length() - 1);
                        UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA,
                                payloadJSON.getJSONObject(ApiKeys.PAYLOAD_LABEL).toString(), getApplicationContext());
                    }
                }

                ConfigView();

                setupToolbar();
                bottomNavigation();
                setCurrentFragment();
            } catch (JSONException e) {
                Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
            }
        } else {
            startLoginAcitivity();
        }


        ECAPIHelper.getDevicesListArray(this, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    devicesArray = response;
                    dbHelper.updatePrivateDataArray(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();
          showYesNoConfirmation(getApplicationContext(), this);

    }

    private void showYesNoConfirmation(final Context context, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PrivateGlobalActivity.this, R.style.AlertDialogPrivate);
        builder.setTitle(getResources().getString(R.string.appTitle));
        builder.setMessage(getResources().getString(R.string.exit_dialog));
        builder.setPositiveButton(getResources().getString(R.string.ok_dialog), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel_dialog), null);
        builder.show();
    }

    private void setCurrentFragment() {
        Fragment AddDevice = new GlobalActivity();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView, AddDevice, null);
        fragmentTransaction.commit();
    }

    public JSONArray getDevicesArray() {
        return devicesArray;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.public_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_public:
//                animatedStartActivity(); // TODO: 10-11-2016 Comment this if u want to stop animation
                startPrivateActivity();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void animatedStartActivity() {
        boolean flag = false;
        JSONArray jsonArray = dbHelper.readPublicDataValue();
        if (jsonArray != null && !(jsonArray.length() < 1)) {
            flag = true;
        }


        final Intent intent;
        if (flag) {
            UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, null, getApplicationContext());
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ActivitySwitcher.animationOut(findViewById(R.id.coordinatorLayout), getWindowManager(), new ActivitySwitcher.AnimationFinishedListener() {
                @Override
                public void onAnimationFinished() {
                    startActivity(intent);
                }
            });
        } else {
            intent = new Intent(this, AddCity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ActivitySwitcher.animationOut(findViewById(R.id.coordinatorLayout), getWindowManager(), new ActivitySwitcher.AnimationFinishedListener() {
                @Override
                public void onAnimationFinished() {
                    startActivity(intent);
                }
            });
        }
        startActivity(intent);
    }


    private void startPrivateActivity() {
        boolean flag = false;
        JSONArray jsonArray = dbHelper.readPublicDataValue();
        if (jsonArray != null && !(jsonArray.length() < 1)) {
            flag = true;
        }

        Intent intent;
        if (flag) {
            UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, null, getApplicationContext());
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, AddCity.class);
        }
        startActivity(intent);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_logo_icon));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    private void ConfigView() {

        bottomNavigationItem = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

    }

    private void bottomNavigation() {
        //Create
        AHBottomNavigationItem Home = new AHBottomNavigationItem(R.string.home, R.drawable.ic_action_home_6, R.color.blue);
        AHBottomNavigationItem Analytics = new AHBottomNavigationItem(R.string.analytics, R.drawable.ic_action_analytics_6, R.color.blue);
        AHBottomNavigationItem Device = new AHBottomNavigationItem(R.string.device, R.drawable.ic_action_devices6, R.color.blue);
        AHBottomNavigationItem Community = new AHBottomNavigationItem(R.string.community, R.drawable.ic_action_community_6, R.color.blue);
        AHBottomNavigationItem Menu = new AHBottomNavigationItem(R.string.other, R.drawable.ic_action_menu_6, R.color.blue);

        //ic_action_user
        //Add item
        bottomNavigationItem.addItem(Home);
        bottomNavigationItem.addItem(Analytics);
        bottomNavigationItem.addItem(Device);
        bottomNavigationItem.addItem(Community);
        bottomNavigationItem.addItem(Menu);

        // Change colors
        bottomNavigationItem.setAccentColor(Color.parseColor("#FFFFFF"));
        bottomNavigationItem.setInactiveColor(Color.parseColor("#70FFFFFF"));

        //Force to tint
        bottomNavigationItem.setForceTint(true);

        //Set color mode
        bottomNavigationItem.setFocusableInTouchMode(true);


        //Disable translationtu
        bottomNavigationItem.setBehaviorTranslationEnabled(false);

        //Force to show tittle
        bottomNavigationItem.setForceTitlesDisplay(true);
        bottomNavigationItem.setFocusableInTouchMode(true);


        //BG color
        bottomNavigationItem.setDefaultBackgroundColor(Color.parseColor("#303841"));

        //Current item selection
        bottomNavigationItem.setCurrentItem(0);

        //On CLick Listener
        bottomNavigationItem.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {


                if (position == 0) {
                    Fragment AddDevice = new GlobalActivity();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, AddDevice, null);
                    fragmentTransaction.commit();
                } else if (position == 1) {
                    Fragment Analtics = new FragmentAnalytics();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, Analtics, null);
                    fragmentTransaction.commit();
                } else if (position == 2) {
                    Fragment MyDevice = new MyDevices();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, MyDevice, null);
                    fragmentTransaction.commit();
                } else if (position == 3) {
                    Fragment MyDevice = new CommunityFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, MyDevice, null);
                    fragmentTransaction.commit();
                } else {
                    Fragment Profile = new FragmentProfile();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, Profile, null);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });
    }


}

