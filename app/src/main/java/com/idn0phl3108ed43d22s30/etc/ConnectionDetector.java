package com.idn0phl3108ed43d22s30.etc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Rutul on 20-06-2016.
 */
public class ConnectionDetector  {
    private Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED || info[i].getState()==NetworkInfo.State.CONNECTING)
                    {
                        return true;
                    }

        }
        return false;
    }

    public boolean isConnectingToMobileData(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean haveConnectedMobile = false;
        NetworkInfo[] netInfo = connectivity.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedMobile;
    }

}
