package com.idn0phl3108ed43d22s30;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.Ui.AddCity;
import com.idn0phl3108ed43d22s30.Ui.ManageDevice;
import com.idn0phl3108ed43d22s30.Ui.MyDevices;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.pojo.Device;
import com.idn0phl3108ed43d22s30.pojo.MyItem;
import com.idn0phl3108ed43d22s30.pojo.getmarkerfromstring;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener {

    private Location mCurrentLocation;


    private static final String TAG = "MapActivity";
    private GoogleMap mGoogleMap;
    private Toolbar toolbar;
    //For json data
    //   static final LatLng Location = new LatLng(-1.782877, 90.381806);
    List<Device> items = new ArrayList<Device>();
    Double late, lng;
    ArrayList<getmarkerfromstring> users = new ArrayList<getmarkerfromstring>();
    String[] countries = new String[576];
    private ClusterManager<Device> mClusterManager;
    private GoogleApiClient mGoogleApiClient;


    private List<Device> mbList = new ArrayList<>();
    private HashMap<Marker, Device> mMarkersHashMap;
    Marker mark;

    private LocationSource.OnLocationChangedListener listener;

    private String label;
    private String city;
    private String country;
    private String _temp;
    private String time, deviceId;
    int aqi;

    private RelativeLayout mRelativeLayout;

    double lat, lon, currentLat, currentLong;

    private SQLiteDatabaseHelper dbHelper;
    JSONArray devicesArray, _jsonArray;
    ObjectMapper mapper = new ObjectMapper();
    JSONObject object;
    private boolean firstTimeCall = true;
    AlertDialog.Builder alertDialog;

    //Location request means how frequently w   e need updated location
    private LocationRequest request = LocationRequest.create().setInterval(60000)
            .setFastestInterval(60).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    //To pick nearest place


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        configView();
        getToolbar();

        alertDialog = new AlertDialog.Builder(MapActivity.this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            _temp = extras.getString(Constants.CITY_LIST_RESPONSE_KEY);
        } else {
            Log.e(TAG, "Something wnt wrong");
        }
        try {
            // JSONObject obj = new JSONObject(_temp);
            _jsonArray = new JSONArray(_temp);
            Log.i(TAG, "Response object :" + _jsonArray.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSONException occur object:" + e.toString());
        }

        Log.i(TAG, "Response " + _temp.toString());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void configView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relMapActivity);
    }


    //private plot marker


    public void sendDeviceInfroFromLatLong(LatLng selectedMarker) {
        int i;
        double lat, lon;
        lat = selectedMarker.latitude;
        lon = selectedMarker.longitude;
        try {
            for (i = 0; i < devicesArray.length(); i++) {
                JSONObject jsonObject = devicesArray.getJSONObject(i);
                double obj_lat = jsonObject.getDouble(ApiKeys.DEVICES_LATITUDE);
                double obj_lon = jsonObject.getDouble(ApiKeys.DEVICES_LONGITUDE);
                if (lat == obj_lat && lon == obj_lon) {
                    sendDeviceData(jsonObject);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        }

    }

    private void sendDeviceData(JSONObject selectedDevice) {
        boolean isPublicRegistered = dbHelper.isPublicDeviceRegistered(selectedDevice);
        if (!isPublicRegistered) {
            dbHelper.updatePublicData(selectedDevice);
        }
        dbHelper.updateLatestPublicObj(selectedDevice);
        startPublicGlobalActivity();
    }


    //TO start public activity
    private void startPublicGlobalActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, null, getApplicationContext());
        startActivity(intent);
    }

    //Calling toolbar
    private void getToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("MAPVIEW");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

    //To see menu view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_list:
                startListView();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    //To start addcity list
    private void startListView() {
        Intent listView = new Intent(this, AddCity.class);
        listView.putExtra(Constants.CITY_LIST_RESPONSE_KEY_MAP, _jsonArray.toString());
        firstTimeCall = true;
        startActivity(listView);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        // TODO: 02-02-2017 initialization of position for animated view


        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        //For clustering
        mClusterManager = new ClusterManager<Device>(getApplicationContext(), mGoogleMap);
        mGoogleMap.setOnCameraChangeListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mGoogleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager()); // To add custom adapter

        mClusterManager
                .setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Device>() {
                    @Override
                    public boolean onClusterClick(final Cluster<Device> cluster) {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                cluster.getPosition(), (float) Math.floor(mGoogleMap
                                        .getCameraPosition().zoom + 2)), 300,
                                null);

                        return true;
                    }
                });


        mGoogleMap.isBuildingsEnabled();
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.isIndoorEnabled();
        mGoogleMap.setIndoorEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);

        //To Lock camera zoom to 10.0f
