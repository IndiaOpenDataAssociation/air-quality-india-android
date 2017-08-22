package com.idn0phl3108ed43d22s30;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idn0phl3108ed43d22s30.Ui.ActivityProfile;
import com.idn0phl3108ed43d22s30.LoginRegister.LoginActivity;
import com.idn0phl3108ed43d22s30.Ui.AboutUs;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.etc.Constants;

import org.json.JSONArray;

/**
 * Created by Rutul on 27-06-2016.
 */
public class FragmentProfile extends Fragment implements View.OnClickListener {
    private View rootView;
    private TextView mProfile, mDevice, mAboutUs, mNotification, mActivity;
    private CoordinatorLayout coordinatorLayout;
    private View deviceBorder, deviceBorderNotification, activityBorder;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.tittle_profile);

        ConfigView();
        onCLick();
        return rootView;
    }

    private void onCLick() {
        mProfile.setOnClickListener(this);
        mDevice.setOnClickListener(this);
        mActivity.setOnClickListener(this);
        mAboutUs.setOnClickListener(this);
        mNotification.setOnClickListener(this);
    }

    private void ConfigView() {


        mProfile = (TextView) rootView.findViewById(R.id.txtbasicProfile);
        mDevice = (TextView) rootView.findViewById(R.id.txtdevice);

        mActivity = (TextView) rootView.findViewById(R.id.txtactivity);
        deviceBorder = (View) rootView.findViewById(R.id.deviceBorder);
        deviceBorderNotification = (View) rootView.findViewById(R.id.deviceBorder_notification);
        activityBorder = rootView.findViewById(R.id.activityBorder);
        mAboutUs = (TextView) rootView.findViewById(R.id.txtaboutUs);
        if (getActivity() instanceof MainActivity) {
            mDevice.setVisibility(View.GONE);
            deviceBorder.setVisibility(View.GONE);
        }
        // TODO: 22-07-2016 Uncomment bellow code .
//        if (getActivity() instanceof MainActivity) {
//            mActivity.setVisibility(View.GONE);
//            activityBorder.setVisibility(View.GONE);
//        }
        mNotification = (TextView) rootView.findViewById(R.id.notification);

        if (getActivity() instanceof MainActivity) {
            mNotification.setVisibility(View.GONE);
            deviceBorderNotification.setVisibility(View.GONE);
        }
        cd = new ConnectionDetector(getActivity().getApplicationContext());

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
    }

    private boolean checkLoggedIn() {
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean loggedIn = sharedPreferences.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false);
        return loggedIn;
    }

    public void Profile() {
        boolean flag = checkLoggedIn();
        if (flag) {
            Intent profile = new Intent(getActivity(), BasicProfile.class);
            profile.putExtra(ApiKeys.SHARED_DEVICE_TYPE, ApiKeys.GLOBAL_BASIC_PROFILE);
            startActivity(profile);
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

    }

    private void Device() {
        Intent device = new Intent(getActivity(), ProfileManageDevice.class);
        if (getActivity() instanceof MainActivity) {
            JSONArray jsonArray = ((MainActivity) getActivity()).getDevicesArray();
            device.putExtra(ApiKeys.PROFILE_MANAGE_DATA, jsonArray.toString());
            device.putExtra(ApiKeys.PROFILE_MANAGE_INTENT, ApiKeys.PROFILE_MANAGE_PUBLIC);
        } else {
            JSONArray jsonArray = ((PrivateGlobalActivity) getActivity()).getDevicesArray();
            device.putExtra(ApiKeys.PROFILE_MANAGE_DATA, jsonArray.toString());
            device.putExtra(ApiKeys.PROFILE_MANAGE_INTENT, ApiKeys.PROFILE_MANAGE_PRIVATE);
        }
        startActivity(device);
    }

    private void Activity() {
        Intent activity = new Intent(getActivity(), ActivityProfile.class);
        startActivity(activity);
    }

    private void AboutUS() {
        Intent aboutUs = new Intent(getActivity(), AboutUs.class);
        startActivity(aboutUs);
    }


    private void Notification() {
        Intent notification = new Intent(getActivity(), NotifiactionActivity.class);
        startActivity(notification);
    }

    private void ConnectionCheck() {
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            //AboutUS();
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, R.string.errorInternet, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }


    @Override
    public void onClick(View v) {
        if (v == mProfile) {
            Profile();
        } else if (v == mDevice) {
            Device();
        } else if (v == mActivity) {
            Activity();
        } else if (v == mNotification) {
            Notification();
        } else {
            AboutUS();
        }
    }
}
