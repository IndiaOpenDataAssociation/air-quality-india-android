package com.idn0phl3108ed43d22s30.Ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.idn0phl3108ed43d22s30.AirOwlWi.WifiConnectAirOwl;
import com.idn0phl3108ed43d22s30.MainActivity;
import com.idn0phl3108ed43d22s30.MapActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.ConnectionDetector;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.holder.ArrowExpandSelectableHeaderHolder;
import com.idn0phl3108ed43d22s30.holder.IconTreeItemHolder;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.Area;
import com.idn0phl3108ed43d22s30.pojo.City;
import com.idn0phl3108ed43d22s30.pojo.Place;
import com.idn0phl3108ed43d22s30.pojo.PublicDevices;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rutul on 14-06-2016.
 */
public class AddCity extends AppCompatActivity {
    private static final String TAG = AddCity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Toolbar toolbar;
    private ListView listView;
    private RelativeLayout treeNodeLayout, mReaRelativeLayout;

    private Boolean isInternetPresent = false;
    private ConnectionDetector connectionDetector;

    private List<City> cityList;
    private List<String> cityNameList;
    private List<String> countryNameList;
    private List<Area> areaList;
    private List<String> countryList;

    private AndroidTreeView tView;
    private TreeNode treeNode;
    private List<Place> places;
    private JSONObject selectedDevice;
    private PublicDevices selectedDeviceObj;
    private JSONArray _tempResponse, _jsonArray;
    String _temp;

    private static SQLiteDatabaseHelper dbHelper;

    private List<PublicDevices> devices;
    private boolean firstTimeCall = false;

    public LinearLayout noInternetConnection;
    public TextView txtRetry;

