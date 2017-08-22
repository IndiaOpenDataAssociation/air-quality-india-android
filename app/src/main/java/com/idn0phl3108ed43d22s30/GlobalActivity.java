package com.idn0phl3108ed43d22s30;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.etc.Activities;
import com.idn0phl3108ed43d22s30.helpers.CommonHelpers;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.HourlyData;
import com.idn0phl3108ed43d22s30.pojo.PayloadData;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;

public class GlobalActivity extends Fragment implements View.OnClickListener {

    public static final String TAG = GlobalActivity.class.getSimpleName();

    private View rootView;
    private String aqi;
    private String status;
    private String deviceLabel, deviceId, city;
    private String lastUpdatedTime;

    private ImageView aqiGauge, imgActivity2, imgActivity3, imgActivity4, imgActivity1;

    private TextView aqiReading, aqiStatus, txtActivity, activityLabel;

    private String s_activity1, s_activity2, s_activity3, s_activity4;
    private int ic_activity1, ic_activity2, ic_activity3, ic_activity4;
    private int ic_activity1_fill, ic_activity2_fill, ic_activity3_fill, ic_activity4_fill;

    private TextView txtTime, txtDate;
    private int temp, hum;

    private int g1, g2, g3, g4, g5, g6, g7, g8, g9, o3, co, no2, so2, pm25, pm10, p1, p2, p3;
    private TextView textProgresspm10, textProgressco2, textProgressg2co, textProgressnh3, textProgressg5, textProgressh2s, textProgressg7,
            textProgressg8, textProgressg9, textProgresso3, textProgressco, textProgressno2, textProgresssso2, textProgresspm25, textProgressp10, textProgressp2,
            textProgressp3;

    private HorizontalScrollView gasesScrollBar;
    private List<LinearLayout> donutLayouts;
    private List<CircularProgressBar> donutViews;

    private List<HourlyData> payloadDataList;

    private TextView txtTemp, txtHumidity, shareIcon, shareRefresh;

    private SQLiteDatabaseHelper dbHelper;

    private PayloadData payloadData;
    private int min = 5;

    @Override
    public void onResume() {
        super.onResume();

    }

    private void fetchLatestDeviceData() {
        JSONObject deviceJSON;
        if (getActivity() instanceof MainActivity) {
            deviceJSON = dbHelper.readLatestPublicObj();
        } else {
            deviceJSON = dbHelper.readLatestPrivateObj();
        }

        try {
            aqi = String.valueOf(deviceJSON.getInt("aqi"));
            deviceLabel = deviceJSON.getString("label");
            city = deviceJSON.getString("city");
            deviceId = deviceJSON.getString("deviceId");
            status = CommonHelpers.getStatus(deviceJSON.getInt("aqi"));
            lastUpdatedTime = String.valueOf(deviceJSON.getLong("t"));
            ObjectMapper mapper = new ObjectMapper();
            JSONObject dJson = deviceJSON.getJSONObject("payload").getJSONObject("d");
            payloadData = mapper.readValue(dJson.toString(), PayloadData.class);
            temp = deviceJSON.getInt("temp");
            hum = deviceJSON.getInt("hum");
        } catch (JSONException e) {
            temp = 0;
            hum = 0;
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        } catch (JsonMappingException e) {
            Log.e(TAG, "JSON Mapping exception occurred with details : " + e.toString());
        } catch (JsonParseException e) {
            Log.e(TAG, "JSON Parse exception occurred with details : " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "IO Exception occurred with details : " + e.toString());
        }

        if (aqi != null && !(aqi.isEmpty()))
            setValues();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof MainActivity) {
            rootView = inflater.inflate(R.layout.activity_global, container, false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.tittle_global);
        } else {
            rootView = inflater.inflate(R.layout.activity_global_private, container, false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_global_private);
        }

        dbHelper = new SQLiteDatabaseHelper(getActivity().getApplicationContext());
        payloadDataList = new ArrayList<HourlyData>();
        donutLayouts = new ArrayList<LinearLayout>();
        donutViews = new ArrayList<CircularProgressBar>();
        ConfigView();

        fetchLatestDeviceData();

        setValues();
        //init datalists for analytics


