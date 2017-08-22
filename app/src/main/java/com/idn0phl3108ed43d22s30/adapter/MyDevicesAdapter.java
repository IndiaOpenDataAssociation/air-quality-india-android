package com.idn0phl3108ed43d22s30.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idn0phl3108ed43d22s30.MainActivity;
import com.idn0phl3108ed43d22s30.PrivateGlobalActivity;
import com.idn0phl3108ed43d22s30.ProfileManageDevice;
import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.Ui.ManageDevice;
import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;
import com.idn0phl3108ed43d22s30.pojo.Device;
import com.idn0phl3108ed43d22s30.pojo.Payload;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by jimish on 28/6/16.
 */

public class MyDevicesAdapter extends RecyclerView.Adapter<MyDevicesAdapter.OfferHolder> {
    private static final String TAG = "MyDeviceAdapter";

    private Context context;
    private List<Device> deviceList;
    private int flag;
    private static final int REQUEST_CALL = 222;
    private Activity activityContext;
    private String _StringAqi;
    private SQLiteDatabaseHelper dbHelper;


    public MyDevicesAdapter(Activity activityContext, Context context, List<Device> devices, int flag) {
        this.context = context;
        dbHelper = new SQLiteDatabaseHelper(context);
        this.activityContext = activityContext;
        this.deviceList = devices;
        this.flag = flag;
    }