//        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                float max = 10.0f;
//                if (cameraPosition.zoom > max) {
//                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(max));
//                }
//            }
//        });


        devicesArray = _jsonArray;
        int jsonLength = _jsonArray.length();
        for (int i = 0; i < jsonLength; i++) {
            Device objectMap = null;
            try {
                objectMap = mapper.readValue(devicesArray.get(i).toString(), Device.class);
                aqi = objectMap.getAqi();
                lat = objectMap.getLatitude();
                lon = objectMap.getLongitude();
                label = objectMap.getLabel();
                country = objectMap.getCountry();
                city = objectMap.getCity();
                time = objectMap.dateFormat();
                deviceId = objectMap.getDeviceId();

                //lat lng city aqi label
                items.add(new Device(lat, lon, city, aqi, label));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //For cluster
        mClusterManager.addItems(items);

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoWindowAdapter());
        mClusterManager.setRenderer(new MyClusterRenderer(getApplicationContext(), mGoogleMap, mClusterManager));


        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Device>() {
            @Override
            public void onClusterItemInfoWindowClick(Device device) {
                sendDeviceInfroFromLatLong(device.getPosition());


            }
        });
        mGoogleMap.setOnInfoWindowClickListener(mClusterManager);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mCurrentLocation != null) {

            currentLat = mCurrentLocation.getLatitude();
            currentLong = mCurrentLocation.getLongitude();
            Log.d(TAG, "Current location found" + currentLong + currentLat);
            CameraPosition position = CameraPosition.builder()
                    .target(new LatLng(currentLat, currentLong))
                    .zoom(10f)
                    .build();

//      Tilt is useful to give a 3D view of the map.

            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);

        } else {
            Toast.makeText(getApplicationContext(), R.string.errorGenral, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Current location not found :" + currentLong + currentLat);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Current location not found. Please turn on your location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Current location not found. Please turn on your location", Toast.LENGTH_SHORT).show();
    }

    //To create marker in cluster
    class MyClusterRenderer extends DefaultClusterRenderer<Device> {

        public MyClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<Device> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Device item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

            markerOptions.title(item.getLabel() + " , " + item.getAqi());
            markerOptions.snippet(String.valueOf(item.dateFormateSmall()));


            final int aqi = item.getAqi();
            if (0 <= aqi && aqi <= 50) {
                //good_map_marker
                markerOptions.icon(BitmapDescriptorFactory.
                        fromResource(R.drawable.good_map_marker_24));
            } else if (50 < aqi && aqi <= 100) {
                //satisfactory_map_marker
                markerOptions.icon(BitmapDescriptorFactory.
                        fromResource(R.drawable.satisfactory_map_marker_24));
            } else if (100 < aqi && aqi <= 200) {
                // moderate_map_marker
                markerOptions.icon(BitmapDescriptorFactory.
                        fromResource(R.drawable.moderate_map_marker_24));
            } else if (200 < aqi && aqi <= 300) {
                //poor_map_marker
                markerOptions.icon(BitmapDescriptorFactory.
                        fromResource(R.drawable.poor_map_marker_24));
            } else if (300 < aqi && aqi <= 400) {
                //very_poor_map_marker
                markerOptions.icon(BitmapDescriptorFactory.
                        fromResource(R.drawable.very_poor_map_marker_24));
            } else if (400 < aqi && aqi <= 500) {
                //severe_map_marker
                markerOptions.icon(BitmapDescriptorFactory.
                        fromResource(R.drawable.severe_map_marker_24));
            } else {
                //moderate_map_marker
                markerOptions.icon(BitmapDescriptorFactory.
                        fromResource(R.drawable.moderate_map_marker_24));
            }

        }

        @Override
        protected void onClusterItemRendered(final Device clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }

    }

    // Class for  custom info adapter
    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View view;

        @Override
        public View getInfoWindow(Marker marker) {

            if (MapActivity.this.mark != null && MapActivity.this.mark.isInfoWindowShown()) {
                MapActivity.this.mark.hideInfoWindow();
                MapActivity.this.mark.showInfoWindow();
            }
            return null;
        }

        //getInfoContents
        @Override
        public View getInfoContents(Marker marker) {

            view = getLayoutInflater().inflate(R.layout.custom_info_window,
                    null);

            LatLng latLng = marker.getPosition();
            TextView tvLable, tvUpdatetime;
            tvLable = (TextView) view.findViewById(R.id.txtLabel);
            tvUpdatetime = (TextView) view.findViewById(R.id.txtTime);

            //if else for color change
            final String tittle = marker.getTitle();
            tvLable.setText(tittle);

            final String snippet = marker.getSnippet();
            tvUpdatetime.setText("Last updated :" + snippet);
            //   Log.e(TAG, tittle + snippet + "adasd ");
            return view;
        }
    }


}
