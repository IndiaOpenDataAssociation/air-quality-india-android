package com.idn0phl3108ed43d22s30.Ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idn0phl3108ed43d22s30.MainActivity;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.Device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    Thread splashThread;

    //static codes for public/private devices
    private static final int PRIVATE_DATA_AVAILABLE = 1;
    private static final int PUBLIC_DATA_AVAILABLE = 2;
    private static final int USER_DATA_AVAILABLE = 3;
    private static final int NO_DATA_AVAILABLE = 0;

    //database instance
    private static SQLiteDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For no status bar..
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splashscreen);

        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());

        startThreads();

        StartAnimations();
    }


    private void StartAnimations() {
        Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        splashAnimation.reset();

        splashAnimation = AnimationUtils.loadAnimation(this, R.anim.translate);
        splashAnimation.reset();

        ImageView splashImageView = (ImageView) findViewById(R.id.splashImg);
        splashImageView.clearAnimation();
        splashImageView.startAnimation(splashAnimation);

        splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }

                    //TODO start Activities

                    int _tmp = readDataFromDb();
                    switch (_tmp) {
                        case PRIVATE_DATA_AVAILABLE:
                            //start private activity
                            if (UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", getApplicationContext()) == null && _tmp == PUBLIC_DATA_AVAILABLE) {
                                startPublicGlobalActivity();
                            } else {
                                startPrivateActivity();
                            }
                            break;
                        case PUBLIC_DATA_AVAILABLE:
                            //start public activity
                            startPublicGlobalActivity();
                            break;
                        case NO_DATA_AVAILABLE:
                            //start add city activity
                            welcomeScreen();
                            break;
                        default:
                            finish();
                            //start add city activity
                            break;
                    }

                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted Exception occurred with details : " + e.toString());
                } finally {
                    SplashActivity.this.finish();
                }

            }
        };
        splashThread.start();
    }

    private int readDataFromDb() {
        try {
            JSONArray privateArray = dbHelper.readPrivateDataValue();
            JSONObject privateJson, publicJson;
            JSONArray publicarray = dbHelper.readPublicDataValue();
            if (privateArray != null) {
                privateJson = privateArray.getJSONObject(0);
                dbHelper.updateLatestPrivateObj(privateJson);
                return PRIVATE_DATA_AVAILABLE;
            } else if (publicarray != null) {
                publicJson = publicarray.getJSONObject(0);
                dbHelper.updateLatestPublicObj(publicJson);
                return PUBLIC_DATA_AVAILABLE;
            } else {
                return NO_DATA_AVAILABLE;
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
            return NO_DATA_AVAILABLE;
        }

    }

    private void startThreads() {
        if (dbHelper.readPrivateDataValue() != null) {
            new Thread(new Runnable() {
                public void run() {
                    fetchPrivateDevices();
                }
            }).start();
        }

        if (dbHelper.readPrivateDataValue() != null) {
            new Thread(new Runnable() {
                public void run() {
                    fetchPublicDevices();
                }
            }).start();
        }
    }

    //add city activity starting function
    private void startAddCityActivity() {
        Intent addCityIntent = new Intent(this, AddCity.class);
        addCityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        addCityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(addCityIntent);
    }

    //start public activity function
    private void startPublicGlobalActivity() {
        Intent publicIntent = new Intent(this, MainActivity.class);
        publicIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        publicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(publicIntent);
    }

    //start private activity function
    private void startPrivateActivity() {
        Intent privateIntent = new Intent(this, PrivateGlobalActivity.class);
        privateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        privateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(privateIntent);
    }

    //Start welcome screen
    private void welcomeScreen() {
        Intent welcome = new Intent(this,WelcomeActivity.class);
        startActivity(welcome);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void fetchPublicDevices() {
        ECAPIHelper.fetchDeviceId(this, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        dbHelper.updatePublicDataArray(response);
                    } else {
                        Log.e(TAG, "null devices received on refresh");
                    }

                } catch (Exception e) {
                    Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "volley error occurred with details : " + error.toString());
            }
        });
    }

    private void fetchPrivateDevices() {
        final Context context = this.getApplicationContext();
        ECAPIHelper.getDevicesListArray(context, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    dbHelper.updatePrivateDataArray(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error occurred with details : " + error.toString());
            }
        });
    }
}