    @Override
    public OfferHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (flag == 1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_mydevice_row, parent, false);
        } else if (flag == 2) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_mydevice_row_public, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_mydevice_row, parent, false);
        }

        OfferHolder vh = new OfferHolder(v, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(final OfferHolder holder, final int position) {
        String label = deviceList.get(position).getLabel();
        if (flag == 1) {
            boolean online = getDiff(String.valueOf(deviceList.get(position).getTime()));
            if (online == true) {
                holder.myDevicesStatusOffline.setVisibility(GONE);
                holder.myDevicesStatusOnline.setVisibility(View.VISIBLE);
            } else {
                holder.myDevicesStatusOnline.setVisibility(GONE);
                holder.myDevicesStatusOffline.setVisibility(View.VISIBLE);
            }
        } else {

            String lastUpdatedTime = "updated at: " + deviceList.get(position).dateFormat();
            holder.myDevicesStatusOffline.setVisibility(GONE);
            holder.myDevicesStatusOnline.setVisibility(View.VISIBLE);
            holder.myDevicesStatusOnline.setText(lastUpdatedTime);
        }

        int aqi = deviceList.get(position).getAqi();
        float aqiP = 100 * aqi / 500; // aqiP is aqu in Percentage
        //    Log.i(TAG, "Original value :" + aqi + " % value:" + String.valueOf(aqiP));
        _StringAqi = String.valueOf(aqi);
        ProgressBarView(holder, aqi, aqiP);
        holder.myDevicesDonutView.setProgress(aqiP);
        //To set custom typeface
        CustomTypeface(holder);

        String deviceType = deviceList.get(position).getType();
        if (deviceType.equals(ApiKeys.AIROWL_WI_TYPE)) {
            holder.circleImageView.setImageResource(R.drawable.airowl_circle);
        } else if (deviceType.equals(ApiKeys.AIROWL_3G_TYPE)) {
            holder.circleImageView.setImageResource(R.drawable.airowl_circle);
        } else if (deviceType.equals(ApiKeys.BREATH_I_TYPE)) {
            holder.circleImageView.setImageResource(R.drawable.breahi_circle);
        } else if (deviceType.equals(ApiKeys.POLLUDRON_TYPE)) {
            holder.circleImageView.setImageResource(R.drawable.polludrone_circle);
        }

        holder.my_device_relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ProfileManageDevice) {
                    startManageDevice(deviceList.get(position));
                } else {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JSONObject deviceJson = new JSONObject(mapper.writeValueAsString(deviceList.get(position)));
                        if (context instanceof MainActivity) {
                            goToHome(deviceJson, null);
                        } else {
                            Payload payload = deviceList.get(position).getPayload();
                            String payloadStr = mapper.writeValueAsString(payload);
                            goToHome(deviceJson, payloadStr);

                        }
                    } catch (JsonProcessingException e) {
                        Log.e(TAG, "JSON Processing exception occurred with details : " + e.toString());
                        Toast.makeText(context, "Please swipe down to refresh data!", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                        Toast.makeText(context, "Please swipe down to refresh data!", Toast.LENGTH_LONG).show();
                    }


                }

            }
        });
        holder.myDevicesLabel.setText(label);
    }

    private void CustomTypeface(OfferHolder holder) {
        Typeface font = Typeface.createFromAsset(
                context.getAssets(),
                "fonts/BebasNeue.ttf");
        holder.myDevicesDonutText.setTypeface(font);
        holder.myDevicesDonutText.setText(_StringAqi);//To set text in donut view
    }

    private void ProgressBarView(OfferHolder holder, int aqi, float aqiP) {
        //To change color
        if (0 <= aqi && aqi <= 50) {
            //good_map_marker
            holder.myDevicesDonutView.setColor(ContextCompat.getColor(context, R.color.good));
            int animationDuration = 1500; // 1500ms = 1,5s
            holder.myDevicesDonutView.setProgressWithAnimation(aqiP, animationDuration);
        } else if (50 < aqi && aqi <= 100) {
            //satisfactory_map_marker
            holder.myDevicesDonutView.setColor(ContextCompat.getColor(context, R.color.satisfactory));
            int animationDuration = 1500; // 1500ms = 1,5s
            holder.myDevicesDonutView.setProgressWithAnimation(aqiP, animationDuration);
        } else if (100 < aqi && aqi <= 200) {
            // moderate_map_marker
            holder.myDevicesDonutView.setColor(ContextCompat.getColor(context, R.color.moderate));
            int animationDuration = 1500; // 1500ms = 1,5s
            holder.myDevicesDonutView.setProgressWithAnimation(aqiP, animationDuration);
        } else if (200 < aqi && aqi <= 300) {
            //poor_map_marker
            holder.myDevicesDonutView.setColor(ContextCompat.getColor(context, R.color.poor));
            int animationDuration = 1500; // 1500ms = 1,5s
            holder.myDevicesDonutView.setProgressWithAnimation(aqiP, animationDuration);
        } else if (300 < aqi && aqi <= 400) {
            //very_poor_map_marker
            holder.myDevicesDonutView.setColor(ContextCompat.getColor(context, R.color.verypoor));
            int animationDuration = 1500; // 1500ms = 1,5s
            holder.myDevicesDonutView.setProgressWithAnimation(aqiP, animationDuration);
        } else if (400 < aqi && aqi <= 500) {
            //severe_map_marker
            holder.myDevicesDonutView.setColor(ContextCompat.getColor(context, R.color.severe));
            int animationDuration = 1500; // 1500ms = 1,5s
            holder.myDevicesDonutView.setProgressWithAnimation(aqiP, animationDuration);
        } else {
            //moderate_map_marker
            holder.myDevicesDonutView.setColor(ContextCompat.getColor(context, R.color.severe));
            int animationDuration = 1500; // 1500ms = 1,5s
            holder.myDevicesDonutView.setProgressWithAnimation(aqiP, animationDuration);
        }
    }

    private void startManageDevice(Device device) {
        Intent intent = new Intent(context, ManageDevice.class);
        intent.putExtra(ApiKeys.MANAGE_DEVICE_ID, device.getDeviceId());
        intent.putExtra(ApiKeys.MANAGE_DEVICE_NAME, device.getLabel());
        (activityContext).startActivity(intent);
    }

    private boolean getDiff(String epochStr) {
        if (epochStr != null) {
            long epochTime = Long.valueOf(epochStr);
            Date date = new Date();
            long currentEpoch = date.getTime();
            currentEpoch = currentEpoch / 1000;
            long diffEpochTime = currentEpoch - epochTime;
            if (diffEpochTime < 3601) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class OfferHolder extends RecyclerView.ViewHolder {

        protected TextView myDevicesLabel;
        protected TextView myDevicesStatusOnline;
        protected TextView myDevicesStatusOffline, myDevicesDonutText;
        protected LinearLayout my_device_relativelayout;
        protected CircularProgressBar myDevicesDonutView;
        protected Context context;
        protected ImageView circleImageView;

        public OfferHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            circleImageView = (ImageView) itemView.findViewById(R.id.circleImg);
            my_device_relativelayout = (LinearLayout) itemView.findViewById(R.id.len1);
            myDevicesLabel = (TextView) itemView.findViewById(R.id.myDevicesTitle);
            myDevicesStatusOnline = (TextView) itemView.findViewById(R.id.myDevicesStatusOnline);
            myDevicesStatusOffline = (TextView) itemView.findViewById(R.id.myDevicesStatusOffline);
            myDevicesDonutView = (CircularProgressBar) itemView.findViewById(R.id.donut_progress_myDevice);
            myDevicesDonutView.setProgressBarWidth(itemView.getResources().getDimension(R.dimen.progressBarWidth)); // donut_finished_color
            myDevicesDonutView.setBackgroundProgressBarWidth(itemView.getResources().getDimension(R.dimen.backgroundProgressBarWidth));// donut_unfinished_stroke
            myDevicesDonutView.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundProgressBarColor)); // donut_unfinished_color
            myDevicesDonutText = (TextView) itemView.findViewById(R.id.textProgress);
            //range

        }
    }

    private void goToHome(JSONObject deviceJson, String payloadStr) {
        if (context instanceof MainActivity) {
            dbHelper.updateLatestPublicObj(deviceJson);
            UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, null, context);
            ((MainActivity) context).switchViewPager(0);
        } else {
            dbHelper.updateLatestPrivateObj(deviceJson);
            UserPrefrenceUtils.saveValueToUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, payloadStr, context);
            ((PrivateGlobalActivity) context).swichViewPager(0);
        }

    }

}
