package com.idn0phl3108ed43d22s30;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.Ui.AddCity;
import com.idn0phl3108ed43d22s30.Ui.ChangePassword;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class BasicProfile extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BasicProfile";

    private Toolbar toolbar;
    private EditText email;
    private Button btnlogout, btnChangePass;
    private User user;
    private static SQLiteDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_profile);

        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());
        ConfigView();
        setupToolbar();
        Onclick();

        fetchUserData();
    }


    private void Onclick() {
        btnChangePass.setOnClickListener(this);
        btnlogout.setOnClickListener(this);
    }

    private void fetchUserData() {
        JSONObject userJson = dbHelper.readUserDataValue();
        if(userJson != null){
            String email;
            try{
                email = userJson.getString("email");
            } catch (JSONException e) {
                Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
                email = null;
            }
            setValues(email);
        }
        ECAPIHelper.getUserData(this, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response != null) {
                        if (response.length() != 0) {
                            JSONObject jsonObject = response.getJSONObject(0);
                            ObjectMapper mapper = new ObjectMapper();
                            dbHelper.updateUserValue(jsonObject);
                            user = mapper.readValue(jsonObject.toString(), User.class);
                            setValues(user.getEmail());
                        } else {
                            Log.e(TAG, "empty array response received from server about user data");
                        }
                    } else {
                        Log.e(TAG, "null response received from server about user data");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                } catch (JsonParseException e) {
                    Log.e(TAG, "JSON parse exceptio occurred with details : " + e.toString());
                } catch (JsonMappingException e) {
                    Log.e(TAG, "JSON Mapping exception occurred with details : " + e.toString());
                } catch (IOException e) {
                    Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Volley error occurred with details : " + error.toString());
            }
        });
    }

    private void setValues(String emailVal) {
        email.setText(emailVal);
        email.setFocusable(false);
        email.setFocusableInTouchMode(false);
        email.setClickable(false);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Basic Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ConfigView() {
        user = new User();
        email = (EditText) findViewById(R.id.etEmail);
        btnlogout = (Button) findViewById(R.id.btnlogout);
        btnChangePass = (Button) findViewById(R.id.btnChangePassword);
    }

    @Override
    public void onClick(View v) {
        if (v == btnChangePass) {
            Changepass();
        } else if (v == btnlogout) {
            Logout();
        }
    }

    private void Changepass() {
        Intent changePass = new Intent(getApplicationContext(), ChangePassword.class);
        startActivity(changePass);
    }

    private void Logout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BasicProfile.this)
                .setMessage(R.string.Logoutmsg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getApplicationContext()
                                .getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(Constants.LOGGEDIN_SHARED_PREF, false);
                        UserPrefrenceUtils.saveValueToUserSharedPrefs("userId", null, getApplicationContext());
                        UserPrefrenceUtils.saveUserIdToLoginPrefs(null, getApplicationContext());
                        editor.commit();

                        dbHelper.deleteUserData();
                        dbHelper.deletePrivateData();

                        boolean flag = false;
                        JSONArray jsonArray = dbHelper.readPublicDataValue();
                        if(jsonArray!=null){
                            flag = true;
                        }
                        Intent intent;
                        if (flag) {
                            intent = new Intent(BasicProfile.this, MainActivity.class);
                        } else {
                            intent = new Intent(BasicProfile.this, AddCity.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
