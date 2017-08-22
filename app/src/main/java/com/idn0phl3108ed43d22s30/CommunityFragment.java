package com.idn0phl3108ed43d22s30;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idn0phl3108ed43d22s30.TabFragment.Blogs;
import com.idn0phl3108ed43d22s30.TabFragment.FragmentDevices;
import com.idn0phl3108ed43d22s30.TabFragment.Knowledge;

import java.util.ArrayList;

/**
 * Created by jimish on 29/6/16.
 */

public class CommunityFragment extends Fragment {
    private static final String TAG = "CommunityFragment";

    private static final String POSITION = "position";

    private View rootView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FragmentManager childFragmentMangaer;

    protected FragmentManager getChildSupportFragmentManager() {
        if (childFragmentMangaer != null) {
            return childFragmentMangaer;
        } else {
            return getActivity().getSupportFragmentManager();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_community, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.tittle_community);

        mViewPager = (ViewPager) rootView.findViewById(R.id.container);

        final ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new Blogs());
        fragmentList.add(new FragmentDevices());
        fragmentList.add(new Knowledge());


        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        mTabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTabLayout.getTabAt(0).setText("BLOG");
        mTabLayout.getTabAt(1).setText("DEVICES");
        mTabLayout.getTabAt(2).setText("KNOWLEDGE");

        //setupTabLayout(mTabLayout);
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
