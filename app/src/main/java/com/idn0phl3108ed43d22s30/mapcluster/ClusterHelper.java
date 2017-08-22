package com.idn0phl3108ed43d22s30.mapcluster;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.pojo.Device;

import java.util.List;

/**
 * Created by jimish on 16/9/16.
 */

public class ClusterHelper {
    private static final String TAG = "ClusterHelper";

    private Context context;
    private GoogleMap map;
    private ClusterManager<AirClusterItem> clusterManager;
    private List<Device> devices;

    public ClusterHelper(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
    }

    public ClusterHelper() {

    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    private void setUpCluster(){
        if(map!=null){
            //position the google map
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.CENTER_LATITUDE,
                    Constants.CENTER_LONGITUDE), Constants.ZOOM_LEVEL_MAP));

            //initialize the manager with the context and map
            //context and maps should be passed in constructor to help this manager
            clusterManager = new ClusterManager<AirClusterItem>(context, map);
        }
    }

    //add cluster items
    private void addItems(){
        int devicesLength = devices.size();
        if(devicesLength>0){
            for(int i=0; i<devicesLength; i++){
                //new latlong from devices latitude and longitude.
                //saving device label for safe side
                LatLng latLng = new LatLng(devices.get(i).getLatitude(),
                        devices.get(i).getLongitude());

                //new item as per device
                AirClusterItem airClusterItem = new AirClusterItem(latLng, devices.get(i).getLabel());
                //clusterManager.addItem(airClusterItem);
            }
        } else {
            Log.d(TAG, "Devices Length is less than 1, Thus no devices");
        }
    }
}
