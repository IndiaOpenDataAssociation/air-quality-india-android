package com.idn0phl3108ed43d22s30.LoginRegister;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.idn0phl3108ed43d22s30.AirOwlCell.AddDeviceAirOwlCell;
import com.idn0phl3108ed43d22s30.AirOwlWi.AddDeviceAirOwlWifi;
import com.idn0phl3108ed43d22s30.BasicProfile;
import com.idn0phl3108ed43d22s30.Breath_i.AddDeviceBreathi;
import com.idn0phl3108ed43d22s30.MainActivity;
import com.idn0phl3108ed43d22s30.Polludrone.AddDevicePolludrone;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.Ui.AddDevice;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.Device;
import com.idn0phl3108ed43d22s30.pojo.Login;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idn0phl3108ed43d22s30.pojo.Payload;
import com.idn0phl3108ed43d22s30.pojo.Signup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";

    private EditText email;
    private EditText password;
    private Button loginBtn;
    private CoordinatorLayout mCordiLogin;
    private Context context;
    private CheckBox mCheckBox;
    private TextView signUpLink, logout;
    private Signup signup;
    private Login login;
    private CoordinatorLayout mCoorRegister;
    private ProgressDialog mProgressDialog;

    Boolean isInternetPresent = false;
    private boolean loggedIn = false;
    ConnectionDetector cd;

    private JSONArray devicesArray;

    private String selectedDeviceType, googleEmail, facebookEmail, googlePassword = "google", facebookPassword = "facebook";

    private static SQLiteDatabaseHelper dbHelper;

    //Google sign up
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;

    //private SignInButton btnSignIn;
    private ImageView btnSignIn, btnFLogin;

    //FacebookSign up
    public AccessTokenTracker tracker;
    public ProfileTracker profileTracker;
    public LoginButton mfaceBook;

    private CallbackManager mCallbackManager;  //to Handle callback from facebook

    //Main callback for login
    private FacebookCallback<LoginResult> mCallbackLogin = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            //To get email
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.i("LoginActivity", response.toString());
                            try {
                                String email = object.getString("email");
                                facebookEmail = email;
                                String id = object.getString("id");
                                String name = object.getString("name");
                                Log.i(TAG, " Email:" + facebookEmail + " id:" + id + " Name:" + name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "JavaException error " + e.toString());
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
            // TODO: 08-11-2016 loginSuccess with facebook
            //   registerUserFacebook();
            Toast.makeText(getApplicationContext(), "Login success with facebook", Toast.LENGTH_LONG).show();
            //loginSuccess();
        }

        @Override
        public void onCancel() {
            Log.e(TAG, "Facebook registration Cancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.e(TAG, "FacebookException with detail:" + error.toString());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For no status bar..
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        FacebookSdk.sdkInitialize(getApplicationContext());//To get access of facebook SDK
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        mCallbackManager = CallbackManager.Factory.create();

//        mfaceBook = (LoginButton) findViewById(R.id.login_button);
//        mfaceBook.setReadPermissions("email");
//        mfaceBook.setReadPermissions(Arrays.asList(
//                "public_profile", "email", "user_birthday", "user_friends"));
//        mfaceBook.setOnClickListener(this);
//        mfaceBook.registerCallback(mCallbackManager, mCallbackLogin);

        LoginManager.getInstance().registerCallback(mCallbackManager, mCallbackLogin);//To manage login
        //TO track token if user is change or not
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        //To track the user profile if profile is change or not
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                if ( currentProfile.toString() != null && !currentProfile.toString().isEmpty()) {
//                    Log.i(TAG, "Facebook loginDetail currentProfile :" + " Name:" + currentProfile.getName() + " ID:" + currentProfile.getId() + " Uri" + currentProfile.getLinkUri());
//                }
            }
        };

        tracker.startTracking();
        profileTracker.startTracking();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedDeviceType = extras.getString(ApiKeys.SHARED_DEVICE_TYPE);
        } else {
            selectedDeviceType = "";
        }

        context = getApplicationContext();

        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());
        login = new Login();
        initConfigView();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void initConfigView() {
        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);
        signUpLink = (TextView) findViewById(R.id.signUpLink);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        mCoorRegister = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        btnSignIn = (ImageView) findViewById(R.id.btn_sign_in);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternetConnectivity();
            }
        });

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignupActivity();
            }
        });
        cd = new ConnectionDetector(getApplicationContext());

        mCordiLogin = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        btnSignIn.setOnClickListener(this);
        // TODO: 08-11-2016 Delete logout button
        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(this);

        btnFLogin = (ImageView) findViewById(R.id.login_f);

        btnFLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList(
                        "public_profile", "email", "user_birthday", "user_friends"));
            }
        });
    }

    private void startSignupActivity() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    private void checkInternetConnectivity() {
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            validateEmailPwd();

        } else {
            Snackbar snackbar = Snackbar
                    .make(mCordiLogin, R.string.errorInternet, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }


    private void validateEmailPwd() {
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();


        if (emailStr.isEmpty() || passwordStr.isEmpty()) {
            Snackbar snackbar = Snackbar
                    .make(mCordiLogin, R.string.errorBlank, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (!(isValidEmail(emailStr))) {
            Snackbar snackbar = Snackbar
                    .make(mCordiLogin, R.string.errorEmail, Snackbar.LENGTH_LONG);
            snackbar.show();

        } else if (passwordStr.isEmpty()) {
            Snackbar snackbar = Snackbar
                    .make(mCordiLogin, R.string.errorPass, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            performServerCallToLogin();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isValidEmail(String username) {

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    private Boolean checkLogin() {
        String userId = UserPrefrenceUtils.getValueFromUserSharedPrefs("userId", this);
        if (userId != null) {
            return true;
        } else {
            return false;
        }
    }

    private void performServerCallToLogin() {
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        final ProgressDialog loading = ProgressDialog.show(this, "Authenticating...", "Please wait...", false, false);
        if (emailStr != null || passwordStr != null) {
            login.setEmail(emailStr);
            login.setPassword(passwordStr);
            final JSONObject jsonObject = login.toJson();
            ECAPIHelper.login(jsonObject, this, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response != null) {
                            int status = response.getInt("status");
                            if (status == 200) {

                                String userId = response.getString("userId");
                                Log.e(TAG, "UserId" + userId);
                                jsonObject.put("userId", userId);
                                SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                //Adding values to editor
                                editor.putBoolean(Constants.LOGGEDIN_SHARED_PREF, true);
                                editor.putString(Constants.SHARED_ID, userId);
                                editor.commit();
                                fetchDevicesList();
                            } else {
                                loading.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(mCordiLogin, R.string.errorLogin, Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    } catch (Exception e) {
                        loading.dismiss();
                        Log.e(TAG, "Exception occurred with details : " + e.toString());
                        Snackbar snackbar = Snackbar
                                .make(mCordiLogin, R.string.errorLogin, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar snackbar = Snackbar
                            .make(mCordiLogin, R.string.errorLogin, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    loading.dismiss();
                    Log.e(TAG, "Volley Error occurred with details : " + error.toString());
                }
            });
            // ECAPIHelper.performServerCallToLogin();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        tracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopTracking();
        profileTracker.stopTracking();
    }

    private void fetchDevicesList() {
        final ProgressDialog loading = ProgressDialog.show(this, "Fetching personal Devices...", "Please wait...", false, false);

        ECAPIHelper.getDevicesListArray(this, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response != null) {
                        devicesArray = response;
                        loading.dismiss();
                        dbHelper.updatePrivateDataArray(response);
                        dbHelper.updateLatestPrivateObj(devicesArray.getJSONObject(0));
                        startDevicesActivity();
                    } else {
                        devicesArray = null;
                        Log.d(TAG, "no devices registered of user");
                        loading.dismiss();
                        startDevicesActivity();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                devicesArray = null;
                Log.e(TAG, "Volley error occurred with details : " + error.toString());
                loading.dismiss();
                startDevicesActivity();
            }
        });
    }

    public void startDevicesActivity() {
        Intent devicesIntent;
        if (devicesArray != null && devicesArray.length() > 0) {
            devicesIntent = new Intent(this, PrivateGlobalActivity.class);
            try {
                ObjectMapper mapper = new ObjectMapper();
                Device device = mapper.readValue(devicesArray.get(0).toString(), Device.class);
                Payload payload = device.getPayload();
                String payloadStr = mapper.writeValueAsString(payload);
                UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, payloadStr, this);
            } catch (JsonParseException e) {
                devicesIntent = new Intent(this, BasicProfile.class);
            } catch (JSONException e) {
                devicesIntent = new Intent(this, BasicProfile.class);
            } catch (JsonMappingException e) {
                devicesIntent = new Intent(this, BasicProfile.class);
            } catch (IOException e) {
                devicesIntent = new Intent(this, BasicProfile.class);
            }


        } else {
            switch (selectedDeviceType) {
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
        }

        //For remove back activity
        devicesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        devicesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(devicesIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, null, getApplicationContext());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSignIn) {
            signIn();
        } else if (v == logout) {
            signOut();   //Remove logout this is for Google logout
        } else if (v == mfaceBook) {
            faceBookLogin();
        }
    }

    private void faceBookLogin() {

    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //Main code
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            googleEmail = acct.getEmail();
            // TODO: 25-10-2016 Google signup
            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);

            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            // TODO: 08-11-2016  loginSuccess google methode
//            updateUI(true);

            //     registerUser();
            //   updateUI(true);

        } else {
            // Signed out, show unauthenticated UI.
            //  updateUI(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        //Facebook login
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
        }
    }

    //signout code
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void loginSuccess() {
        Intent sa = new Intent(getApplicationContext(), Register.class);
        startActivity(sa);
    }

    //Register user with gamil
    private void registerUser() {
        signup = new Signup(googleEmail, googlePassword);
        final JSONObject jsonObject = signup.toJson();
        final ProgressDialog loading = ProgressDialog.show(this, "Registering User...", "Please wait...", false, false);

        ECAPIHelper.signUpRequest(this, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        int status = response.getInt("status");
                        if (status == 201) {
                            loading.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(mCoorRegister, R.string.successRegister, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            //User register success...
                            performLogin();
                        } else {
                            loading.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(mCoorRegister, R.string.failedToRegister, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } catch (JSONException e) {
                        loading.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(mCoorRegister, R.string.failedToRegister, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        Log.e(TAG, "JSON Excpetion occurred with details : " + e.toString());
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
                Log.e(TAG, "Volley Error occurred with details : " + error.toString());
            }
        });
    }

    private void performLogin() {
        Login login = new Login(googleEmail, googlePassword);
        final JSONObject jsonObject = login.toJson();
        final ProgressDialog loading = ProgressDialog.show(this, "Logging In...", "Please wait...", false, false);

        ECAPIHelper.login(jsonObject, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        int status = response.getInt("status");
                        if (status == 200) {
                            String id = response.getString("userId");
                            jsonObject.put("userId", id);
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
                            //   startLogin();
                        }
                    }
                } catch (JSONException e) {
                    Snackbar snackbar = Snackbar
                            .make(mCoorRegister, R.string.autoLoginFailed, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    loading.dismiss();
                    //  startLogin();
                    Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar
                        .make(mCoorRegister, R.string.autoLoginFailed, Snackbar.LENGTH_LONG);
                snackbar.show();
                //  startLogin();
                loading.dismiss();
                Log.e(TAG, "VolleyError occurred with details : " + error.toString());
            }
        });
    }

    //Register user with facebook
    private void registerUserFacebook() {
        signup = new Signup(facebookEmail, facebookPassword);
        final JSONObject jsonObject = signup.toJson();
        final ProgressDialog loading = ProgressDialog.show(this, "Registering User...", "Please wait...", false, false);

        ECAPIHelper.signUpRequest(this, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        int status = response.getInt("status");
                        if (status == 201) {
                            loading.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(mCoorRegister, R.string.successRegister, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            //User register success...
                            performLoginwith();
                        } else {
                            loading.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(mCoorRegister, R.string.failedToRegister, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } catch (JSONException e) {
                        loading.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(mCoorRegister, R.string.failedToRegister, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        Log.e(TAG, "JSON Excpetion occurred with details : " + e.toString());
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
                Log.e(TAG, "Volley Error occurred with details : " + error.toString());
            }
        });
    }

    private void performLoginwith() {
        Login login = new Login(facebookEmail, facebookPassword);
        final JSONObject jsonObject = login.toJson();
        final ProgressDialog loading = ProgressDialog.show(this, "Logging In...", "Please wait...", false, false);

        ECAPIHelper.login(jsonObject, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        int status = response.getInt("status");
                        if (status == 200) {
                            String id = response.getString("userId");
                            jsonObject.put("userId", id);
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
                            //   startLogin();
                        }
                    }
                } catch (JSONException e) {
                    Snackbar snackbar = Snackbar
                            .make(mCoorRegister, R.string.autoLoginFailed, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    loading.dismiss();
                    //  startLogin();
                    Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar
                        .make(mCoorRegister, R.string.autoLoginFailed, Snackbar.LENGTH_LONG);
                snackbar.show();
                //  startLogin();
                loading.dismiss();
                Log.e(TAG, "VolleyError occurred with details : " + error.toString());
            }
        });
    }


    private void startDevicesScreen() {
        Intent devicesIntent;
        switch (selectedDeviceType) {
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

    private void startLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }
}
