package com.idn0phl3108ed43d22s30.Ui;

import android.app.ProgressDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChangePasswordActivity";

    private Toolbar toolbar;
    protected EditText mCurrentPass, mNewPass, mConformPass;
    public String currentPass, newPass, conformPass;
    public CoordinatorLayout mCoordinatorChangePass;
    public Button mbtnSave;
    public CheckBox checkBox;

    private com.idn0phl3108ed43d22s30.pojo.ChangePassword changePassword;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setupToolbar();
        ConfigView();
    }


    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Validation() {
        currentPass = mCurrentPass.getText().toString().trim();
        newPass = mNewPass.getText().toString().trim();
        conformPass = mConformPass.getText().toString().trim();


        if (currentPass.isEmpty() || newPass.isEmpty() || conformPass.isEmpty()) {
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorChangePass, R.string.errorBlankChange, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (currentPass.equals(newPass)) {
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorChangePass, R.string.errorEqual, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (!newPass.equals(conformPass)) {
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorChangePass, R.string.errorNotEqal, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (newPass.length() < 5) {
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorChangePass, R.string.errorPasswordlength, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            passwordChange();
        }


    }

    private void passwordChange() {
        ///TODO main function
        final ProgressDialog dialog = ProgressDialog.show(this, "Updating..", "Please wait..", false, false);
        changePassword.setPassword(newPass);
        changePassword.setOldPassword(currentPass);
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", this);
        changePassword.setUserId(userId);
        ECAPIHelper.changePassword(this, changePassword.toJson(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        int status = response.getInt("status");
                        if (status != 200) {
                            dialog.dismiss();
                            showServerFailError();
                            Log.e(TAG, "Server responded with non 200 status code with response : " + response.toString());
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(mCoordinatorChangePass, R.string.successMesg, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            //Toast.makeText(ChangePassword.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        showServerFailError();
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                    }
                } else {
                    dialog.dismiss();
                    showServerFailError();
                    Log.e(TAG, "Null response received from server");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                showServerFailError();
                Log.e(TAG, "Volly error occurred with details : " + error.toString());
            }
        });
    }

    private void showServerFailError() {
        Snackbar snackbar = Snackbar
                .make(mCoordinatorChangePass, R.string.errorServerChangePassword, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void ConnectionCheck() {
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            Validation();
        } else {
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorChangePass, R.string.errorInternet, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    private void ConfigView() {
        changePassword = new com.idn0phl3108ed43d22s30.pojo.ChangePassword();
        mCurrentPass = (EditText) findViewById(R.id.etCurrentPass);
        mNewPass = (EditText) findViewById(R.id.etNewPass);
        mConformPass = (EditText) findViewById(R.id.etConformPass);
        mbtnSave = (Button) findViewById(R.id.btnApply);
        mbtnSave.setOnClickListener(this);
        mCoordinatorChangePass = (CoordinatorLayout) findViewById(R.id.main_content);
        cd = new ConnectionDetector(getApplicationContext());
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mConformPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mCurrentPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    mNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mConformPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mCurrentPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        ConnectionCheck();
    }
}
