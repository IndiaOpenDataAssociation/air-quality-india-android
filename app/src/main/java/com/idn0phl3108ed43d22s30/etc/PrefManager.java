package com.idn0phl3108ed43d22s30.etc;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Rutul on 18-06-2016.
 */
public class PrefManager {

    SharedPreferences pref, pref2, pref3;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AirQualityIndia-welcome";
    private static final String PREF_NAME2 = "AirQualityIndia-turorial";
    private static final String PREF_NAME3 = "AirQualityIndia-turorial2";


    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FIRST_TIME_LAUNCH_TUT = "IsSecondTimeLaunch";
    private static final String IS_FIRST_TIME_LAUNCH_TUT_ = "IsSecondTimeLaunchBreathi";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        pref2 = _context.getSharedPreferences(PREF_NAME2, PRIVATE_MODE);
        editor = pref2.edit();
        pref3 = _context.getSharedPreferences(PREF_NAME3, PRIVATE_MODE);
        editor = pref3.edit();
    }

    public void setIsFirstTimeLaunchTut(boolean isTutorial) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH_TUT, isTutorial);
        editor.commit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setFirstTimeLaunchBreathi(boolean isTutorialBreathi) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH_TUT_, isTutorialBreathi);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isFirstTImeLaunchTut() {
        return pref2.getBoolean(IS_FIRST_TIME_LAUNCH_TUT, true);
    }


    public boolean isFirstTimeLaunchbreathi() {
        return pref2.getBoolean(IS_FIRST_TIME_LAUNCH_TUT_, true);
    }
}
