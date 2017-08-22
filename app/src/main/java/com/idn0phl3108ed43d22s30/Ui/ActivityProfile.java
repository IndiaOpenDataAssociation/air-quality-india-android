package com.idn0phl3108ed43d22s30.Ui;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.TabFragment.Indoor;
import com.idn0phl3108ed43d22s30.TabFragment.Outdoor;

import java.util.ArrayList;
import java.util.List;

public class ActivityProfile extends AppCompatActivity {

    private static final String TAG = ActivityProfile.class.getSimpleName();

    private ViewPager mViewPager;
    private TabLayout mTabLayout;


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        setupToolbar();
        ConfigView();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ACTIVITIES");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ConfigView() {
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Indoor(), "INDOOR");
        adapter.addFragment(new Outdoor(), "OUTDOOR");
        mViewPager.setAdapter(adapter);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
