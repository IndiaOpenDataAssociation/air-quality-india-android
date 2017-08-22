package com.idn0phl3108ed43d22s30;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.idn0phl3108ed43d22s30.Breath_i.AddDeviceBreathiBuyPage;
import com.idn0phl3108ed43d22s30.LoginRegister.LoginActivity;

/**
 * Created by Rutul on 13-06-2016.
 */
public class FragmentMyDevices extends Fragment  {
    private View rootView;

    ImageView mAddDevice , mPurchase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_devices, container, false);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.tittle_myDevices);

        ConfigView();


        mAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addDevice = new Intent(getActivity(),LoginActivity.class);
                startActivity(addDevice);
            }
        });


        mPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent purchase = new Intent(getActivity(),AddDeviceBreathiBuyPage.class);
                startActivity(purchase);
            }
        });

        return rootView;
    }



    private void ConfigView() {
        mAddDevice = (ImageView) rootView.findViewById(R.id.imgAddDevice);
        mPurchase = (ImageView) rootView.findViewById(R.id.imgPurchase);
    }
}
