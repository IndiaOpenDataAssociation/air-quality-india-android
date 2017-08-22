package com.idn0phl3108ed43d22s30;

import android.app.ProgressDialog;
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
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.idn0phl3108ed43d22s30.Linechart.LineCardOne;
import com.idn0phl3108ed43d22s30.Linechart.LineCardTwo;
import com.idn0phl3108ed43d22s30.SQLite.SQLiteDatabaseHelper;
import com.idn0phl3108ed43d22s30.barchart.BarCardOne;
import com.idn0phl3108ed43d22s30.barchart.BarCardTwo;
import com.idn0phl3108ed43d22s30.etc.Constants;
import com.idn0phl3108ed43d22s30.network.ECAPIHelper;
import com.idn0phl3108ed43d22s30.pojo.HourlyData;
import com.idn0phl3108ed43d22s30.pojo.Payload;
import com.idn0phl3108ed43d22s30.pojo.PayloadData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FragmentAnalytics extends Fragment {
    private static final String TAG = "FragmentAnalytics";

    private View rootView;
    private String[] timeValues;
    private float[][] aqiValues;
    private List<String> timeValuesList;
    private List<Float> aqiValuesList;
    private List<HourlyData> payloadDataList;


    private TextView txtAqiReading, txtStatus, txtLineChart, txtBarChart;


    private List<LinearLayout> donutLayouts;
    private List<CircularProgressBar> donutViews;
    private List<TextView> textData;
    private TextView globaleDeviceLabel;
    private TextView textProgresspm10, textProgressco2, textProgressg2co, textProgressnh3, textProgressg5, textProgressh2s, textProgressg7,
            textProgressg8, textProgressg9, textProgresso3, textProgressco, textProgressno2, textProgresssso2, textProgresspm25, textProgressp10, textProgressp2,
            textProgressp3;

    private int avgAqi, g1, g2, g3, g4, g5, g6, g7, g8, g9, o3, co, no2, so2, pm25, pm10, p1, p2, p3, min = 5;

    private String title, city, deviceId, mDaily, mWeekly;

    private SQLiteDatabaseHelper dbHelper;


    private HorizontalScrollView gasesScrollBar;

    private RelativeLayout relTitleContainer, relLineChart, relBarChart, relContainer;
    private LineChartView mLineCharDaily, mLineChartWeekly;
    private BarChartView mBarChartDaily, mBarChartWeekly;
    private TextView txtDaily, txtWeekly;
    private boolean firstTime = true, weekly = true, hours = true, secondtime = true, bar = true, flag = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof MainActivity) {
            rootView = inflater.inflate(R.layout.fragment_analytics_public, container, false);
        } else if (getActivity() instanceof PrivateGlobalActivity) {
            rootView = inflater.inflate(R.layout.fragment_analytics, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_analytics_public, container, false);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.tittle_analytics);

        //initialize db
        dbHelper = new SQLiteDatabaseHelper(getActivity().getApplicationContext());

        //init datalists for analytics
        payloadDataList = new ArrayList<HourlyData>();
        donutLayouts = new ArrayList<LinearLayout>();
        donutViews = new ArrayList<CircularProgressBar>();

        //deviceLabel at the top
        globaleDeviceLabel = (TextView) rootView.findViewById(R.id.globaleDeviceLabel);
        relContainer = (RelativeLayout) rootView.findViewById(R.id.relContainerAnalytics);
        //hide scroll bar first. will be activated as per gases available
        gasesScrollBar = (HorizontalScrollView) rootView.findViewById(R.id.childScroll);
        gasesScrollBar.setVisibility(View.GONE);

        ConfigView();
        fetchDataFromDb();
        //fetchAnalyticsData(deviceId);

        // values();
        return rootView;

    }

    private void fetchDataFromDb() {
        JSONObject deviceJson;
        try {
            if (getActivity() instanceof MainActivity) {
                deviceJson = dbHelper.readLatestPublicObj();
            } else {
                deviceJson = dbHelper.readLatestPrivateObj();
            }
            title = deviceJson.getString("label");
            city = deviceJson.getString("city");
            deviceId = deviceJson.getString("deviceId");

            globaleDeviceLabel.setText(title + " , " + city);
            //Initialize first Line chart when acltivity run first time
            if (getActivity() instanceof MainActivity) {
                fetchPublicAnalyticsData(deviceId);
            } else {
                fetchAnalyticsData(deviceId);
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON exception occurred with details : " + e.toString());
            initConfigView();
        }
    }

    private PayloadData fetchPayloadData() {
        PayloadData payloadData = null;
        try {
            if (getActivity() instanceof PrivateGlobalActivity) {
                ObjectMapper mapper = new ObjectMapper();
                JSONObject json = dbHelper.readLatestPrivateObj();
                Payload payload = mapper.readValue(json.getJSONObject("payload").toString(), Payload.class);
                //String payloadStr = UserPrefrenceUtils.getValueFromUserSharedPrefs(ApiKeys.ANALYTICS_PAYLOAD_DATA, getActivity().getApplicationContext());
                //Payload payload = mapper.readValue(payloadStr, Payload.class);
                payloadData = payload.getData();
            } else {
                payloadData = null;
            }
        } catch (JsonParseException e) {
            payloadData = null;
            Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
        } catch (JsonMappingException e) {
            payloadData = null;
            Log.e(TAG, "JSON Mapping Exception occurred with details : " + e.toString());
        } catch (IOException e) {
            payloadData = null;
            Log.e(TAG, "IO Exception occurred with details : " + e.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
        }
        if (payloadData == null) {
            payloadData = payloadDataList.get(payloadDataList.size() - 1).getPayloadData().getData();
        }
        return payloadData;
    }


    // To setup donuts
    private void setUpDonuts() {
        int flag = 1;
        PayloadData payloadData = fetchPayloadData();
        //Log.e(TAG, "Payload data :" + payloadData.toString());


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


//        Log.i(TAG, "Info of all device :" + "g3:" + g3 + " g2:" + g2 + " p1:" + p1 + "" + " o3:" + o3 + " g4:" + g4 + " co:" + co + " p2" + p2 + " g1:" + g1 +
//                " g8:" + g8 + " g5:" + g5 + " pm25:" + pm25 + " pm10:" + pm10 + " Original p1" + payloadData.getP1() + " " + payloadData.getP2() + "Original O3 :" + payloadData.getO3());

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

    public String getStatus(int aqiIntVal) {
        String retStatus = "";
        if (aqiIntVal >= Constants.AQI_GOOD_LOW && aqiIntVal <= Constants.AQI_GOOD_HIGH) {
            retStatus = "Good";
        } else if (aqiIntVal >= Constants.AQI_SATISFACTORY_LOW && aqiIntVal <= Constants.AQI_SATISFACTORY_HIGH) {
            retStatus = "Satisfactory";
        } else if (aqiIntVal >= Constants.AQI_MODERATE_LOW && aqiIntVal <= Constants.AQI_MODERATE_HIGH) {
            retStatus = "Moderate";
        } else if (aqiIntVal >= Constants.AQI_POOR_LOW && aqiIntVal <= Constants.AQI_POOR_HIGH) {
            retStatus = "Poor";
        } else if (aqiIntVal >= Constants.AQI_VERY_POOR_LOW && aqiIntVal <= Constants.AQI_VERY_POOR_HIGH) {
            retStatus = "Very Poor";
        } else if (aqiIntVal >= Constants.AQI_SEVERE_LOW && aqiIntVal <= Constants.AQI_SEVERE_HIGH) {
            retStatus = "Severe";
        } else {
            retStatus = "Good";
        }
        return retStatus;
    }

    private void ConfigView() {
        //Btn for changing view
        txtBarChart = (TextView) rootView.findViewById(R.id.txtBarView);
        txtLineChart = (TextView) rootView.findViewById(R.id.txtLineView);
        txtAqiReading = (TextView) rootView.findViewById(R.id.txtAqiReading);

        relLineChart = (RelativeLayout) rootView.findViewById(R.id.relChartView);
        relBarChart = (RelativeLayout) rootView.findViewById(R.id.relChartBarView);

        //btn for changing graph type
        txtDaily = (TextView) rootView.findViewById(R.id.txtDaily);
        txtWeekly = (TextView) rootView.findViewById(R.id.txtWeekly);

        //LineChart define
        mLineCharDaily = (LineChartView) rootView.findViewById(R.id.chart1);
        mLineChartWeekly = (LineChartView) rootView.findViewById(R.id.chart2);
        mLineChartWeekly.setVisibility(View.INVISIBLE);

        //BarChart define
        mBarChartDaily = (BarChartView) rootView.findViewById(R.id.chart3);
        mBarChartWeekly = (BarChartView) rootView.findViewById(R.id.chart4);
        mBarChartWeekly.setVisibility(View.INVISIBLE);

        //OnClick events
        if (getActivity() instanceof MainActivity) {
            txtDaily.setBackgroundResource(R.color.green); //72c16d
        } else {
            txtDaily.setBackgroundResource(R.color.blue);
        }
        txtDaily.setTextColor(Color.WHITE);

        //TO switch between daily to monthly
        txtDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAqiReading.setText(mDaily);
                if (hours == false) {
                    hours = true;
                    mBarChartDaily.setVisibility(View.VISIBLE);

                    if (getActivity() instanceof MainActivity) {
                        txtDaily.setBackgroundResource(R.color.green); //72c16d
                        txtWeekly.setTextColor(Color.parseColor("#72c16d"));
                    } else {
                        txtDaily.setBackgroundResource(R.color.blue);
                        txtWeekly.setTextColor(Color.parseColor("#00b3bf"));
                    }
                    txtDaily.setTextColor(Color.WHITE);
                    txtWeekly.setBackground(null);
                    mLineChartWeekly.setVisibility(View.INVISIBLE);
                    mBarChartWeekly.setVisibility(View.INVISIBLE);

                    weekly = false;
                    flag = false;


                    if (getActivity() instanceof MainActivity) {
                        //  mLineCharDaily.reset();
                        //  fetchPublicAnalyticsData(deviceId);
                        mLineCharDaily.setVisibility(View.VISIBLE);
                    } else {
                        // TODO: 14-12-2016 uncomment
                        //  fetchAnalyticsData(deviceId);
                        mLineCharDaily.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        txtWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hours) {
                    hours = false;
                    if (getActivity() instanceof MainActivity) {
                        txtWeekly.setBackgroundResource(R.color.green);
                        txtDaily.setTextColor(Color.parseColor("#72c16d"));
                    } else {
                        txtWeekly.setBackgroundResource(R.color.blue);
                        txtDaily.setTextColor(Color.parseColor("#00b3bf"));
                    }
                    txtWeekly.setTextColor(Color.WHITE);

                    txtDaily.setBackground(null);

                    mLineChartWeekly.setVisibility(View.VISIBLE);
                    mBarChartWeekly.setVisibility(View.VISIBLE);
                    mLineCharDaily.setVisibility(View.INVISIBLE);
                    mBarChartDaily.setVisibility(View.INVISIBLE);
                    mBarChartWeekly.reset();


                    if (getActivity() instanceof MainActivity) {
                        //fetchPublicAnalyticsData(deviceId);
                        fetchPublicAnalyticsWeeklyBar(deviceId);
                    } else {
                        //If weekly analytics is not load then it will be load else it will be not load next time
                        if (weekly) {
                            fetchAnalyticsDataWeeklyBar(deviceId);
                        } else {
                            // You can also use reset for this  mChart.reset();
                            fetchAnalyticsDataWeeklyBar(deviceId);
                            mBarChartWeekly.setVisibility(View.VISIBLE);
                        }
                    }


                    if (getActivity() instanceof MainActivity) {
                        //fetchPublicAnalyticsData(deviceId);
                        fetchPublicAnalyticsWeekly(deviceId);
                    } else {
                        //If weekly analytics is not load then it will be load else it will be not load next time
                        if (weekly) {
                            fetchAnalyticsDataWeekly(deviceId);
                        } else {
                            // You can also use reset for this  mChart.reset();
                            fetchAnalyticsDataWeekly(deviceId);
                            mLineChartWeekly.setVisibility(View.VISIBLE);
                        }
                    }
                }


            }
        });

        //To hide and change view of chart
        txtBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if boolean is true then go inside other wise skip this
                if (firstTime && hours) {
                    if (getActivity() instanceof MainActivity) {
                        fetchPublicAnalyticsDataBar(deviceId);
                    } else {
                        if (flag) {
                            fetchAnalyticsDataBar(deviceId);
                        } else {
                            fetchPublicAnalyticsDataBar(deviceId);
                            mBarChartDaily.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (secondtime && hours == false) {
                    if (getActivity() instanceof MainActivity) {
                        fetchAnalyticsDataWeeklyBar(deviceId);
                    } else {
                        if (flag) {
                            fetchPublicAnalyticsWeeklyBar(deviceId);
                        } else {
                            fetchPublicAnalyticsWeeklyBar(deviceId);
                            mBarChartWeekly.setVisibility(View.VISIBLE);
                        }
                    }
                }
                txtBarChart.setVisibility(View.GONE);
                txtLineChart.setVisibility(View.VISIBLE);
                relLineChart.setVisibility(View.GONE);
                relBarChart.setVisibility(View.VISIBLE);
            }
        });

        txtLineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtLineChart.setVisibility(View.GONE);
                txtBarChart.setVisibility(View.VISIBLE);
                relBarChart.setVisibility(View.GONE);
                relLineChart.setVisibility(View.VISIBLE);
                firstTime = false;
                secondtime = false;
                flag = false;
            }
        });
        //deviceLabel at the top
        globaleDeviceLabel = (TextView) rootView.findViewById(R.id.globaleDeviceLabel);

        //hide scroll bar first. will be activated as per gases available
        gasesScrollBar = (HorizontalScrollView) rootView.findViewById(R.id.childScroll);
        gasesScrollBar.setVisibility(View.GONE);
    }


    //Hours

    private void initConfigView() {
        txtAqiReading = (TextView) rootView.findViewById(R.id.txtAqiReading);
        txtStatus = (TextView) rootView.findViewById(R.id.txtStatus);
        relTitleContainer = (RelativeLayout) rootView.findViewById(R.id.relTitleContainer);
        setBebasNeue();
        relTitleContainer.setVisibility(View.VISIBLE);
        if (aqiValuesList != null && aqiValuesList.size() > 0) {
            Collections.reverse(aqiValuesList);
            Collections.reverse(timeValuesList);
            aqiValues = new float[1][aqiValuesList.size()];

            timeValues = new String[timeValuesList.size()];
            Log.i(TAG, "Analytics " + aqiValues.toString() + timeValues.toString());
            for (int i = 0; i < timeValuesList.size(); i++) {
                timeValues[i] = "";
                aqiValues[0][i] = aqiValuesList.get(i);
            }
            int middleLength = timeValuesList.size() / 2;
            timeValues[middleLength] = "Last 24 hours Analytics data";

            if (getActivity() instanceof MainActivity) {
                (new LineCardOne((RelativeLayout) rootView.findViewById(R.id.relChartView), getContext(), timeValues, aqiValues, true)).init();
            } else {
                (new LineCardOne((RelativeLayout) rootView.findViewById(R.id.relChartView), getContext(), timeValues, aqiValues, false)).init();
                //  (new BarCardOne((RelativeLayout) rootView.findViewById(R.id.relBarChartView), getContext())).init();
            }


            txtAqiReading.setText(String.valueOf(avgAqi));
            mDaily = String.valueOf(avgAqi);
            txtStatus.setText(R.string.avg);


        } else {
            txtAqiReading.setText("No");
            txtStatus.setText("analytics available!");
            Toast.makeText(getActivity(), "No Analytics data available for selected device.", Toast.LENGTH_SHORT).show();

        }

    }

    //Weekly
    private void initConfigViewWeekly() {
        txtAqiReading = (TextView) rootView.findViewById(R.id.txtAqiReading);
        txtStatus = (TextView) rootView.findViewById(R.id.txtStatus);
        relTitleContainer = (RelativeLayout) rootView.findViewById(R.id.relTitleContainer);
        setBebasNeue();
        relTitleContainer.setVisibility(View.VISIBLE);
        if (aqiValuesList != null && aqiValuesList.size() > 0) {
            Collections.reverse(aqiValuesList);
            Collections.reverse(timeValuesList);
            aqiValues = new float[1][aqiValuesList.size()];

            timeValues = new String[timeValuesList.size()];
            Log.i(TAG, "Analytics " + aqiValues.toString() + timeValues.toString());
            for (int i = 0; i < timeValuesList.size(); i++) {
                timeValues[i] = "";
                aqiValues[0][i] = aqiValuesList.get(i);
            }
            int middleLength = timeValuesList.size() / 2;
            timeValues[middleLength] = "Last 7 days data";


            if (getActivity() instanceof MainActivity) {
                (new LineCardTwo((RelativeLayout) rootView.findViewById(R.id.relChartView), getContext(), timeValues, aqiValues, true)).init();
            } else {
                (new LineCardTwo((RelativeLayout) rootView.findViewById(R.id.relChartView), getContext(), timeValues, aqiValues, false)).init();
                //  (new BarCardOne((RelativeLayout) rootView.findViewById(R.id.relBarChartView), getContext())).init();
            }

            txtAqiReading.setText(String.valueOf(avgAqi));
            txtStatus.setText(R.string.avg);


        } else {
            txtAqiReading.setText("No");
            txtStatus.setText("analytics available!");
            Toast.makeText(getActivity(), "No Analytics data available for selected device.", Toast.LENGTH_SHORT).show();
//            if (getActivity() instanceof MainActivity) {
//                ((MainActivity) getActivity()).switchViewPager(0);
//            } else if (getActivity() instanceof PrivateGlobalActivity) {
//                ((PrivateGlobalActivity) getActivity()).swichViewPager(0);
//            } else {
//
//            }
        }

    }

    //HoursBar
    private void initConfigViewBar() {
        txtAqiReading = (TextView) rootView.findViewById(R.id.txtAqiReading);
        txtStatus = (TextView) rootView.findViewById(R.id.txtStatus);
        relTitleContainer = (RelativeLayout) rootView.findViewById(R.id.relTitleContainer);
        setBebasNeue();
        relTitleContainer.setVisibility(View.VISIBLE);
        if (aqiValuesList != null && aqiValuesList.size() > 0) {
            Collections.reverse(aqiValuesList);
            Collections.reverse(timeValuesList);
            aqiValues = new float[1][aqiValuesList.size()];

            timeValues = new String[timeValuesList.size()];
            Log.i(TAG, "Analytics " + aqiValues.toString() + timeValues.toString());
            for (int i = 0; i < timeValuesList.size(); i++) {
                timeValues[i] = "";
                aqiValues[0][i] = aqiValuesList.get(i);
            }
            int middleLength = timeValuesList.size() / 2;
            timeValues[middleLength] = "Last 24 hours Analytics data";

            if (getActivity() instanceof MainActivity) {
                (new BarCardOne((RelativeLayout) rootView.findViewById(R.id.relChartBarView), getContext(), timeValues, aqiValues, true)).init();
            } else {
                (new BarCardOne((RelativeLayout) rootView.findViewById(R.id.relChartBarView), getContext(), timeValues, aqiValues, false)).init();
            }


            txtAqiReading.setText(String.valueOf(avgAqi));
            mDaily = String.valueOf(avgAqi);
            txtStatus.setText(R.string.avg);


        } else {
            txtAqiReading.setText("No");
            txtStatus.setText("analytics available!");
            Toast.makeText(getActivity(), "No Analytics data available for selected device.", Toast.LENGTH_SHORT).show();
//            if (getActivity() instanceof MainActivity) {
//                ((MainActivity) getActivity()).switchViewPager(0);
//            } else if (getActivity() instanceof PrivateGlobalActivity) {
//                ((PrivateGlobalActivity) getActivity()).swichViewPager(0);
//            }
//            else{
//
//            }
        }

    }

    //WeeklyBar
    private void initConfigViewWeeklyBar() {
        txtAqiReading = (TextView) rootView.findViewById(R.id.txtAqiReading);
        txtStatus = (TextView) rootView.findViewById(R.id.txtStatus);
        relTitleContainer = (RelativeLayout) rootView.findViewById(R.id.relTitleContainer);
        setBebasNeue();
        relTitleContainer.setVisibility(View.VISIBLE);
        if (aqiValuesList != null && aqiValuesList.size() > 0) {
            Collections.reverse(aqiValuesList);
            Collections.reverse(timeValuesList);
            aqiValues = new float[1][aqiValuesList.size()];

            timeValues = new String[timeValuesList.size()];
            Log.i(TAG, "Analytics " + aqiValues.toString() + timeValues.toString());
            for (int i = 0; i < timeValuesList.size(); i++) {
                timeValues[i] = "";
                aqiValues[0][i] = aqiValuesList.get(i);
            }
            int middleLength = timeValuesList.size() / 2;
            timeValues[middleLength] = "Last 7 days data";


            if (getActivity() instanceof MainActivity) {
                (new BarCardTwo((RelativeLayout) rootView.findViewById(R.id.relChartBarView), getContext(), timeValues, aqiValues, true)).init();
            } else {
                (new BarCardTwo((RelativeLayout) rootView.findViewById(R.id.relChartBarView), getContext(), timeValues, aqiValues, false)).init();
            }

            txtAqiReading.setText(String.valueOf(avgAqi));
            mWeekly = String.valueOf(avgAqi);
            txtStatus.setText(R.string.avg);


        } else {
            txtAqiReading.setText("No");
            txtStatus.setText("analytics available!");
            Toast.makeText(getActivity(), "No Analytics data available for selected device.", Toast.LENGTH_SHORT).show();
        }

    }

    private void setBebasNeue() {
        Typeface font = Typeface.createFromAsset(
                getContext().getAssets(),
                "fonts/BebasNeue.ttf");
        txtAqiReading.setTypeface(font);
    }

    // Analytics for Public Weekly data
    private void fetchPublicAnalyticsWeekly(String deviceId) {
        if (deviceId != null && !(deviceId.isEmpty())) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
            ECAPIHelper.fetchPublicAnalyticsWeekly(deviceId, getActivity().getApplicationContext(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            Log.i(TAG, "Weekly data : " + response.toString());
                            payloadDataList = new ArrayList<HourlyData>();
                            int lengthResponse = response.length();
                            if (lengthResponse > 0) {
                                for (int i = 0; i < lengthResponse; i++) {
                                    HourlyData payloadData = mapper.readValue(response.get(i).toString(), HourlyData.class);
                                    payloadDataList.add(payloadData);

                                    loading.dismiss();
                                    relContainer.setVisibility(View.VISIBLE);
                                }
                                getLabelnValueFromListWeekly();
                            } else {
                                loading.dismiss();
                                relContainer.setVisibility(View.VISIBLE);
                                Log.d(TAG, "response of analytics is zero");
                                initConfigViewWeekly();
                            }
                        } else {
                            loading.dismiss();
                            relContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "response of analytics is null");
                            initConfigViewWeekly();
                        }
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                        initConfigViewWeekly();
                    } catch (JSONException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                        initConfigViewWeekly();
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Mapping Exceptoin occurred with details : " + e.toString());
                        initConfigViewWeekly();
                    } catch (IOException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                        initConfigViewWeekly();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    relContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Volley error occurred with details : " + error.toString());
                    initConfigViewWeekly();
                }
            });
        }
    }

    //Analytics for public data
    private void fetchPublicAnalyticsData(String deviceId) {
        if (deviceId != null && !(deviceId.isEmpty())) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
            ECAPIHelper.fetchPublicAnalytics(deviceId, getActivity().getApplicationContext(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response != null) {
                            Log.i(TAG, "Hours Analytics data " + response.toString());
                            ObjectMapper mapper = new ObjectMapper();
                            int lengthResponse = response.length();
                            if (lengthResponse > 0) {
                                for (int i = 0; i < lengthResponse; i++) {
                                    HourlyData payloadData = mapper.readValue(response.get(i).toString(), HourlyData.class);
                                    payloadDataList.add(payloadData);

                                    loading.dismiss();
                                    relContainer.setVisibility(View.VISIBLE);
                                }
                                getLabelnValueFromList();
                            } else {
                                loading.dismiss();
                                relContainer.setVisibility(View.VISIBLE);
                                Log.d(TAG, "response of analytics is zero");
                                initConfigView();
                            }
                        } else {
                            loading.dismiss();
                            relContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "response of analytics is null");
                            initConfigView();
                        }
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                        initConfigView();
                    } catch (JSONException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                        initConfigView();
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Mapping Exceptoin occurred with details : " + e.toString());
                        initConfigView();
                    } catch (IOException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                        initConfigView();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    relContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Volley error occurred with details : " + error.toString());
                    initConfigView();
                }
            });
        }
    }

    //Analytics for private data
    private void fetchAnalyticsData(String deviceId) {
        if (deviceId != null && !(deviceId.isEmpty())) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
            ECAPIHelper.fetchHourlyAnalytics(deviceId, getActivity().getApplicationContext(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            int lengthResponse = response.length();
                            if (lengthResponse > 0) {
                                payloadDataList.removeAll(payloadDataList);
                                for (int i = 0; i < lengthResponse; i++) {
                                    HourlyData payloadData = mapper.readValue(response.get(i).toString(), HourlyData.class);
                                    payloadDataList.add(payloadData);
                                    loading.dismiss();
                                    relContainer.setVisibility(View.VISIBLE);
                                }
                                getLabelnValueFromList();
                            } else {
                                loading.dismiss();
                                relContainer.setVisibility(View.VISIBLE);
                                Log.d(TAG, "response of analytics is zero");
                                initConfigView();
                            }
                        } else {
                            loading.dismiss();
                            relContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "response of analytics is null");
                            initConfigView();
                        }
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                        initConfigView();
                    } catch (JSONException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                        initConfigView();
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Mapping Exceptoin occurred with details : " + e.toString());
                        initConfigView();
                    } catch (IOException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                        initConfigView();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    relContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Volley error occurred with details : " + error.toString());
                    initConfigView();
                }
            });
        }
    }

    // for private weekly analytics
    private void fetchAnalyticsDataWeekly(String deviceId) {
        if (deviceId != null && !(deviceId.isEmpty())) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
            ECAPIHelper.fetchWeeklyAnalytics(deviceId, getActivity().getApplicationContext(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response != null) {
                            Log.i(TAG, "Weekly private data" + response.toString());
                            ObjectMapper mapper = new ObjectMapper();
                            int lengthResponse = response.length();
                            //  payloadDataList.removeAll(payloadDataList);
                            //  payloadDataList = new ArrayList<HourlyData>();
                            if (lengthResponse > 0) {
                                payloadDataList.removeAll(payloadDataList);
                                for (int i = 0; i < lengthResponse; i++) {
                                    HourlyData payloadData = mapper.readValue(response.get(i).toString(), HourlyData.class);
                                    payloadDataList.add(payloadData);
                                    loading.dismiss();
                                    relContainer.setVisibility(View.VISIBLE);
                                }
                                getLabelnValueFromListWeekly();
                            } else {
                                loading.dismiss();
                                relContainer.setVisibility(View.VISIBLE);
                                Log.d(TAG, "response of analytics is zero");
                                initConfigViewWeekly();
                            }
                        } else {
                            loading.dismiss();
                            relContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "response of analytics is null");
                            initConfigViewWeekly();
                        }
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                        initConfigViewWeekly();
                    } catch (JSONException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                        initConfigViewWeekly();
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Mapping Exceptoin occurred with details : " + e.toString());
                        initConfigViewWeekly();
                    } catch (IOException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                        initConfigViewWeekly();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    relContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Volley error occurred with details : " + error.toString());
                    initConfigViewWeekly();
                }
            });
        }
    }

    //Weekly
    private void getLabelnValueFromListWeekly() {
        if (payloadDataList != null) {
            aqiValuesList = new ArrayList<Float>();
            timeValuesList = new ArrayList<String>();
            if (payloadDataList.size() > 2) {
                for (int j = 0; j < payloadDataList.size(); j++) {
                    aqiValuesList.add((float) payloadDataList.get(j).getAqi());
                    String epochTime = payloadDataList.get(j).getPayloadData().getData().getTime();
                    if (epochTime != null && !(epochTime.isEmpty())) {
                        Date date = new Date(Long.valueOf(epochTime) * 1000);
                        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");
                        String fullTime = dateFormat.format(date);
                        String[] strArray = fullTime.split(" ");
                        String[] timeArray = strArray[1].split(":");
                        String _tmp = timeArray[0];
                        timeValuesList.add(_tmp);
                    }
                }
                //timeValues = clean(timeValues);
                avgAqi = calculateAverageWeekly(aqiValuesList);
                initConfigViewWeekly();
                setUpDonuts();
            } else {
                initConfigViewWeekly();
            }
        } else {
            initConfigViewWeekly();
        }
    }

    //Hours
    private void getLabelnValueFromList() {
        if (payloadDataList != null) {
            aqiValuesList = new ArrayList<Float>();
            timeValuesList = new ArrayList<String>();
            if (payloadDataList.size() > 2) {
                for (int j = 0; j < payloadDataList.size(); j++) {
                    aqiValuesList.add((float) payloadDataList.get(j).getAqi());
                    String epochTime = payloadDataList.get(j).getPayloadData().getData().getTime();
                    if (epochTime != null && !(epochTime.isEmpty())) {
                        Date date = new Date(Long.valueOf(epochTime) * 1000);
                        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");
                        String fullTime = dateFormat.format(date);
                        String[] strArray = fullTime.split(" ");
                        String[] timeArray = strArray[1].split(":");
                        String _tmp = timeArray[0];
                        timeValuesList.add(_tmp);
                    }
                }
                //timeValues = clean(timeValues);
                avgAqi = calculateAverage(aqiValuesList);
                initConfigView();
                setUpDonuts();
            } else {
                initConfigView();
            }
        } else {
            initConfigView();
        }
    }


    private int calculateAverageWeekly(List<Float> marks) {
        float sum = 0;
        if (!marks.isEmpty()) {
            for (Float mark : marks) {
                sum += mark;
            }
            return (int) sum / marks.size();
        }
        return (int) sum;
    }

    private int calculateAverage(List<Float> marks) {
        float sum = 0;
        if (!marks.isEmpty()) {
            for (Float mark : marks) {
                sum += mark;
            }
            return (int) sum / marks.size();
        }
        return (int) sum;
    }

    //For bar chart

    // Analytics for Public Weekly data
    private void fetchPublicAnalyticsWeeklyBar(String deviceId) {
        if (deviceId != null && !(deviceId.isEmpty())) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
            ECAPIHelper.fetchPublicAnalyticsWeekly(deviceId, getActivity().getApplicationContext(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            Log.i(TAG, "Weekly data : " + response.toString());
                            payloadDataList = new ArrayList<HourlyData>();
                            int lengthResponse = response.length();
                            if (lengthResponse > 0) {
                                for (int i = 0; i < lengthResponse; i++) {
                                    HourlyData payloadData = mapper.readValue(response.get(i).toString(), HourlyData.class);
                                    payloadDataList.add(payloadData);

                                    loading.dismiss();
                                    relContainer.setVisibility(View.VISIBLE);
                                }
                                getLabelnValueFromListWeeklyBar();
                            } else {
                                loading.dismiss();
                                relContainer.setVisibility(View.VISIBLE);
                                Log.d(TAG, "response of analytics is zero");
                                initConfigViewWeeklyBar();
                            }
                        } else {
                            loading.dismiss();
                            relContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "response of analytics is null");
                            initConfigViewWeeklyBar();
                        }
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                        initConfigViewWeeklyBar();
                    } catch (JSONException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                        initConfigViewWeeklyBar();
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Mapping Exceptoin occurred with details : " + e.toString());
                        initConfigViewWeeklyBar();
                    } catch (IOException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                        initConfigViewWeeklyBar();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    relContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Volley error occurred with details : " + error.toString());
                    initConfigViewWeeklyBar();
                }
            });
        }
    }

    //Analytics for public data hours
    private void fetchPublicAnalyticsDataBar(String deviceId) {
        if (deviceId != null && !(deviceId.isEmpty())) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
            ECAPIHelper.fetchPublicAnalytics(deviceId, getActivity().getApplicationContext(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response != null) {
                            Log.i(TAG, "Hours Analytics data " + response.toString());
                            ObjectMapper mapper = new ObjectMapper();
                            int lengthResponse = response.length();
                            if (lengthResponse > 0) {
                                for (int i = 0; i < lengthResponse; i++) {
                                    HourlyData payloadData = mapper.readValue(response.get(i).toString(), HourlyData.class);
                                    payloadDataList.add(payloadData);

                                    loading.dismiss();
                                    relContainer.setVisibility(View.VISIBLE);
                                }
                                getLabelnValueFromListBar();
                            } else {
                                loading.dismiss();
                                relContainer.setVisibility(View.VISIBLE);
                                Log.d(TAG, "response of analytics is zero");
                                initConfigViewBar();
                            }
                        } else {
                            loading.dismiss();
                            relContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "response of analytics is null");
                            initConfigViewBar();
                        }
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                        initConfigViewBar();
                    } catch (JSONException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                        initConfigViewBar();
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Mapping Exceptoin occurred with details : " + e.toString());
                        initConfigViewBar();
                    } catch (IOException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                        initConfigViewBar();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    relContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Volley error occurred with details : " + error.toString());
                    initConfigViewBar();
                }
            });
        }
    }

    //Analytics for private data hours
    private void fetchAnalyticsDataBar(String deviceId) {
        if (deviceId != null && !(deviceId.isEmpty())) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
            ECAPIHelper.fetchHourlyAnalytics(deviceId, getActivity().getApplicationContext(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            int lengthResponse = response.length();
                            if (lengthResponse > 0) {
                                payloadDataList.removeAll(payloadDataList);
                                for (int i = 0; i < lengthResponse; i++) {
                                    HourlyData payloadData = mapper.readValue(response.get(i).toString(), HourlyData.class);
                                    payloadDataList.add(payloadData);
                                    loading.dismiss();
                                    relContainer.setVisibility(View.VISIBLE);
                                }
                                getLabelnValueFromListBar();
                            } else {
                                loading.dismiss();
                                relContainer.setVisibility(View.VISIBLE);
                                Log.d(TAG, "response of analytics is zero");
                                initConfigViewBar();
                            }
                        } else {
                            loading.dismiss();
                            relContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "response of analytics is null");
                            initConfigViewBar();
                        }
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                        initConfigViewBar();
                    } catch (JSONException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                        initConfigView();
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Mapping Exceptoin occurred with details : " + e.toString());
                        initConfigViewBar();
                    } catch (IOException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                        initConfigViewBar();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    relContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Volley error occurred with details : " + error.toString());
                    initConfigViewBar();
                }
            });
        }
    }

    // for private weekly analytics
    private void fetchAnalyticsDataWeeklyBar(String deviceId) {
        if (deviceId != null && !(deviceId.isEmpty())) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data...", "Please wait...", false, false);
            ECAPIHelper.fetchWeeklyAnalytics(deviceId, getActivity().getApplicationContext(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response != null) {
                            Log.i(TAG, "Weekly private data" + response.toString());
                            ObjectMapper mapper = new ObjectMapper();
                            int lengthResponse = response.length();
                            //  payloadDataList.removeAll(payloadDataList);
                            //  payloadDataList = new ArrayList<HourlyData>();
                            if (lengthResponse > 0) {
                                payloadDataList.removeAll(payloadDataList);
                                for (int i = 0; i < lengthResponse; i++) {
                                    HourlyData payloadData = mapper.readValue(response.get(i).toString(), HourlyData.class);
                                    payloadDataList.add(payloadData);
                                    loading.dismiss();
                                    relContainer.setVisibility(View.VISIBLE);
                                }
                                getLabelnValueFromListWeeklyBar();
                            } else {
                                loading.dismiss();
                                relContainer.setVisibility(View.VISIBLE);
                                Log.d(TAG, "response of analytics is zero");
                                initConfigViewWeeklyBar();
                            }
                        } else {
                            loading.dismiss();
                            relContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "response of analytics is null");
                            initConfigViewWeeklyBar();
                        }
                    } catch (JsonParseException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Parse Exception occurred with details : " + e.toString());
                        initConfigViewWeeklyBar();
                    } catch (JSONException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Exception occurred with details : " + e.toString());
                        initConfigViewWeeklyBar();
                    } catch (JsonMappingException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Mapping Exceptoin occurred with details : " + e.toString());
                        initConfigViewWeeklyBar();
                    } catch (IOException e) {
                        loading.dismiss();
                        relContainer.setVisibility(View.VISIBLE);
                        Log.e(TAG, "IO Exception occurred with details : " + e.toString());
                        initConfigViewWeeklyBar();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    //relContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Volley error occurred with details : " + error.toString());
                    initConfigViewWeeklyBar();
                }
            });
        }
    }

    //Weekly
    private void getLabelnValueFromListWeeklyBar() {
        if (payloadDataList != null) {
            aqiValuesList = new ArrayList<Float>();
            timeValuesList = new ArrayList<String>();
            if (payloadDataList.size() > 2) {
                for (int j = 0; j < payloadDataList.size(); j++) {
                    aqiValuesList.add((float) payloadDataList.get(j).getAqi());
                    String epochTime = payloadDataList.get(j).getPayloadData().getData().getTime();
                    if (epochTime != null && !(epochTime.isEmpty())) {
                        Date date = new Date(Long.valueOf(epochTime) * 1000);
                        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");
                        String fullTime = dateFormat.format(date);
                        String[] strArray = fullTime.split(" ");
                        String[] timeArray = strArray[1].split(":");
                        String _tmp = timeArray[0];
                        timeValuesList.add(_tmp);
                    }
                }
                //timeValues = clean(timeValues);
                avgAqi = calculateAverageWeekly(aqiValuesList);
                initConfigViewWeeklyBar();
                setUpDonuts();
            } else {
                initConfigViewWeeklyBar();
            }
        } else {
            initConfigViewWeeklyBar();
        }
    }

    //Hours
    private void getLabelnValueFromListBar() {
        if (payloadDataList != null) {
            aqiValuesList = new ArrayList<Float>();
            timeValuesList = new ArrayList<String>();
            if (payloadDataList.size() > 2) {
                for (int j = 0; j < payloadDataList.size(); j++) {
                    aqiValuesList.add((float) payloadDataList.get(j).getAqi());
                    String epochTime = payloadDataList.get(j).getPayloadData().getData().getTime();
                    if (epochTime != null && !(epochTime.isEmpty())) {
                        Date date = new Date(Long.valueOf(epochTime) * 1000);
                        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");
                        String fullTime = dateFormat.format(date);
                        String[] strArray = fullTime.split(" ");
                        String[] timeArray = strArray[1].split(":");
                        String _tmp = timeArray[0];
                        timeValuesList.add(_tmp);
                    }
                }
                //timeValues = clean(timeValues);
                avgAqi = calculateAverage(aqiValuesList);
                initConfigViewBar();
                setUpDonuts();
            } else {
                initConfigViewBar();
            }
        } else {
            initConfigViewBar();
        }
    }


}