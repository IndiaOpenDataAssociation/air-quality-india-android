package com.idn0phl3108ed43d22s30.Ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idn0phl3108ed43d22s30.AirOwlWi.AirOwlId;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.PrefManager;

public class TutorialAirOwlWelcome extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private ViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTImeLaunchTut()) {
            launchAirOwlid();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_tutorial_air_owl_welcome);
        ConnfigView();
    }

    private void setOnclickEvent() {
        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
    }

    private void ConnfigView() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        layouts = new int[]{
                R.layout.tutorial_screen1,
                R.layout.tutorial_screen2,
                R.layout.tutorial_screen3,
                R.layout.tutorial_screen4,
        };
        setOnclickEvent();

        addBottomDots(0);
        changeStatusBarColor();
        myViewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            nextClick();
        } else if (v == btnSkip) {
            skipClick();
        }
    }

    //On click events
    private void nextClick() {
        int current = getItem(+1);
        if (current < layouts.length) {
            viewPager.setCurrentItem(current);
        } else {
            launchAirOwlid();
        }
    }

    private void skipClick() {
        launchAirOwlid();
    }

    //View pager adapter ....

    private class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        public ViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    /**
     * View page click listener to selecting new layout..
     **/

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchAirOwlid() {

        prefManager.setIsFirstTimeLaunchTut(false);
        Intent airOwlid = new Intent(getApplicationContext(), AirOwlId.class);
        startActivity(airOwlid);
        finish();
    }
}
