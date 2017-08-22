package com.idn0phl3108ed43d22s30;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by jimish on 29/9/16.
 */

public class OizomApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
