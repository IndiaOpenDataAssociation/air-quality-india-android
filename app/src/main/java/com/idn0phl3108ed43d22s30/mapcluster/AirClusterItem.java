package com.idn0phl3108ed43d22s30.mapcluster;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by jimish on 16/9/16.
 */

public class AirClusterItem implements ClusterItem {
    private LatLng deviceLatLong;
    private String deviceLabel;

    public AirClusterItem(LatLng deviceLatLong, String deviceLabel) {
        this.deviceLatLong = deviceLatLong;
        this.deviceLabel = deviceLabel;
    }

    public AirClusterItem() {
    }

    public AirClusterItem createClusterFromLatLong(double latitude, double longitude, String label){
        AirClusterItem clusterItem = new AirClusterItem();
        LatLng latLng = new LatLng(latitude,longitude);

        return clusterItem;
    }

    public LatLng getDeviceLatLong() {
        return deviceLatLong;
    }

    public void setDeviceLatLong(LatLng deviceLatLong) {
        this.deviceLatLong = deviceLatLong;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }

    @Override
    public LatLng getPosition() {
        return null;
    }
}
