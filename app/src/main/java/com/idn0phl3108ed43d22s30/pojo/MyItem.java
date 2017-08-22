package com.idn0phl3108ed43d22s30.pojo;

/**
 * Created by rohanpc on 2/3/2016.
 */


import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private String label, country, city;
    private int aqi;

    public MyItem(double latitude, double longitude, String label, String country, String city, int aqi) {
        mPosition = new LatLng(latitude, longitude);
        this.label = label;
        this.country = country;
        this.city = city;
        this.aqi = aqi;
    }

    public String getLabel() {
        return label;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public int getAqi() {
        return aqi;
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}

class MyClusterRenderer extends DefaultClusterRenderer<MyItem> {

    public MyClusterRenderer(Context context, GoogleMap map,
                             ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        markerOptions.title(item.getLabel());
        markerOptions.snippet(item.getCity() + item.getAqi());

    }

    @Override
    protected void onClusterItemRendered(final MyItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

}