    LocationManager locationManager;
    boolean gpsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcity);
        initConfigView();

        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());

        cityList = new ArrayList<City>();
        cityNameList = new ArrayList<String>();
        countryNameList = new ArrayList<String>();
        areaList = new ArrayList<Area>();
        countryList = new ArrayList<String>();
        devices = new ArrayList<PublicDevices>();
        places = new ArrayList<Place>();

        if (!checkPermission()) {
            requestPermission();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            _temp = (String) extras.get(Constants.CITY_LIST_RESPONSE_KEY_MAP);
            firstTimeCall = true;
        } else {
            Log.e(TAG, "Something wnt wrong");
        }

        checkInternetConnectivity();
    }


    private void startPublicGlobalActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, null, getApplicationContext());
        startActivity(intent);
    }

    private void checkInternetConnectivity() {
        isInternetPresent = connectionDetector.isConnectingToInternet();
        noInternetConnection.setVisibility(View.INVISIBLE);
        if (isInternetPresent) {
//            fetchCityList();
            switching();
        } else {
            noInternetConnection.setVisibility(View.VISIBLE);
            Snackbar.make(mReaRelativeLayout, R.string.errorInternet, Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fetchCityList();
                        }
                    })
                    .setDuration(10000) // to set duration of Snackbar
                    .show();
        }
    }

    private void initConfigView() {
        listView = (ListView) findViewById(R.id.listView_addCity);
        noInternetConnection = (LinearLayout) findViewById(R.id.LenNoInternet);
        txtRetry = (TextView) findViewById(R.id.txtTaptoRetry);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        treeNodeLayout = (RelativeLayout) findViewById(R.id.treeNodeLayout);
        toolbar = (Toolbar) findViewById(R.id.toolBarImage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add City");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mReaRelativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayoutAddCity);
        txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternetConnectivity();
                noInternetConnection.setVisibility(View.INVISIBLE);
            }
        });

    }

    //For calling response one time
    private void switching() {

        if (firstTimeCall) {
            fetchCityListlocal();
            firstTimeCall = false;
            Log.e(TAG, "Second time call");
        } else {
            fetchCityList();
            Log.e(TAG, "First time call");
        }
    }

    //Response call
    private void fetchCityListlocal() {
        try {
            _jsonArray = new JSONArray(_temp);
            Log.i(TAG, "Local data called");
            final ProgressDialog loading = ProgressDialog.show(this, "Fetching data ....", "Please wait...", false, false);
            if (_jsonArray != null) {
                _tempResponse = _jsonArray;
                ObjectMapper objectMapper = new ObjectMapper();
                Log.i(TAG, "response is not null, size of array is :" + _jsonArray.length());
                try {
                    JSONArray devicesArray = _jsonArray;
                    int jsonLength = devicesArray.length();
                    for (int i = 0; i < jsonLength; i++) {
                        PublicDevices device = objectMapper.readValue(devicesArray.get(i).toString(), PublicDevices.class);

                        Area area = new Area(device.getLabel(), device);
                        areaList.add(area);
                        //device.setLabel(device.getLabel()+", "+device.getCity());
                        devices.add(device);

                        if (!(cityNameList.contains(device.getCity()))) {
                            if (device.getCity() != null) {
                                cityNameList.add(device.getCity().trim());
                            }

                        }

                        if (!(countryNameList.contains(device.getCountry()))) {
                            if (device.getCountry() != null) {
                                countryNameList.add(device.getCountry().trim());
                            }

                        }
                    }

                    Collections.sort(cityNameList);
                    Collections.sort(countryNameList);

                    loading.dismiss();
                    storeDummyData();
                    fetchDiffLists();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "Response second" + _jsonArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Main call
    private void fetchCityList() {
        final ProgressDialog loading = ProgressDialog.show(this, "Fetching data ....", "Please wait...", false, false);
        ECAPIHelper.fetchDeviceId(getApplicationContext(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    _tempResponse = response;
                    ObjectMapper objectMapper = new ObjectMapper();
                    Log.i(TAG, "response is not null, size of array is :" + response.length());
                    try {
                        JSONArray devicesArray = response;
                        int jsonLength = devicesArray.length();
                        for (int i = 0; i < jsonLength; i++) {
                            PublicDevices device = objectMapper.readValue(devicesArray.get(i).toString(), PublicDevices.class);

                            Area area = new Area(device.getLabel(), device);
                            areaList.add(area);
                            //device.setLabel(device.getLabel()+", "+device.getCity());
                            devices.add(device);

                            if (!(cityNameList.contains(device.getCity()))) {
                                if (device.getCity() != null) {
                                    cityNameList.add(device.getCity().trim());
                                }

                            }

                            if (!(countryNameList.contains(device.getCountry()))) {
                                if (device.getCountry() != null) {
                                    countryNameList.add(device.getCountry().trim());
                                }

                            }
                        }

                        Collections.sort(cityNameList);
                        Collections.sort(countryNameList);

                        loading.dismiss();
                        storeDummyData();
                        fetchDiffLists();
                    } catch (JSONException e) {
                        loading.dismiss();
                        Log.e(TAG, "JSON Exception occurred with details :" + e.toString());
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        Log.e(TAG, "JSON Parse Exception occurred with details :" + e.toString());
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        Log.e(TAG, "JSON Mapping Exception occurred with details :" + e.toString());
                    } catch (IOException e) {
                        loading.dismiss();
                        Log.e(TAG, "IO Exception occurred with details :" + e.toString());
                    }
                } else {
                    loading.dismiss();
                    Log.e(TAG, "response received in devices api is null.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Snackbar.make(mReaRelativeLayout, R.string.errorGenral, Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchCityList();
                            }
                        })
                        .show();
                Log.e(TAG, "Volley error occurred with details : " + error.toString());
            }
        });
    }

    private void storeDummyData() {
    }

    public String trimStr(String str) {
        str = str.trim();
        return str;
    }


    public void fetchDiffLists() {
        String leftCity, rightCity, leftCountry, rightCountry;
        if (devices != null && devices.size() != 0) {
            for (int j = 0; j < cityNameList.size(); j++) {
                City city = new City(cityNameList.get(j));
                cityList.add(city);
            }
            for (int i = 0; i < countryNameList.size(); i++) {
                Place place = new Place(countryNameList.get(i));
                places.add(place);
            }

            for (int i = 0; i < cityList.size(); i++) {
                for (int j = 0; j < devices.size(); j++) {

                    String _temp = areaList.get(j).getDevice().getCity();
                    if (_temp != null && !(_temp.isEmpty())) {
                        _temp = _temp.trim();
                        leftCity = _temp;

                        rightCity = cityList.get(i).getCity();
                        if (leftCity.equals(rightCity)) {
                            cityList.get(i).getAreas().add(areaList.get(j));
                        }
                    } else {
                        // TODO: 05-08-2016
                    }


                }
            }
            for (int i = 0; i < places.size(); i++) {
                for (int j = 0; j < cityList.size(); j++) {
                    String _temp = cityList.get(j).getAreas().get(0).getDevice().getCountry();

                    //Checking null value in array if any null value find then stop it..

                    if (_temp != null && !(_temp.isEmpty())) {
                        _temp = _temp.trim();
                        leftCountry = _temp;
                        rightCountry = places.get(i).getCountry();
                        if (leftCountry.equals(rightCountry)) {
                            places.get(i).getCities().add(cityList.get(j));
                        }
                    } else {
                        // TODO: 05-08-2016
                    }
                }

            }
            Log.i(TAG, "fetched list of city, country and area");
            //adapter = new AddCityAdapter(this, places);
            //listView.setAdapter(adapter);
            setTreeNodes();
        }
    }

    private void setTreeNodes() {
        TreeNode root = TreeNode.root();

        for (int i = 0; i < places.size(); i++) {
            treeNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.blank, places.get(i).getCountry() + Constants.SPACES)).setViewHolder(
                    new ArrowExpandSelectableHeaderHolder(this));

            Place place = places.get(i);
            for (int j = 0; j < place.getCities().size(); j++) {
                City city = place.getCities().get(j);
                TreeNode cityNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.blank, city.getCity() + Constants.SPACES));
                treeNode.addChild(cityNode);
                for (int k = 0; k < city.getAreas().size(); k++) {
                    Area area = city.getAreas().get(k);
                    TreeNode areaNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(getDrawableForArea(String.valueOf(area.getDevice().getAqi())), area.getArea(), getDrawableForArea(String.valueOf(area.getDevice().getAqi()))));
                    cityNode.addChild(areaNode);
                }
            }
            root.addChild(treeNode);

        }
        setTreeView(root);
        tView.expandNode(treeNode);
    }

    private int getDrawableForArea(String aqi) {
        int retDrawable;
        if (aqi != null) {
            int aqiIntVal = Integer.valueOf(aqi);

            if (aqiIntVal >= Constants.AQI_GOOD_LOW && aqiIntVal <= Constants.AQI_GOOD_HIGH) {
                retDrawable = R.drawable.ic_good_dot;
            } else if (aqiIntVal >= Constants.AQI_SATISFACTORY_LOW && aqiIntVal <= Constants.AQI_SATISFACTORY_HIGH) {
                retDrawable = R.drawable.ic_good_dot;
            } else if (aqiIntVal >= Constants.AQI_MODERATE_LOW && aqiIntVal <= Constants.AQI_MODERATE_HIGH) {
                retDrawable = R.drawable.ic_moderate_dot;
            } else if (aqiIntVal >= Constants.AQI_POOR_LOW && aqiIntVal <= Constants.AQI_POOR_HIGH) {
                retDrawable = R.drawable.ic_poor_dot;
            } else if (aqiIntVal >= Constants.AQI_VERY_POOR_LOW && aqiIntVal <= Constants.AQI_VERY_POOR_HIGH) {
                retDrawable = R.drawable.ic_verypoor_dot;
            } else if (aqiIntVal >= Constants.AQI_SEVERE_LOW && aqiIntVal <= Constants.AQI_SEVERE_HIGH) {
                retDrawable = R.drawable.ic_severe_dot;
            } else {
                retDrawable = R.drawable.ic_good_dot;
            }
        } else {
            retDrawable = R.drawable.ic_good;
        }
        return retDrawable;
    }

    private void setTreeView(TreeNode root) {
        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setUse2dScroll(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);

        tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                Toast toast = Toast.makeText(getApplicationContext(), ((IconTreeItemHolder.IconTreeItem) value).text, Toast.LENGTH_SHORT);
                String deviceName = ((IconTreeItemHolder.IconTreeItem) value).text;

                registerPublicDeviceLocally(deviceName, node);
            }
        });
        tView.setDefaultViewHolder(ArrowExpandSelectableHeaderHolder.class);
        treeNodeLayout.addView(tView.getView());
        tView.setUseAutoToggle(false);
    }

    private void registerPublicDeviceLocally(String deviceName, TreeNode node) {
        //for(int i=0; i<devices.size(); i++){
        if (node.getParent() != null) {
            if (node.getParent().getParent() != null) {
                String _tmp = ((IconTreeItemHolder.IconTreeItem) node.getParent().getValue()).text;
                //_tmp = _tmp.trim();
                _tmp = trimTrailing(_tmp);
                for (int p = 0; p < cityList.size(); p++) {
                    if (_tmp.equals(trimTrailing(cityList.get(p).getCity() + Constants.SPACES))) {
                        for (int n = 0; n < cityList.get(p).getAreas().size(); n++) {
                            if (deviceName.equals(cityList.get(p).getAreas().get(n).getDevice().getLabel())) {
                                try {

                                    ObjectMapper mapper = new ObjectMapper();
                                    if (node.getParent() != null) {
                                        if (node.getParent().getParent() != null) {

                                        }
                                    }
                                    selectedDevice = new JSONObject(mapper.writeValueAsString(cityList.get(p).getAreas().get(n).getDevice()));

                                    boolean isPublicRegistered = dbHelper.isPublicDeviceRegistered(selectedDevice);
                                    if (!isPublicRegistered) {
                                        dbHelper.updatePublicData(selectedDevice);
                                    }
                                    dbHelper.updateLatestPublicObj(selectedDevice);

                                    startPublicGlobalActivity();
                                    break;
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                                } catch (JsonProcessingException e) {
                                    Log.e(TAG, "JSON Processing Exception occurred with details : " + e.toString());
                                }

                            }
                        }
                    }
                }
            }
        }

        for (int j = 0; j < places.size(); j++) {
            deviceName = trimTrailing(deviceName);
            if (deviceName.equals(trimTrailing(places.get(j).getCountry()))) {
                if (node.isExpanded()) {
                    tView.collapseNode(node);
                } else {
                    tView.expandNode(node);
                }

            }
        }
        for (int k = 0; k < cityList.size(); k++) {
            deviceName = trimTrailing(deviceName);
            if (deviceName.equals(trimTrailing(cityList.get(k).getCity()))) {
                if (node.isExpanded()) {
                    tView.collapseNode(node);
                } else {
                    tView.expandNode(node);
                }
            }
        }
    }

    public static String trimTrailing(String str) {
        if (str != null) {
            for (int i = str.length() - 1; i >= 0; --i) {
                if (str.charAt(i) != ' ') {
                    return str.substring(0, i + 1);
                }
            }
        }
        return str;
    }

    // to check location permission
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    // to request permission
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getApplicationContext(), "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mReaRelativeLayout, "Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mReaRelativeLayout, "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    // TODO: 11-10-2016 Adding menu for switching to list view to map and map to list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isInternetPresent) {
            Log.e(TAG, "NO internet");
        } else {

            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.map_view, menu);
            return super.onCreateOptionsMenu(menu);
        }
        return true;
    }

    //To swap between Global and Private mode
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (!isInternetPresent) {
            item.setVisible(false);
        } else {
            switch (id) {

                case R.id.action_map:
                    startMapActivity();
                    return true;

                default:
                    return super.onOptionsItemSelected(item);

            }
        }
        return true;


    }

    //To check status of GPS
    public void checkGPSStatus() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private void startMapActivity() {
        checkGPSStatus();
        if (gpsStatus == true) {
            Intent intent = new Intent(this, MapActivity.class);
            if (_tempResponse.toString() == null || _tempResponse.toString().isEmpty()) {
                Snackbar.make(mReaRelativeLayout, R.string.errorInternet, Snackbar.LENGTH_LONG)
                        .show();
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constants.CITY_LIST_RESPONSE_KEY, _tempResponse.toString());
            }
            startActivity(intent);

        } else {
            Toast.makeText(getApplicationContext(),"Transferring to location service page",Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    Intent a = new Intent(action);
                    startActivity(a);
                }
            }, 2000);
        }
    }


}
