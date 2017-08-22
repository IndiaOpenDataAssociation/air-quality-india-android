package com.idn0phl3108ed43d22s30.TabFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idn0phl3108ed43d22s30.R;

/**
 * Created by Rutul on 22-07-2016.
 */
public class Indoor extends Fragment {
    private View rooView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rooView = inflater.inflate(R.layout.fragment_indoor, container, false);
        ConfigView();
        return rooView;
    }

    private void ConfigView() {

    }
}