        currentSelectedActivity = 0;
        onClick();
        return rootView;
    }

    private PayloadData fetchPayloadData() {
        return payloadData;
    }

    // To setup donuts
    private void setUpDonuts() {
        int flag = 1;
        PayloadData payloadData = fetchPayloadData();
//        Log.e(TAG, "Payload data :" + payloadData.toString());

        gasesScrollBar = (HorizontalScrollView) rootView.findViewById(R.id.childScroll);
        textProgresspm10 = (TextView) rootView.findViewById(R.id.textProgresspm10);
        textProgressco2 = (TextView) rootView.findViewById(R.id.textProgressco2);
        textProgressg2co = (TextView) rootView.findViewById(R.id.textProgressg2co);
        textProgressnh3 = (TextView) rootView.findViewById(R.id.textProgressnh3);
        textProgressg5 = (TextView) rootView.findViewById(R.id.textProgressg5);
        textProgressh2s = (TextView) rootView.findViewById(R.id.textProgressh2s);
        textProgressg7 = (TextView) rootView.findViewById(R.id.textProgressg7);
        textProgressg8 = (TextView) rootView.findViewById(R.id.textProgressg8);
        textProgressg9 = (TextView) rootView.findViewById(R.id.textProgresspm10);
        textProgresso3 = (TextView) rootView.findViewById(R.id.textProgresso3);
        textProgressco = (TextView) rootView.findViewById(R.id.textProgressco);
        textProgressno2 = (TextView) rootView.findViewById(R.id.textProgressno2);
        textProgresssso2 = (TextView) rootView.findViewById(R.id.textProgresssso2);
        textProgresspm25 = (TextView) rootView.findViewById(R.id.textProgresspm25);
        textProgressp2 = (TextView) rootView.findViewById(R.id.textProgressp2);
        textProgressp3 = (TextView) rootView.findViewById(R.id.textProgressp3);
        textProgressp10 = (TextView) rootView.findViewById(R.id.textProgressp10);

        if (payloadData != null) {

            g1 = (int) (100 * payloadData.getG1() / 2000);
            g2 = (int) (100 * payloadData.getG2() / 34);
            g3 = (int) (100 * payloadData.getG3() / 400);
            g4 = (int) (100 * payloadData.getG4() / 1800);
            g5 = (int) (100 * payloadData.getG5() / 748);
            g6 = (int) (100 * payloadData.getG6() / 10);
            g7 = (int) (100 * payloadData.getG7() / 10);
            g8 = (int) (100 * payloadData.getG8() / 1600);
            g9 = (int) (100 * payloadData.getG9() / 10);
            o3 = (int) (100 * payloadData.getO3() / 748);
            co = (int) (100 * payloadData.getCo() / 34);
            no2 = (int) (100 * payloadData.getNo2() / 400);
            so2 = (int) (100 * payloadData.getSo2() / 1600);
            pm25 = (int) (100 * payloadData.getCpcb_p2() / 250);
            pm10 = (int) (100 * payloadData.getCpcb_p1() / 430);
            p1 = (int) (100 * payloadData.getP1() / 250);
            p2 = (int) (100 * payloadData.getP2() / 430);
            p3 = (int) (100 * payloadData.getP3() / 250);


            //     Log.i(TAG, "Info of all device :" + "g3:" + g3 + " g2:" + g2 + " p1:" + p1 + "" + " o3:" + o3 + " g4:" + g4 + " co:" + co + " p2" + p2 + " g1:" + g1 +
            //           " g8:" + g8 + " g5:" + g5 + " pm25:" + pm25 + " pm10:" + pm10 + " Original p1" + payloadData.getP1() + " " + payloadData.getP2() + "Original O3 :" + payloadData.getO3());
        }

        initDonutLayouts();

        if (payloadData != null) {

            if (payloadData.getCpcb_p1() != 0) {
                flag = 0;
                if (pm10 == 0) {
                    donutViews.get(0).setProgress(min);
                } else {
                    donutViews.get(0).setProgress(pm10);
                }
                textProgresspm10.setText(Float.toString((float) payloadData.getCpcb_p1()));
                textProgresspm10.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(0).setVisibility(View.VISIBLE);
            }
            if (payloadData.getG1() != 0) {
                flag = 0;
                if (g1 == 0) {
                    donutViews.get(1).setProgress(min);
                } else {
                    donutViews.get(1).setProgress(g1);
                }
                donutViews.get(1).setProgress(g1);
                textProgressco2.setText(Float.toString((float) payloadData.getG1()));
                textProgressco2.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(1).setVisibility(View.VISIBLE);
            }
            if (payloadData.getG2() != 0) {
                flag = 0;
                if (g2 == 0) {
                    donutViews.get(2).setProgress(min);
                } else {
                    donutViews.get(2).setProgress(g2);
                }
                textProgressg2co.setText(Float.toString((float) payloadData.getG2()));
                textProgressg2co.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(1).setVisibility(View.VISIBLE);
            }
            if (payloadData.getG4() != 0) {
                flag = 0;
                if (g4 == 0) {
                    donutViews.get(3).setProgress(min);
                } else {
                    donutViews.get(3).setProgress(g4);
                }
                textProgressnh3.setText(Float.toString((float) payloadData.getG4()));
                textProgressnh3.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(3).setVisibility(View.VISIBLE);
            }
            if (payloadData.getG5() != 0) {
                flag = 0;
                if (g5 == 0) {
                    donutViews.get(4).setProgress(min);
                } else {
                    donutViews.get(4).setProgress(g5);
                }
                textProgressg5.setText(Float.toString((float) payloadData.getG5()));
                textProgressg5.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(4).setVisibility(View.VISIBLE);
            }
            if (payloadData.getG6() != 0 && payloadData.getG6() > 0) {
                flag = 0;
                if (g6 == 0) {
                    donutViews.get(5).setProgress(min);
                } else {
                    donutViews.get(5).setProgress(g6);
                }
                textProgressh2s.setText(Float.toString((float) payloadData.getG6()));
                textProgressh2s.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(5).setVisibility(View.VISIBLE);
            }
            if (payloadData.getG7() != 0) {
                flag = 0;
                if (g7 == 0) {
                    donutViews.get(6).setProgress(min);
                } else {
                    donutViews.get(6).setProgress(g7);
                }
                textProgressg7.setText(Float.toString((float) payloadData.getG7()));
                textProgressg7.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(6).setVisibility(View.VISIBLE);
            }
            if (payloadData.getG8() != 0) {
                flag = 0;
                if (g8 == 0) {
                    donutViews.get(7).setProgress(min);
                } else {
                    donutViews.get(7).setProgress(g8);
                }
                textProgressg8.setText(Float.toString((float) payloadData.getG8()));
                textProgressg8.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(7).setVisibility(View.VISIBLE);
            }
            if (payloadData.getG9() != 0) {
                flag = 0;
                if (g9 == 0) {
                    donutViews.get(8).setProgress(min);
                } else {
                    donutViews.get(8).setProgress(g9);
                }
                textProgressg9.setText(Float.toString((float) payloadData.getG9()));
                textProgressg9.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(8).setVisibility(View.VISIBLE);
            }
            if (payloadData.getO3() != 0) {
                flag = 0;
                if (o3 == 0) {
                    donutViews.get(9).setProgress(min);
                } else {
                    donutViews.get(9).setProgress(o3);
                }
                textProgresso3.setText(Float.toString((float) payloadData.getO3()));
                textProgresso3.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(9).setVisibility(View.VISIBLE);
            }
            if (payloadData.getCo() != 0) {
                flag = 0;
                if (co == 0) {
                    donutViews.get(10).setProgress(min);
                } else {
                    donutViews.get(10).setProgress(co);
                }
                textProgressco.setText(Float.toString((float) payloadData.getCo()));
                textProgressco.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(10).setVisibility(View.VISIBLE);
            }
            if (payloadData.getNo2() != 0) {
                flag = 0;
                if (no2 == 0) {
                    donutViews.get(11).setProgress(min);
                } else {
                    donutViews.get(11).setProgress(no2);
                }
                textProgressno2.setText(Float.toString((float) payloadData.getNo2()));
                textProgressno2.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(11).setVisibility(View.VISIBLE);
            }
            if (payloadData.getSo2() != 0) {
                flag = 0;
                if (so2 == 0) {
                    donutViews.get(12).setProgress(min);
                } else {
                    donutViews.get(12).setProgress(so2);
                }
                textProgresssso2.setText(Float.toString((float) payloadData.getSo2()));
                textProgresssso2.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(12).setVisibility(View.VISIBLE);
            }
            if (payloadData.getCpcb_p2() != 0) {
                flag = 0;
                if (pm25 == 0) {
                    donutViews.get(13).setProgress(min);
                } else {
                    donutViews.get(13).setProgress(pm25);
                }
                textProgresspm25.setText(Float.toString((float) payloadData.getCpcb_p2()));
                textProgresspm25.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(13).setVisibility(View.VISIBLE);
            }
            if (payloadData.getP1() != 0) {
                flag = 0;
                if (p1 == 0) {
                    donutViews.get(14).setProgress(min);
                } else {
                    donutViews.get(14).setProgress(p1);
                }
                textProgressp10.setText(Float.toString((float) payloadData.getP1()));
                textProgressp10.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(14).setVisibility(View.VISIBLE);
            }
            if (payloadData.getP2() != 0) {
                flag = 0;
                if (p2 == 0) {
                    donutViews.get(15).setProgress(min);
                } else {
                    donutViews.get(15).setProgress(p2);
                }
                donutViews.get(15).setProgress(p2);
                textProgressp2.setText(Float.toString((float) payloadData.getP2()));
                textProgressp2.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(15).setVisibility(View.VISIBLE);
            }
            if (payloadData.getP3() != 0) {
                flag = 0;
                if (p3 == 0) {
                    donutViews.get(16).setProgress(min);
                } else {
                    donutViews.get(16).setProgress(p3);
                }
                textProgressp3.setText(Float.toString((float) payloadData.getP3()));
                textProgressp3.setTextColor(Color.parseColor("#454444"));
                donutLayouts.get(16).setVisibility(View.VISIBLE);
            }
        }
        if (flag == 0) {
            gasesScrollBar.setVisibility(View.VISIBLE);
        }
    }


    //To initialization of donut view
    private void initDonutLayouts() {
        PayloadData payloadData = fetchPayloadData();

        CircularProgressBar donutProgress;
        LinearLayout donutLayout;

        if (payloadData != null) {

            //pm10
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progresspm10);
            pm10Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progresspm10Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //co2(g1)
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progresspmco2);
            g1Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressco2Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //co(g2)
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressg2co);
            g2Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressCOLayout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //nh3(g4)
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressnh3);
            g4Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressnh3Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //g5(o3)
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressg5);
            g5Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressg5Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //g6(h2s)
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressh2s);
            g6Linits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressh2sLayout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //g7(no2)
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressg7);
            g7Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressg7Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //g8(so2)
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressg8);
            g8Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressg8Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //g9(sco)
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressg9);
            g9Linits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressg9Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //o3
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progresso3);
            o3Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progresso3Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //co
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressco);
            coLimits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progresscoLayout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //no2
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressno2);
            no2Linits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressno2Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //s02
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressso2);
            so2Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressso2Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //pm25
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progresspm25);
            p25Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progresspm25Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //p1
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressp1);
            p1Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressp1Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //p2
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressp2);
            p2Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progresspmLayout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

            //p3
            donutProgress = (CircularProgressBar) rootView.findViewById(R.id.donut_progressp3);
            p3Limits(payloadData, donutProgress);
            donutLayout = (LinearLayout) rootView.findViewById(R.id.progressp3Layout);
            donutViews.add(donutProgress);
            donutLayouts.add(donutLayout);

        }
    }

    private void no2Linits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getNo2() && payloadData.getNo2() <= 40) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (40 < payloadData.getNo2() && payloadData.getNo2() <= 80) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (80 < payloadData.getNo2() && payloadData.getNo2() <= 180) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (180 < payloadData.getNo2() && payloadData.getNo2() <= 280) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (280 < payloadData.getNo2() && payloadData.getNo2() <= 400) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getNo2() > 400) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void p2Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getP2() && payloadData.getP2() <= 50) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (50 < payloadData.getP2() && payloadData.getP2() <= 100) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (100 < payloadData.getP2() && payloadData.getP2() <= 250) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (250 < payloadData.getP2() && payloadData.getP2() <= 350) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (350 < payloadData.getP2() && payloadData.getP2() <= 430) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getP2() > 430) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void p1Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getP1() && payloadData.getP1() <= 30) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (30 < payloadData.getP1() && payloadData.getP1() <= 60) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (60 < payloadData.getP1() && payloadData.getP1() <= 90) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (90 < payloadData.getP1() && payloadData.getP1() <= 120) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (120 < payloadData.getP1() && payloadData.getP1() <= 250) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getP1() > 250) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }

    }

    private void so2Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getSo2() && payloadData.getSo2() <= 40) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (40 < payloadData.getSo2() && payloadData.getSo2() <= 80) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (80 < payloadData.getSo2() && payloadData.getSo2() <= 380) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (380 < payloadData.getSo2() && payloadData.getSo2() <= 800) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (800 < payloadData.getSo2() && payloadData.getSo2() <= 1600) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getSo2() > 1600) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void g6Linits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getG6() && payloadData.getG6() <= 1) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (1 < payloadData.getG6() && payloadData.getG6() <= 2) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (2 < payloadData.getG6() && payloadData.getG6() <= 10) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (10 < payloadData.getG6() && payloadData.getG6() <= 17) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (payloadData.getG6() > 17) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void g5Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getG5() && payloadData.getG5() <= 50) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (50 < payloadData.getG5() && payloadData.getG5() <= 100) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (100 < payloadData.getG5() && payloadData.getG5() <= 168) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (168 < payloadData.getG5() && payloadData.getG5() <= 208) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (208 < payloadData.getG5() && payloadData.getG5() <= 748) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getG5() > 748) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void g4Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getG4() && payloadData.getG4() <= 200) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (200 < payloadData.getG4() && payloadData.getG4() <= 400) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (400 < payloadData.getG4() && payloadData.getG4() <= 800) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (800 < payloadData.getG4() && payloadData.getG4() <= 1200) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (1200 < payloadData.getG4() && payloadData.getG4() <= 1800) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getG4() > 1800) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void g2Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getG2() && payloadData.getG2() <= 1) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (1 < payloadData.getG2() && payloadData.getG2() <= 2) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (2 < payloadData.getG2() && payloadData.getG2() <= 10) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (10 < payloadData.getG2() && payloadData.getG2() <= 17) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (17 < payloadData.getG2() && payloadData.getG2() <= 34) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getG2() > 34) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void g1Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getG1() && payloadData.getG1() <= 600) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (600 < payloadData.getG1() && payloadData.getG1() <= 800) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (800 < payloadData.getG1() && payloadData.getG1() <= 1000) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (1000 < payloadData.getG1() && payloadData.getG1() <= 1200) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (1200 < payloadData.getG1() && payloadData.getG1() <= 1400) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (1400 < payloadData.getG1()) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void pm10Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getCpcb_p1() && payloadData.getCpcb_p1() <= 50) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (50 < payloadData.getCpcb_p1() && payloadData.getCpcb_p1() <= 100) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));

        } else if (100 < payloadData.getCpcb_p1() && payloadData.getCpcb_p1() <= 250) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (250 < payloadData.getCpcb_p1() && payloadData.getCpcb_p1() <= 350) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (350 < payloadData.getCpcb_p1() && payloadData.getCpcb_p1() <= 430) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getCpcb_p1() > 430) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void p25Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getCpcb_p2() && payloadData.getCpcb_p2() <= 30) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (30 < payloadData.getCpcb_p2() && payloadData.getCpcb_p2() <= 60) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (60 < payloadData.getCpcb_p2() && payloadData.getCpcb_p2() <= 90) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (90 < payloadData.getCpcb_p2() && payloadData.getCpcb_p2() <= 120) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (120 < payloadData.getCpcb_p2() && payloadData.getCpcb_p2() <= 250) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getCpcb_p2() > 250) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }

    }

    private void g8Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getG8() && payloadData.getG8() <= 40) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (40 < payloadData.getG8() && payloadData.getG8() <= 80) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (80 < payloadData.getG8() && payloadData.getG8() <= 380) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (380 < payloadData.getG8() && payloadData.getG8() <= 800) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (800 < payloadData.getG8() && payloadData.getG8() <= 1600) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getG8() > 1600) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void p3Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getP3() && payloadData.getP3() <= 40) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (40 < payloadData.getP3() && payloadData.getP3() <= 80) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (80 < payloadData.getP3() && payloadData.getP3() <= 380) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (380 < payloadData.getP3() && payloadData.getP3() <= 800) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (800 < payloadData.getP3() && payloadData.getP3() <= 1600) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getP3() > 1600) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void coLimits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getCo() && payloadData.getCo() <= 1) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (1 < payloadData.getCo() && payloadData.getCo() <= 2) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (2 < payloadData.getCo() && payloadData.getCo() <= 10) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (10 < payloadData.getCo() && payloadData.getCo() <= 17) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (17 < payloadData.getCo() && payloadData.getCo() <= 34) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getCo() > 34) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void g7Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getG7() && payloadData.getG7() <= 200) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (200 < payloadData.getG7() && payloadData.getG7() <= 400) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (400 < payloadData.getG7() && payloadData.getG7() <= 800) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (800 < payloadData.getG7() && payloadData.getG7() <= 1200) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (1200 < payloadData.getG7() && payloadData.getG7() <= 1800) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getG7() > 1800) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void o3Limits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getO3() && payloadData.getO3() <= 50) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (50 < payloadData.getO3() && payloadData.getO3() <= 100) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (100 < payloadData.getO3() && payloadData.getO3() <= 168) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (168 < payloadData.getO3() && payloadData.getO3() <= 208) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (208 < payloadData.getO3() && payloadData.getO3() <= 748) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else if (payloadData.getO3() > 748) {
            //severe_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }

    private void g9Linits(PayloadData payloadData, CircularProgressBar donutProgress) {
        if (0 <= payloadData.getG9() && payloadData.getG9() <= 1) {
            //good_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.good));
        } else if (1 < payloadData.getG9() && payloadData.getG9() <= 2) {
            //satisfactory_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.satisfactory));
        } else if (2 < payloadData.getG9() && payloadData.getG9() <= 10) {
            // moderate_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.moderate));
        } else if (10 < payloadData.getG9() && payloadData.getG9() <= 17) {
            //poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.poor));
        } else if (payloadData.getG9() > 17) {
            //very_poor_map_marker
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.verypoor));
        } else {
            donutProgress.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.severe));
        }
    }


    private void prepareValueFromJson(JSONObject deviceJSON) {
        try {
            aqi = String.valueOf(deviceJSON.getInt("aqi"));
            deviceLabel = deviceJSON.getString("label");
            city = deviceJSON.getString("city");
            deviceId = deviceJSON.getString("deviceId");
            status = CommonHelpers.getStatus(deviceJSON.getInt("aqi"));
            lastUpdatedTime = String.valueOf(deviceJSON.getLong("t"));

            ObjectMapper mapper = new ObjectMapper();
            JSONObject dJson = deviceJSON.getJSONObject("payload").getJSONObject("d");
            payloadData = mapper.readValue(dJson.toString(), PayloadData.class);


        } catch (JSONException e) {
            Log.e(TAG, "json exception occurred in prepareValueFromJson() fun with detail : " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "IO Exception occurred with details in preparevaluefromjson() : " + e.toString());
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JSONObject dJson = deviceJSON.getJSONObject("payload").getJSONObject("d");
            payloadData = mapper.readValue(dJson.toString(), PayloadData.class);

            temp = deviceJSON.getInt("temp");
            hum = deviceJSON.getInt("hum");
        } catch (JSONException e) {
            Log.e(TAG, "IO Exception2 occurred with details in preparevaluefromjson()  : " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "IO Exception occurred with details in preparevaluefromjson() : " + e.toString());
        }
        setValues();
        setUpDonuts();
    }

    private void onClick() {
        imgActivity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActivity(1);
            }
        });
        imgActivity2.setOnClickListener(this);
        imgActivity3.setOnClickListener(this);
        imgActivity4.setOnClickListener(this);
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareFunction();

            }
        });

        shareRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDeviceData();
            }
        });
    }

    private void updateDeviceData() {
        Context context = getActivity().getApplicationContext();
        if (getActivity() instanceof MainActivity) {
            updatePublicData(deviceId, context);
        } else {
            updatePrivateData(deviceId, context);
        }
    }

    private void updatePrivateData(String deviceId, Context context) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
        ECAPIHelper.fetchPrivateDeviceData(deviceId, context, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject deviceJson = response.getJSONObject(0);
                        prepareValueFromJson(deviceJson);
                        dbHelper.updatePrivateData(deviceJson);
                        dbHelper.updateLatestPrivateObj(deviceJson);
                        loading.dismiss();
                    } catch (JSONException e) {
                        loading.dismiss();
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                    }
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

    private void updatePublicData(String deviceId, Context context) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
        ECAPIHelper.fetchPublicDeviceData(deviceId, context, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject deviceJson = response.getJSONObject(0);
                        prepareValueFromJson(deviceJson);
                        dbHelper.updatePublicData(deviceJson);
                        dbHelper.updateLatestPublicObj(deviceJson);
                        loading.dismiss();
                    } catch (JSONException e) {
                        loading.dismiss();
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                    }
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

    private void ConfigView() {
        shareIcon = (TextView) rootView.findViewById(R.id.txtShare);
        shareRefresh = (TextView) rootView.findViewById(R.id.txtRefresh);
        aqiGauge = (ImageView) rootView.findViewById(R.id.imgGauge);
        aqiReading = (TextView) rootView.findViewById(R.id.txtAqiReading);
        aqiStatus = (TextView) rootView.findViewById(R.id.txtAvgAqi);
        imgActivity1 = (ImageView) rootView.findViewById(R.id.imgActivity1);
        imgActivity2 = (ImageView) rootView.findViewById(R.id.imgActivity2);
        imgActivity3 = (ImageView) rootView.findViewById(R.id.imgActivity3);
        imgActivity4 = (ImageView) rootView.findViewById(R.id.imgActivity4);
        txtActivity = (TextView) rootView.findViewById(R.id.txtActivity);
        txtTemp = (TextView) rootView.findViewById(R.id.txtTemp);
        txtHumidity = (TextView) rootView.findViewById(R.id.txtHumidity);
        txtTime = (TextView) rootView.findViewById(R.id.txtTime);
        txtDate = (TextView) rootView.findViewById(R.id.txtDate);
        activityLabel = (TextView) rootView.findViewById(R.id.globaleDeviceLabel);
        setBebasNeue();
        setShareIcon();
    }

    private void setValues() {
        //aqiGauge.setImageResource(0);
        if (status != null && !status.isEmpty()) {
            if (status.equals("Good")) {
                //Picasso.with(getActivity().getApplicationContext()).load(R.drawable.good).into(aqiGauge);
                aqiGauge.setImageResource(R.drawable.good);
            } else if (status.equals("Satisfactory")) {
                //Picasso.with(getActivity().getApplicationContext()).load(R.drawable.satisfactory).into(aqiGauge);
                aqiGauge.setImageResource(R.drawable.satisfactory);
            } else if (status.equals("Moderate")) {
                //Picasso.with(getActivity().getApplicationContext()).load(R.drawable.moderate).into(aqiGauge);
                aqiGauge.setImageResource(R.drawable.moderate);
            } else if (status.equals("Poor")) {
                //Picasso.with(getActivity().getApplicationContext()).load(R.drawable.poor).into(aqiGauge);
                aqiGauge.setImageResource(R.drawable.poor);
            } else if (status.equals("Very Poor")) {
                //Picasso.with(getActivity().getApplicationContext()).load(R.drawable.verypoor).into(aqiGauge);
                aqiGauge.setImageResource(R.drawable.verypoor);
            } else if (status.equals("Severe")) {
                //Picasso.with(getActivity().getApplicationContext()).load(R.drawable.severe).into(aqiGauge);
                aqiGauge.setImageResource(R.drawable.severe);
            } else {
                //Picasso.with(getActivity().getApplicationContext()).load(R.drawable.good).into(aqiGauge);
                aqiGauge.setImageResource(R.drawable.satisfactory);
            }
        }


        aqiReading.setText(aqi);
        aqiStatus.setText(status);
        activityLabel.setText(deviceLabel + " , " + city);

        if (lastUpdatedTime != "" && !lastUpdatedTime.isEmpty()) {
            Date date = new Date(Long.valueOf(lastUpdatedTime) * 1000);

            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");
            String fullTime = dateFormat.format(date);

            String[] strArray = fullTime.split(" ");
            String[] dateArray = strArray[0].split("-");
            String[] timeArray = strArray[1].split(":");
            txtDate.setText(dateArray[0].toString() + " " + dateArray[1]);
            txtTime.setText(timeArray[0] + ":" + timeArray[1] + " " + strArray[2]);
        } else {
            //Else block
        }
        if (temp != 0) {
            txtTemp.setText(String.valueOf(temp) + (char) 0x00B0 + "C");
        } else {
            txtTemp.setVisibility(GONE);
        }

        if (hum != 0) {
            txtHumidity.setText(String.valueOf(hum) + "%");
        } else {
            txtHumidity.setVisibility(GONE);
        }

        setUpDonuts();
        fillAllActivities();
        selectActivity(1);
    }

    private void setShareIcon() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/ionicons.ttf");
        shareIcon.setTypeface(font);
        shareRefresh.setTypeface(font);
    }

    private void setBebasNeue() {
        Typeface font = Typeface.createFromAsset(
                getContext().getAssets(),
                "fonts/BebasNeue.ttf");
        aqiReading.setTypeface(font);
    }

    private void setActivities() {
        boolean flag;
        if (getActivity() instanceof MainActivity) {
            flag = true;
        } else {
            flag = false;
        }
        if (status.equals("Good")) {
            s_activity1 = Activities.S_GOOD_ACTIVITY_1;
            ic_activity1 = Activities.IC_GOOD_ACTIVITY_1;
            if (flag) {
                ic_activity1_fill = Activities.IC_GOOD_ACTIVITY_1_FILL;
            } else {
                ic_activity1_fill = Activities.IC_GOOD_ACTIVITY_1_BLUE;
            }


            s_activity2 = Activities.S_GOOD_ACTIVITY_2;
            ic_activity2 = Activities.IC_GOOD_ACTIVITY_2;
            if (flag) {
                ic_activity2_fill = Activities.IC_GOOD_ACTIVITY_2_FILL;
            } else {
                ic_activity2_fill = Activities.IC_GOOD_ACTIVITY_2_BLUE;
            }

            s_activity3 = Activities.S_GOOD_ACTIVITY_3;
            ic_activity3 = Activities.IC_GOOD_ACTIVITY_3;
            if (flag) {
                ic_activity3_fill = Activities.IC_GOOD_ACTIVITY_3_FILL;
            } else {
                ic_activity3_fill = Activities.IC_GOOD_ACTIVITY_3_BLUE;
            }

            s_activity4 = Activities.S_GOOD_ACTIVITY_4;
            ic_activity4 = Activities.IC_GOOD_ACTIVITY_4;
            if (flag) {
                ic_activity4_fill = Activities.IC_GOOD_ACTIVITY_4_FILL;
            } else {
                ic_activity4_fill = Activities.IC_GOOD_ACTIVITY_4_BLUE;
            }

        } else if (status.equals("Satisfactory")) {
            s_activity1 = Activities.S_SATISFACTORY_ACTIVITY_1;
            ic_activity1 = Activities.IC_SATISFACTORY_ACTIVITY_1;
            if (flag) {
                ic_activity1_fill = Activities.IC_SATISFACTORY_ACTIVITY_1_FILL;
            } else {
                ic_activity1_fill = Activities.IC_SATISFACTORY_ACTIVITY_1_BLUE;
            }

            s_activity2 = Activities.S_SATISFACTORY_ACTIVITY_2;
            ic_activity2 = Activities.IC_SATISFACTORY_ACTIVITY_2;
            if (flag) {
                ic_activity2_fill = Activities.IC_SATISFACTORY_ACTIVITY_2_FILL;
            } else {
                ic_activity2_fill = Activities.IC_SATISFACTORY_ACTIVITY_2_BLUE;
            }

            s_activity3 = Activities.S_SATISFACTORY_ACTIVITY_3;
            ic_activity3 = Activities.IC_SATISFACTORY_ACTIVITY_3;
            if (flag) {
                ic_activity3_fill = Activities.IC_SATISFACTORY_ACTIVITY_3_FILL;
            } else {
                ic_activity3_fill = Activities.IC_SATISFACTORY_ACTIVITY_3_BLUE;
            }

            s_activity4 = Activities.S_SATISFACTORY_ACTIVITY_4;
            ic_activity4 = Activities.IC_SATISFACTORY_ACTIVITY_4;
            if (flag) {
                ic_activity4_fill = Activities.IC_SATISFACTORY_ACTIVITY_4_FILL;
            } else {
                ic_activity4_fill = Activities.IC_SATISFACTORY_ACTIVITY_4_BLUE;
            }
        } else if (status.equals("Moderate")) {
            s_activity1 = Activities.S_MODERATE_ACTIVITY_1;
            ic_activity1 = Activities.IC_MODERATE_ACTIVITY_1;
            if (flag) {
                ic_activity1_fill = Activities.IC_MODERATE_ACTIVITY_1_FILL;
            } else {
                ic_activity1_fill = Activities.IC_MODERATE_ACTIVITY_1_BLUE;
            }

            s_activity2 = Activities.S_MODERATE_ACTIVITY_2;
            ic_activity2 = Activities.IC_MODERATE_ACTIVITY_2;
            if (flag) {
                ic_activity2_fill = Activities.IC_MODERATE_ACTIVITY_2_FILL;
            } else {
                ic_activity2_fill = Activities.IC_MODERATE_ACTIVITY_2_BLUE;
            }

            s_activity3 = Activities.S_MODERATE_ACTIVITY_3;
            ic_activity3 = Activities.IC_MODERATE_ACTIVITY_3;
            if (flag) {
                ic_activity3_fill = Activities.IC_MODERATE_ACTIVITY_3_FILL;
            } else {
                ic_activity3_fill = Activities.IC_MODERATE_ACTIVITY_3_BLUE;
            }

            s_activity4 = Activities.S_MODERATE_ACTIVITY_4;
            ic_activity4 = Activities.IC_MODERATE_ACTIVITY_4;
            if (flag) {
                ic_activity4_fill = Activities.IC_MODERATE_ACTIVITY_4_FILL;
            } else {
                ic_activity4_fill = Activities.IC_MODERATE_ACTIVITY_4_BLUE;
            }
        } else if (status.equals("Poor")) {
            s_activity1 = Activities.S_POOR_ACTIVITY_1;
            ic_activity1 = Activities.IC_POOR_ACTIVITY_1;
            if (flag) {
                ic_activity1_fill = Activities.IC_POOR_ACTIVITY_1_FILL;
            } else {
                ic_activity1_fill = Activities.IC_POOR_ACTIVITY_1_BLUE;
            }

            s_activity2 = Activities.S_POOR_ACTIVITY_2;
            ic_activity2 = Activities.IC_POOR_ACTIVITY_2;
            if (flag) {
                ic_activity2_fill = Activities.IC_POOR_ACTIVITY_2_FILL;
            } else {
                ic_activity2_fill = Activities.IC_POOR_ACTIVITY_2_BLUE;
            }

            s_activity3 = Activities.S_POOR_ACTIVITY_3;
            ic_activity3 = Activities.IC_POOR_ACTIVITY_3;
            if (flag) {
                ic_activity3_fill = Activities.IC_POOR_ACTIVITY_3_FILL;
            } else {
                ic_activity3_fill = Activities.IC_POOR_ACTIVITY_3_BLUE;
            }

            s_activity4 = Activities.S_POOR_ACTIVITY_4;
            ic_activity4 = Activities.IC_POOR_ACTIVITY_4;
            if (flag) {
                ic_activity4_fill = Activities.IC_POOR_ACTIVITY_4_FILL;
            } else {
                ic_activity4_fill = Activities.IC_POOR_ACTIVITY_4_BLUE;
            }
        } else if (status.equals("Very Poor")) {
            s_activity1 = Activities.S_VERY_POOR_ACTIVITY_1;
            ic_activity1 = Activities.IC_VERY_POOR_ACTIVITY_1;
            if (flag) {
                ic_activity1_fill = Activities.IC_VERY_POOR_ACTIVITY_1_FILL;
            } else {
                ic_activity1_fill = Activities.IC_VERY_POOR_ACTIVITY_1_BLUE;
            }

            s_activity2 = Activities.S_VERY_POOR_ACTIVITY_2;
            ic_activity2 = Activities.IC_VERY_POOR_ACTIVITY_2;
            if (flag) {
                ic_activity2_fill = Activities.IC_VERY_POOR_ACTIVITY_2_FILL;
            } else {
                ic_activity2_fill = Activities.IC_VERY_POOR_ACTIVITY_2_BLUE;
            }

            s_activity3 = Activities.S_VERY_POOR_ACTIVITY_3;
            ic_activity3 = Activities.IC_VERY_POOR_ACTIVITY_3;
            if (flag) {
                ic_activity3_fill = Activities.IC_VERY_POOR_ACTIVITY_3_FILL;
            } else {
                ic_activity3_fill = Activities.IC_VERY_POOR_ACTIVITY_3_BLUE;
            }

            s_activity4 = Activities.S_VERY_POOR_ACTIVITY_4;
            ic_activity4 = Activities.IC_VERY_POOR_ACTIVITY_4;
            if (flag) {
                ic_activity4_fill = Activities.IC_VERY_POOR_ACTIVITY_4_FILL;
            } else {
                ic_activity4_fill = Activities.IC_VERY_POOR_ACTIVITY_4_BLUE;
            }
        } else if (status.equals("Severe")) {
            s_activity1 = Activities.S_SEVERE_ACTIVITY_1;
            ic_activity1 = Activities.IC_SEVERE_ACTIVITY_1;
            if (flag) {
                ic_activity1_fill = Activities.IC_SEVERE_ACTIVITY_1_FILL;
            } else {
                ic_activity1_fill = Activities.IC_SEVERE_ACTIVITY_1_BLUE;
            }

            s_activity2 = Activities.S_SEVERE_ACTIVITY_2;
            ic_activity2 = Activities.IC_SEVERE_ACTIVITY_2;
            if (flag) {
                ic_activity2_fill = Activities.IC_SEVERE_ACTIVITY_2_FILL;
            } else {
                ic_activity2_fill = Activities.IC_SEVERE_ACTIVITY_2_BLUE;
            }

            s_activity3 = Activities.S_SEVERE_ACTIVITY_3;
            ic_activity3 = Activities.IC_SEVERE_ACTIVITY_3;
            if (flag) {
                ic_activity3_fill = Activities.IC_SEVERE_ACTIVITY_3_FILL;
            } else {
                ic_activity3_fill = Activities.IC_SEVERE_ACTIVITY_3_BLUE;
            }

            s_activity4 = Activities.S_SEVERE_ACTIVITY_4;
            ic_activity4 = Activities.IC_SEVERE_ACTIVITY_4;
            if (flag) {
                ic_activity4_fill = Activities.IC_SEVERE_ACTIVITY_4_FILL;
            } else {
                ic_activity4_fill = Activities.IC_SEVERE_ACTIVITY_4_BLUE;
            }
        } else {

        }
    }

    @Override
    public void onClick(View v) {
        if (v == imgActivity2) {
            selectActivity(2);
        } else if (v == imgActivity3) {
            selectActivity(3);
        } else {
            selectActivity(4);
        }
    }

    //Sharing function for share data
    private void shareFunction() {
        //  dbHelper.readDeviceData();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        //Hey, The Air Quality at location name is 301 find out Air Quality of your area on link.
        share.putExtra(Intent.EXTRA_TEXT, "Hey, The Air Quality at " + deviceLabel + ", " + city + " is " + aqi + " (" + status + "). Find out Air Quality of your area on http://bit.ly/29tmSFB #KnowWhatYouBreathe");
        startActivity(Intent.createChooser(share, "Share using"));

    }

    private int currentSelectedActivity;

    private void fillAllActivities() {
        imgActivity1.setImageResource(ic_activity1);
        imgActivity2.setImageResource(ic_activity2);
        imgActivity3.setImageResource(ic_activity3);
        imgActivity4.setImageResource(ic_activity4);
    }

    private void selectActivity(int i) {
        if (currentSelectedActivity != 0) {
            switch (currentSelectedActivity) {
                case 1:
                    imgActivity1.setImageResource(ic_activity1);
                    break;
                case 2:
                    imgActivity2.setImageResource(ic_activity2);
                    break;
                case 3:
                    imgActivity3.setImageResource(ic_activity3);
                    break;
                case 4:
                    imgActivity4.setImageResource(ic_activity4);
                    break;
                default:
                    imgActivity1.setImageResource(ic_activity1);
                    break;
            }
        } else {
        }

        switch (i) {
            case 1:
                imgActivity1.setImageResource(ic_activity1_fill);
                txtActivity.setText(s_activity1);
                break;
            case 2:
                imgActivity2.setImageResource(ic_activity2_fill);
                txtActivity.setText(s_activity2);
                break;
            case 3:
                imgActivity3.setImageResource(ic_activity3_fill);
                txtActivity.setText(s_activity3);
                break;
            case 4:
                imgActivity4.setImageResource(ic_activity4_fill);
                txtActivity.setText(s_activity4);
                break;
            default:
                imgActivity4.setImageResource(ic_activity4_fill);
                txtActivity.setText(s_activity4);
                break;

        }
        currentSelectedActivity = i;
    }


}
