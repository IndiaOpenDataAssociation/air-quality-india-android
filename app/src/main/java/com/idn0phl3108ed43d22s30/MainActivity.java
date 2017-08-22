package com.idn0phl3108ed43d22s30;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.idn0phl3108ed43d22s30.Animation.ActivitySwitcher;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.Ui.MyDevices;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.helpers.CommonHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AHBottomNavigation bottomNavigationItem;
    private Toolbar toolbar;

    private String aqi;
    private String status;

    private JSONArray devicesArray;

    private SQLiteDatabaseHelper dbHelper;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // finish();
        //super.onBackPressed();
        showYesNoConfirmation(getApplicationContext(), this);

    }

    private void showYesNoConfirmation(final Context context, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogGlobal);
        builder.setTitle(getResources().getString(R.string.appTitle));
        builder.setMessage(getResources().getString(R.string.exit_dialog));
        builder.setPositiveButton(getResources().getString(R.string.ok_dialog), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.finish();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel_dialog), null);
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, null, getApplicationContext());
        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());

        devicesArray = dbHelper.readPublicDataValue();

        JSONObject jsonObject = dbHelper.readLatestPublicObj();

        try {
            aqi = String.valueOf(jsonObject.getInt("aqi"));
            status = CommonHelpers.getStatus(jsonObject.getInt("aqi"));
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred while fetching aqi. details : " + e.toString());
        }

        ConfigView();

        setupToolbar();
        bottomNavigation();
        setCurrentFragment();

    }

    private void setCurrentFragment() {
        Fragment AddDevice = new GlobalActivity();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView, AddDevice, null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.private_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //To swap between Global and Private mode
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_private:
//                animatedStartActivity(); // TODO: 10-11-2016 Comment this if u want to stop animation
                startPrivateActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void animatedStartActivity() {
        final Intent intent = new Intent(this, PrivateGlobalActivity.class);
        intent.putExtra(ApiKeys.SWITCH_FLAG, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ActivitySwitcher.animationOut(findViewById(R.id.coordinatorLayout), getWindowManager(), new ActivitySwitcher.AnimationFinishedListener() {
            @Override
            public void onAnimationFinished() {
                startActivity(intent);
            }
        });

    }

    public void switchViewPager(int i) {
        bottomNavigationItem.setCurrentItem(i);
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    //start private activity
    private void startPrivateActivity() {
        Intent intent = new Intent(this, PrivateGlobalActivity.class);
        intent.putExtra(ApiKeys.SWITCH_FLAG, true);
        startActivity(intent);
        //overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_logo_icon));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    private void ConfigView() {
        //    dbHelper = new SQLiteDatabaseHelper(getApplicationContext());
        bottomNavigationItem = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

    }

    public JSONArray getDevicesArray() {
        return devicesArray;
    }


    // to create bottom navigation drawer...
    private void bottomNavigation() {
        //Create

        AHBottomNavigationItem Home = new AHBottomNavigationItem(R.string.home, R.drawable.ic_action_home_6, R.color.white);
        AHBottomNavigationItem Analytics = new AHBottomNavigationItem(R.string.analytics, R.drawable.ic_action_analytics_6, R.color.blue);
        AHBottomNavigationItem City = new AHBottomNavigationItem(R.string.city, R.drawable.ic_action_city6, R.color.blue);
        AHBottomNavigationItem Device = new AHBottomNavigationItem(R.string.device, R.drawable.ic_action_devices6, R.color.blue);
        AHBottomNavigationItem Community = new AHBottomNavigationItem(R.string.community, R.drawable.ic_action_community_6, R.color.blue);
        AHBottomNavigationItem Menu = new AHBottomNavigationItem(R.string.other, R.drawable.ic_action_menu_6, R.color.blue);

        //ic_action_user
        //Add item
        bottomNavigationItem.addItem(Home);
        bottomNavigationItem.addItem(Analytics);
        if (getApplicationContext() instanceof MainActivity) {
            bottomNavigationItem.addItem(City);
        } else {
            bottomNavigationItem.addItem(City);
        }
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
