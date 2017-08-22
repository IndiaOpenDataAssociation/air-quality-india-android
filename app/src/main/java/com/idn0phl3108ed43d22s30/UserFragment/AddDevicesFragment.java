package com.idn0phl3108ed43d22s30.UserFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.idn0phl3108ed43d22s30.MainActivity;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;

/**
 * Created by Rutul on 05-08-2016.
 */
public class AddDevicesFragment extends Fragment implements View.OnClickListener {
    View rootView;
    private ImageView mAirOWl, mAirOwlWi, mBreathi, mPolludrone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_add_device, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Device");
        configView();
        setOnclickEvent();
        return rootView;
    }

    private void setOnclickEvent() {
        mAirOWl.setOnClickListener(this);
        mAirOwlWi.setOnClickListener(this);
        mPolludrone.setOnClickListener(this);
        mBreathi.setOnClickListener(this);
    }

    private void configView() {

        mAirOwlWi = (ImageView) rootView.findViewById(R.id.imgairOwlWifi);
        mAirOWl = (ImageView) rootView.findViewById(R.id.imgairOwlCell);
        mBreathi = (ImageView) rootView.findViewById(R.id.imgPolludrone);
        mPolludrone = (ImageView) rootView.findViewById(R.id.imgBreath_i);
    }

    @Override
    public void onClick(View v) {
        if (v == mAirOWl) {
            AirOWl();
        } else if (v == mAirOwlWi) {
            AirOWlWi();
        } else if (v == mBreathi) {
            Breathi();
        } else if (v == mPolludrone) {
            Polludrone();
        }
    }

    private void AirOWl() {
        Fragment airOWl = new AirOwl();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView_user, airOWl, null);
        fragmentTransaction.commit();
    }

    private void Breathi() {
        Fragment breathi = new Breathi();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView_user, breathi, null);
        fragmentTransaction.commit();
    }

    private void Polludrone() {
        Fragment polludrone = new Polludrone();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView_user, polludrone, null);
        fragmentTransaction.commit();
    }

    private void AirOWlWi() {
        Fragment AddDevices = new AirOwlWi();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView_user, AddDevices, null);
        fragmentTransaction.commit();

    }
}
