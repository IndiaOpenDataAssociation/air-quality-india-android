package com.idn0phl3108ed43d22s30;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.idn0phl3108ed43d22s30.pojo.AirPollutionData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PollutionMap extends AppCompatActivity {

    private MapView mMapView;
    private static final String TAG = "PollutionMap";
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pollution_map);
//        mMapView = (MapView) findViewById(R.id.mapView);
//        mMapView.onCreate(savedInstanceState);
       

    }


//    private void addAirPollutionDataToMap(ArrayList<AirPollutionData> airPollutionDatas, MapboxMap mapboxMap) {
//        for (int i = 0; i < airPollutionDatas.size(); i++) {
//            AirPollutionData deviceData = airPollutionDatas.get(i);
//            //Icon icon = getIconForAirQualityIndex(deviceData.getAqi());
//
////            mapboxMap.addMarker(new MarkerOptions()
////                    .position(new LatLng(deviceData.getLatitude(), deviceData.getLongitude()))
////                    .title(deviceData.getLabel())
////                    .snippet("AQI: " + deviceData.getAqi().toString()))
////                    .setIcon(icon);
//        }
//    }

//    private Icon getIconForAirQualityIndex(Integer aqi) {
//        IconFactory iconFactory = IconFactory.getInstance(PollutionMap.this);
//        Drawable iconDrawable;
//        //asds ad
//
//
//        if (!aqi.toString().isEmpty() && aqi.toString() != null) {
//            if (0 <= aqi.intValue() && aqi.intValue() <= 50) {
//                iconDrawable = ContextCompat.getDrawable(PollutionMap.this, R.drawable.good_map_marker);
//            } else if (50 < aqi.intValue() && aqi.intValue() <= 100) {
//                iconDrawable = ContextCompat.getDrawable(PollutionMap.this, R.drawable.satisfactory_map_marker);
//            } else if (100 < aqi.intValue() && aqi.intValue() <= 200) {
//                iconDrawable = ContextCompat.getDrawable(PollutionMap.this, R.drawable.moderate_map_marker);
//            } else if (200 < aqi.intValue() && aqi.intValue() <= 300) {
//                iconDrawable = ContextCompat.getDrawable(PollutionMap.this, R.drawable.poor_map_marker);
//            } else if (300 < aqi.intValue() && aqi.intValue() <= 400) {
//                iconDrawable = ContextCompat.getDrawable(PollutionMap.this, R.drawable.very_poor_map_marker);
//            } else if (400 < aqi.intValue() && aqi.intValue() <= 500) {
//                iconDrawable = ContextCompat.getDrawable(PollutionMap.this, R.drawable.severe_map_marker);
//            } else {
//                iconDrawable = ContextCompat.getDrawable(PollutionMap.this, R.drawable.moderate_map_marker);
//            }
//            Icon icon = iconFactory.fromDrawable(iconDrawable);
//            return icon;
//        } else {
//
//            Log.e(TAG, "Null");
//            return null;
//        }
//    }

    private ArrayList<AirPollutionData> parseAirPollutionData(JSONArray response) {
        JSONArray firstJsonArray = null;
        ArrayList<AirPollutionData> datas = new ArrayList<>();
        try {
            firstJsonArray = (JSONArray) response.get(0);
            for (int i = 0; i < firstJsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) firstJsonArray.get(i);
//                Gson gson = new GsonBuilder().create();
//                AirPollutionData deviceData = gson.fromJson(jsonObject.toString(), AirPollutionData.class);
                //datas.add(deviceData);
            }


        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return datas;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.om.demo.pollutionmap/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.om.demo.pollutionmap/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
