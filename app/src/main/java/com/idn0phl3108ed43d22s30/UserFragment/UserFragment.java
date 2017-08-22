package com.idn0phl3108ed43d22s30.UserFragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.idn0phl3108ed43d22s30.GlobalActivity;
import com.idn0phl3108ed43d22s30.MainActivity;
import com.idn0phl3108ed43d22s30.R;

public class UserFragment extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fragment);
        configView();
        setUpToolbar();


        getSupportFragmentManager().addOnBackStackChangedListener(getListener());
        setCurrentFragment();
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount == 0) {
                        finish();
                    }
                    Fragment fragment = manager.getFragments()
                            .get(backStackEntryCount - 1);
                    fragment.onResume();
                }
            }
        };
        return result;
    }

    private void setCurrentFragment() {
        Fragment AddDevices = new AddDevicesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView_user, AddDevices, null);
        fragmentTransaction.commit();
    }

    private void configView() {
        mHeader = (TextView) findViewById(R.id.txtHeader);
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
