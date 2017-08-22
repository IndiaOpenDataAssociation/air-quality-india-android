package com.idn0phl3108ed43d22s30.Ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.idn0phl3108ed43d22s30.AirOwlWi.WifiConnectAirOwl;
import com.idn0phl3108ed43d22s30.MainActivity;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.adapter.MyDevicesAdapter;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.Device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rutul on 14-06-2016.
 */
public class MyDevices extends Fragment implements View.OnClickListener {

    private static final String TAG = MyDevices.class.getSimpleName();
    View rootView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recycleLayoutManager;

    private SwipeRefreshLayout myDevicesSwipeRefreshLayout;
    private int edit_position;
    private FloatingActionButton mFloatingActionButtonDevice;
    private List<Device> devices;
    private JSONArray devicesArray;
    private Paint p = new Paint();
    private String deviceID = "";
    private SQLiteDatabaseHelper dbHelper;
    private Device device;

    private CoordinatorLayout mCoordinatorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mydevice, container, false);

        if (getActivity() instanceof MainActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.tittle_myDevices_public);
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.tittle_myDevices);
        }

        dbHelper = new SQLiteDatabaseHelper(getActivity().getApplicationContext());
        devices = new ArrayList<Device>();
        fetchLocalDevices();

        ConfigView();

        return rootView;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.removeAllViews();
        initiateAdapter(true);
        fetchLocalDevices();

    }

    @Override
    public void onResume() {
        recyclerView.removeAllViews();
        initiateAdapter(true);
        fetchLocalDevices();
        super.onResume();
    }

    private void fetchLocalDevices() {
        if (getActivity() instanceof MainActivity) {
            devicesArray = dbHelper.readPublicDataValue();

        } else {
            devicesArray = dbHelper.readPrivateDataValue();
        }

        if (devicesArray != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                int responseLength = devicesArray.length();
                devices.removeAll(devices);
                for (int i = 0; i < responseLength; i++) {
                    Device device = objectMapper.readValue(devicesArray.get(i).toString(), Device.class);
                    Log.i(TAG, "String detail :" + device.toString());
                    if (getActivity() instanceof MainActivity) {
                        if (device.getPrivateFlag() == 0) {
                            devices.add(device);
                        }
                    } else {
                        if (true) {
                            devices.add(device);
                        }
                    }
                }
                initiateAdapter(true);
            } catch (JsonParseException e) {
                fetchDevices(getActivity().getApplicationContext());
                Log.e(TAG, "JSON Parse exception occurred with details :" + e.toString());
            } catch (JSONException e) {
                fetchDevices(getActivity().getApplicationContext());
                Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
            } catch (JsonMappingException e) {
                fetchDevices(getActivity().getApplicationContext());
                Log.e(TAG, "JSON Mapping exception occurred with details Current  : " + e.toString());
            } catch (IOException e) {
                fetchDevices(getActivity().getApplicationContext());
                Log.e(TAG, "IO Exception occurred with details : " + e.toString());
            }

        } else {
            if (getActivity() instanceof MainActivity) {
                //nothing to do
            } else {
                fetchDevices(getActivity().getApplicationContext());
            }
            Log.d(TAG, "failed to fetch local devices this will fetch it from server");
        }
    }

    private void initiateAdapter(boolean flag) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_device);
        recyclerView.setHasFixedSize(true);

        recycleLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(recycleLayoutManager);


        // TODO: 17-08-2016 for swipe to delete function uncomment bellow

        if (getActivity() instanceof MainActivity) {
            devicesArray = dbHelper.readPublicDataValue();
            int jsonLength = devicesArray.length();
            Log.i(TAG, "JSONLength for MyDevices" + jsonLength);
            if (jsonLength >= 2) {
                initSwipeDelete();
            }
        } else {
            initSwipe();
        }


        if (flag) {
            if (recyclerViewAdapter != null) {
                recyclerView.removeAllViews();
                recycleLayoutManager.removeAllViews();
                recyclerViewAdapter.notifyItemRangeRemoved(0, recyclerViewAdapter.getItemCount());
            }

            if (getActivity() instanceof MainActivity) {
                recyclerViewAdapter = new MyDevicesAdapter(getActivity(), getActivity(), devices, 2);
            } else {
                recyclerViewAdapter = new MyDevicesAdapter(getActivity(), getActivity(), devices, 1);
            }
            recyclerView.setAdapter(recyclerViewAdapter);
        } else {
            //recyclerViewAdapter.notifyDataSetChanged();
        }
        checkNull();
    }

    private void initSwipeDelete() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                try {

                    devices.remove(position);
                    Gson gson = new Gson();
                    String devicesArray = gson.toJson(devices); // TO convert ListView into JSON data
                    JSONArray array = new JSONArray(devicesArray); // Converting Array into JSONArray
                    //Log.e(TAG, "Array" + array.toString());
                    dbHelper.updateRefreshedDataArray(array); // Updating SQLite data
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.notifyItemRemoved(position);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSONException  detail" + e.toString());
                }

            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;


                    p.setColor(Color.BLUE);
                    p.setColor(Color.parseColor("#72c16d"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);


                    super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    //TODO function for swipe to delete or edit... call bellow this recyclerView.setLayoutManager(recycleLayoutManager);

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {

                    try {
                        UserPrefrenceUtils.deviceDelete(devicesArray.getJSONObject(position), getActivity());
                        devices.remove(position);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerViewAdapter.notifyItemRemoved(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSONException  detail" + e.toString());
                    }
                } else {

                    try {
                        edit_position = position;
                        Intent manageDevice = new Intent(getActivity(), WifiConnectAirOwl.class);
                        manageDevice.putExtra(ApiKeys.MANAGE_DEVICE_ID, devicesArray.getJSONObject(position).getString(ApiKeys.DEVICES_DEVICEID));
                        manageDevice.putExtra(ApiKeys.MANAGE_DEVICE_TYPE, devicesArray.getJSONObject(position).getString(ApiKeys.DEVICES_TYPE));
                        manageDevice.putExtra(ApiKeys.MANAGE_DEVICE_NAME, devicesArray.getJSONObject(position).getString(ApiKeys.DEVICES_LABEL));
                        removeView();
                        startActivity(manageDevice);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                    }


                }
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;


                    if (dX > 0) {
                        p.setColor(Color.parseColor("#00b3bf"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.BLUE);
                        p.setColor(Color.parseColor("#00b3bf"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }

                    super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    private void removeView() {
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    private void removeAllRecyclerViewItems() {

    }

    private void checkNull() {
        if (recyclerView.getAdapter() != null) {
            int listSize = recyclerView.getAdapter().getItemCount();
            if (listSize == 0) {
                myDeviceFab();
            }
        } else {
            myDeviceFab();
        }

    }


    // private device list ..

    private void fetchDevices(final Context context) {

        if (devicesArray != null) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Updating device list....", "Please wait...", false, false);
            ECAPIHelper.getDevicesListArray(context, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response != null) {
                            devices.removeAll(devices);
                            ObjectMapper objectMapper = new ObjectMapper();
                            int responseLength = response.length();

                            for (int i = 0; i < responseLength; i++) {
                                Device device = objectMapper.readValue(response.get(i).toString(), Device.class);
                                if (!(devices.contains(device))) {
                                    devices.add(device);
                                }
                                loading.dismiss();

                            }
                            dbHelper.updatePrivateDataArray(response);
                            initiateAdapter(false);
                        } else {
                            loading.dismiss();
                            initiateAdapter(false);
                            goToGlobalActivity();
                            Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        loading.dismiss();
                        initiateAdapter(false);
                        goToGlobalActivity();
                        Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        initiateAdapter(false);
                        goToGlobalActivity();
                        Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        initiateAdapter(false);
                        goToGlobalActivity();
                        Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON Mapping 3 Exception occurred with details : " + e.toString());
                    } catch (IOException e) {
                        loading.dismiss();
                        initiateAdapter(false);
                        goToGlobalActivity();
                        Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    initiateAdapter(false);
                    loading.dismiss();
                    goToGlobalActivity();
                    Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Volley Error occurred with details : " + error.toString());
                }
            });
        } else {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Updating device list....", "Please wait...", false, false);
            ECAPIHelper.getDevicesListArray(context, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    try {
                        if (response != null) {
                            devices.removeAll(devices);
                            ObjectMapper objectMapper = new ObjectMapper();
                            int responseLength = response.length();
                            for (int i = 0; i < responseLength; i++) {
                                Device device = objectMapper.readValue(response.get(i).toString(), Device.class);
                                devices.add(device);

                            }
                            initiateAdapter(true);
                        } else {
                            initiateAdapter(false);
                            goToGlobalActivity();
                            Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        initiateAdapter(false);
                        goToGlobalActivity();
                        Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                    } catch (JsonParseException e) {
                        initiateAdapter(false);
                        goToGlobalActivity();
                        Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                    } catch (JsonMappingException e) {
                        initiateAdapter(false);
                        goToGlobalActivity();
                        Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON Mapping 54 Exception occurred with details : " + e.toString());
                    } catch (IOException e) {
                        initiateAdapter(false);
                        goToGlobalActivity();
                        Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                    }
                    loading.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "No devices registered", Toast.LENGTH_SHORT).show();
                    initiateAdapter(false);
                    goToGlobalActivity();
                    Log.e(TAG, "Volley Error occurred with details : " + error.toString());
                    loading.dismiss();
                }
            });
        }

    }

    private void ConfigView() {
        mFloatingActionButtonDevice = (FloatingActionButton) rootView.findViewById(R.id.fab_myDevice);
        if (getActivity() instanceof MainActivity) {
            mFloatingActionButtonDevice.setBackgroundTintList(getActivity().getApplicationContext().getResources().getColorStateList(R.color.green));
        } else {
            mFloatingActionButtonDevice.setBackgroundTintList(getActivity().getApplicationContext().getResources().getColorStateList(R.color.blue));
        }
        mFloatingActionButtonDevice.setOnClickListener(this);
        myDevicesSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.myDevicesSwipeRefreshLayout);
        myDevicesSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getActivity() instanceof MainActivity) {
                    fetchPublicDevices(getActivity().getApplicationContext());
                } else if (getActivity() instanceof PrivateGlobalActivity) {
                    devicesArray = null;
                    fetchDevices(getActivity().getApplicationContext());
                }
                myDevicesSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.cordiLayout);
    }

    //For public device

    private void fetchPublicDevices(final Context context) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Updating list..", "Please wait...", false, false);
        ECAPIHelper.fetchDeviceId(context, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        loading.dismiss();
                        dbHelper.updatePublicDataArray(response);
                        fetchLocalDevices();
                        initiateAdapter(true);
                    } else {
                        loading.dismiss();
                        Log.e(TAG, "null devices received on refresh");
                    }

                } catch (Exception e) {
                    loading.dismiss();
                    Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.e(TAG, "volley error occurred with details : " + error.toString());
            }
        });
    }


    private void myDeviceFab() {
        Intent addDevice;
        if (getActivity() instanceof MainActivity) {
            addDevice = new Intent(getActivity(), AddCity.class);
            startActivity(addDevice);
        } else if (getActivity() instanceof PrivateGlobalActivity) {
            addDevice = new Intent(getActivity(), AddDevice.class);
            startActivity(addDevice);
        }

    }

    private void goToGlobalActivity() {
        UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, null, getActivity().getApplicationContext());

        Intent addDevice = new Intent(getActivity(), MainActivity.class);
        startActivity(addDevice);
    }

    @Override
    public void onClick(View v) {
        if (v == mFloatingActionButtonDevice) {
            myDeviceFab();
        }
    }


    private JSONObject fetchLocalDeviceIds() {
        JSONObject jsonObject = new JSONObject();
        try {
            int sizeOfDevices = devices.size();
            String[] deviceIdsArray = new String[sizeOfDevices];
            for (int i = 0; i < sizeOfDevices; i++) {
                deviceIdsArray[i] = devices.get(i).getDeviceId();
            }
            jsonObject.put("devices", deviceIdsArray);
        } catch (JSONException e) {
            jsonObject = null;
            Log.e(TAG, "JSON Exception occurred with details fetch : " + e.toString());
        }
        return jsonObject;
    }

    // TODO: 27-08-2016 Please check the code
    private void sendUserID() {
        //device = new Device(deviceID);
        final JSONObject jsonObject = fetchLocalDeviceIds();
        if (jsonObject != null) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Updating data ...", "Please wait...", false, false);

            ECAPIHelper.deviceData(getActivity().getApplicationContext(), jsonObject, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    if (response != null) {
                        try {
                            Log.i(TAG, response.toString());
                            // TODO: 30-08-2016 Main function call
                            if (response.length() > 0) {
                                loading.dismiss();
                                fetchLocalDevices();
                                initiateAdapter(true);
                            } else {
                                loading.dismiss();
                                Log.e(TAG, "null devices received on refresh");
                            }
                        } catch (Exception e) {
                            loading.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(mCoordinatorLayout, "Something went wromg ", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            Log.e(TAG, "JSON Excpetion occurred with details : " + e.getMessage());
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(mCoordinatorLayout, "Something went wromg ", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar snackbar = Snackbar
                            .make(mCoordinatorLayout, R.string.failedToRegister, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    loading.dismiss();
                    Log.e(TAG, "Volley Error occurred with details : " + error.toString());
                }
            });
        }

    }

}
