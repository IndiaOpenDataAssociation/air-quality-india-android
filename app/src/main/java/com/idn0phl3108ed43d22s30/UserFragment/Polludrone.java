package com.idn0phl3108ed43d22s30.UserFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idn0phl3108ed43d22s30.R;

/**
 * Created by Rutul on 05-08-2016.
 */
public class Polludrone extends Fragment implements View.OnClickListener {
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_activity_add_device_polludrone, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Device");
        configView();
        setOnclickEvent();
        return rootView;
    }

    private void configView() {

    }

    private void setOnclickEvent() {

    }

    @Override
    public void onClick(View v) {

    }
}
