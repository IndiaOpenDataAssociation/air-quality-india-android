package com.idn0phl3108ed43d22s30;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.adapter.MyDevicesAdapter;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.Device;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileManageDevice extends AppCompatActivity {
    private static final String TAG = "ProfileManageDevices";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recycleLayoutManager;
    private int privateFlag;

    private static SQLiteDatabaseHelper dbHelper;

    private static final int REQUEST_CALL = 222;

    private JSONArray devicesArray;
    private List<Device> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_manage_device);

        dbHelper = new SQLiteDatabaseHelper(getApplicationContext());

        devices = new ArrayList<Device>();

        fetchLocalDevices();

        configView();
        setToolbar();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CALL){
            if(resultCode == RESULT_OK){
                fetchLocalDevices();
            }
        }
    }

    private void fetchLocalDevices(){

        devicesArray = dbHelper.readPrivateDataValue();

        if(devicesArray!=null){
            try{
                ObjectMapper objectMapper = new ObjectMapper();
                int responseLength = devicesArray.length();
                for (int i = 0; i < responseLength; i++) {
                    Device device = objectMapper.readValue(devicesArray.get(i).toString(), Device.class);
                    devices.add(device);
                }
                intiateAdapter(true);
            } catch (JsonParseException e) {
                Log.e(TAG, "JSON Parse exception occurred with details :" +e.toString());
            } catch (JSONException e) {
                Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
            } catch (JsonMappingException e) {
                Log.e(TAG, "JSON Mapping exception occurred with details : "+e.toString());
            } catch (IOException e) {
                Log.e(TAG, "IO Exception occurred with details : "+e.toString());
            }

        } else {
            Log.d(TAG, "failed to fetch local devices this will fetch it from server");
        }
    }

    private void intiateAdapter(boolean flag){
        recyclerView = (RecyclerView) findViewById(R.id.relDeviceManage);
        recyclerView.setHasFixedSize(true);
        recycleLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recycleLayoutManager);


        if(flag){
            recyclerViewAdapter = new MyDevicesAdapter(this, this, devices, 1);
            recyclerView.setAdapter(recyclerViewAdapter);
        } else {
            //recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void removeCurrentDevicesAndRestartAdapter(){
        recycleLayoutManager.removeAllViews();
        intiateAdapter(true);
    }


    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.txtManageDevice);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void configView() {

    }
}
