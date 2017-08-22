package com.idn0phl3108ed43d22s30.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.idn0phl3108ed43d22s30.AirOwlCell.AddDeviceAirOwlCell;
import com.idn0phl3108ed43d22s30.AirOwlWi.AddDeviceAirOwlWifi;
import com.idn0phl3108ed43d22s30.BasicProfile;
import com.idn0phl3108ed43d22s30.Breath_i.AddDeviceBreathi;
import com.idn0phl3108ed43d22s30.Polludrone.AddDevicePolludrone;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.Ui.AddDevice;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.Login;
import com.idn0phl3108ed43d22s30.pojo.Signup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Register";

    private Button mBtnSignup;
    private EditText mEmail, mCPassword, mPassword;
    private CoordinatorLayout mCoorRegister;
    private CheckBox checkBox;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private String Email, CPassword, Password;

    private Signup signup;
    private String selectedDeviceType;

    private static SQLiteDatabaseHelper dbHelper;

    private JSONArray devicesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            selectedDeviceType = extras.getString(ApiKeys.SHARED_DEVICE_TYPE);
            if(selectedDeviceType == null){
                selectedDeviceType = "";
            }
        } else {
            selectedDeviceType = "";
        }

        //For no status bar..
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());

        setContentView(R.layout.activity_register);

        initConfigView();


    }

    private void checkInternetConnectivity() {
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            validateEmailPwd();
        } else {
            Snackbar snackbar = Snackbar
                    .make(mCoorRegister, R.string.errorInternet, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    private void validateEmailPwd() {
        Email = mEmail.getText().toString();
        CPassword = mCPassword.getText().toString();
        Password = mPassword.getText().toString();
        if (Email.isEmpty() || CPassword.isEmpty() || Password.isEmpty()) {
            Snackbar snackbar = Snackbar
                    .make(mCoorRegister, R.string.errorBlank, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (!isValidEmail(Email)) {
            Snackbar snackbar = Snackbar
                    .make(mCoorRegister, R.string.errorEmail, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (!Password.equals(CPassword)) {
            Snackbar snackbar = Snackbar
                    .make(mCoorRegister, R.string.errorPassword, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (Password.length() < 5) {
            Snackbar snackbar = Snackbar
                    .make(mCoorRegister, R.string.errorPasswordlength, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            registerUser();
        }

    }

    private boolean isValidEmail(String username) {

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    private void initConfigView() {
        mBtnSignup = (Button) findViewById(R.id.signBtn);
        mEmail = (EditText) findViewById(R.id.etEmail);
        mCPassword = (EditText) findViewById(R.id.etConformPass);
        mPassword = (EditText) findViewById(R.id.etPassword);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mCPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mCPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
        mCoorRegister = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        cd = new ConnectionDetector(getApplicationContext());
        //OnClick
        mBtnSignup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSignup) {
            checkInternetConnectivity();
        }
    }

    private void registerUser(){
        signup = new Signup(Email, Password);
        final JSONObject jsonObject = signup.toJson();
        final ProgressDialog loading = ProgressDialog.show(this, "Registering User...", "Please wait...", false, false);

        ECAPIHelper.signUpRequest(this, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response!=null){
                    try{
                        int status = response.getInt("status");
                        if(status == 201){
                            loading.dismiss();
                            Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_LONG).show();
                            performLogin();
                        } else {
                            loading.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(mCoorRegister, R.string.failedToRegister, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } catch (JSONException e){
                        loading.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(mCoorRegister, R.string.failedToRegister, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        Log.e(TAG, "JSON Excpetion occurred with details : "+e.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar
                        .make(mCoorRegister, R.string.failedToRegister, Snackbar.LENGTH_LONG);
                snackbar.show();
                loading.dismiss();
                Log.e(TAG,"Volley Error occurred with details : "+error.toString());
            }
        });
    }

    private void performLogin(){
        Login login = new Login(Email, Password);
        final JSONObject jsonObject = login.toJson();
        final ProgressDialog loading = ProgressDialog.show(this, "Logging In...", "Please wait...", false, false);

        ECAPIHelper.login(jsonObject, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response!=null){
                        int status = response.getInt("status");
                        if(status == 200){
                            String id = response.getString("userId");
                            jsonObject.put("userId",id);
                            dbHelper.updateUserValue(jsonObject);
                            loading.dismiss();
                            UserPrefrenceUtils.saveValueToUserSharedPrefs("userId", id, getApplicationContext());
                            UserPrefrenceUtils.saveUserIdToLoginPrefs(id, getApplicationContext());
                            startDevicesScreen();
                        } else {
                            loading.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(mCoorRegister, R.string.autoLoginFailed, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            startLogin();
                        }
                    }
                } catch (JSONException e){
                    Snackbar snackbar = Snackbar
                            .make(mCoorRegister, R.string.autoLoginFailed, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    loading.dismiss();
                    startLogin();
                    Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar
                        .make(mCoorRegister, R.string.autoLoginFailed, Snackbar.LENGTH_LONG);
                snackbar.show();
                startLogin();
                loading.dismiss();
                Log.e(TAG, "VolleyError occurred with details : "+error.toString());
            }
        });
    }

    private void startLogin(){
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    private void startDevicesScreen() {
        Intent devicesIntent;
        switch (selectedDeviceType){
            case ApiKeys.AIROWL_3G_TYPE:
                devicesIntent = new Intent(this, AddDeviceAirOwlCell.class);
                break;
            case ApiKeys.AIROWL_WI_TYPE:
                devicesIntent = new Intent(this, AddDeviceAirOwlWifi.class);
                break;
            case ApiKeys.BREATH_I_TYPE:
                devicesIntent = new Intent(this, AddDeviceBreathi.class);
                break;
            case ApiKeys.POLLUDRON_TYPE:
                devicesIntent = new Intent(this, AddDevicePolludrone.class);
                break;
            case ApiKeys.GLOBAL_BASIC_PROFILE:
                devicesIntent = new Intent(this, BasicProfile.class);
                break;
            default:
                devicesIntent = new Intent(this, AddDevice.class);
                break;
        }
        startActivity(devicesIntent);
    }
}
