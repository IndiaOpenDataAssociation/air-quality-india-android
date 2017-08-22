package com.idn0phl3108ed43d22s30.network;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idn0phl3108ed43d22s30.etc.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class ECNetworkingManager {
    private static String TAG = "ECNetworkingManager";
    private static ECNetworkingManager instance;
    private RequestQueue mRequestQueue;
    private static String kCirclePCloudURL = Constants.serverUrl;
    private static String publicCloudURL = Constants.PUBLIC_SERVER_DOMAIN;
    private Context mContext;
    private ImageLoader mImageLoader;
    private Timer mTimer;
    private ECNetworkRequest lastRequest;

    private static final int PUBLIC_URL = 0;
    private static final int PRIVATE_URL = 1;

    private ECNetworkingManager(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static ECNetworkingManager getInstance(Context context) {
        if (instance == null) {
            instance = new ECNetworkingManager(context);
        }

        return instance;
    }

    public String publicConstructURL(String url){
        return publicCloudURL+url;
    }

    public ImageLoader getImageLoader() {
        return this.mImageLoader;
    }

    public String constructURL(String url) {
        return kCirclePCloudURL + url;
    }

    public void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public void addRequestToQueue(StringRequest request) {
        mRequestQueue.add(request);
    }

    public void post(String method, JSONObject jsonRequest,
                     Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        String url = constructURL(method);
        JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put(Constants.AIR_QUALITY_INDIA_APP_HEADER, Constants.HEADER_VAL);
                return params;
            }
        };

        //TODO
        int socketTimeout = 1000000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objRequest.setRetryPolicy(policy);

        mRequestQueue.add(objRequest);
    }




    // TODO: 27-08-2016 POST request..
    public void postJsonArray(String method, JSONObject jsonArray,
                              Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = constructURL(method);
        JsonArrayRequest objRequest = new JsonArrayRequest(Request.Method.POST, url, jsonArray, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put(Constants.AIR_QUALITY_INDIA_APP_HEADER, Constants.HEADER_VAL);
                return params;
            }
        };

        //TODO
        int socketTimeout = 1000000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objRequest.setRetryPolicy(policy);

        mRequestQueue.add(objRequest);
    }

    public void put(String method, JSONObject jsonRequest,
                    Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        String url = constructURL(method);
        JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonRequest, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put(Constants.AIR_QUALITY_INDIA_APP_HEADER, Constants.HEADER_VAL);
                return params;
            }
        };

        //TODO
        int socketTimeout = 1000000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objRequest.setRetryPolicy(policy);

        mRequestQueue.add(objRequest);
    }

    /*
    1. First check if its new request, then cancel retry of last request and add last request to pendingRequests
    2. If there any Pending requests add to params
    3. Network Call
        On Success --> Clear Pending requests
        On Failure --> Is News Request --> Set the request for retry
                   --> If Already retrying --> If RetryAttempts > 0 --> Do nothing
                                            --> Else Add to pending requests
    */
    public void post(final String method, final JSONObject jsonRequest, final int retryAttempts,
                     final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        Log.d(TAG, "Post with retry attempts : " + retryAttempts);
        final boolean isRetrying = (lastRequest != null && lastRequest.getJSONObject() == jsonRequest);
        final ECFailedRequestsQueue requestsQueue = ECFailedRequestsQueue.getInstance(mContext);

        //News Reques
        if (!isRetrying) {
            Log.d(TAG, "News request");
            Log.d(TAG, "Checking if retrying for last failed request is going on");

            //If retrying for last failed request
            if (lastRequest != null) {
                Log.d(TAG, "Retrying for last failed request is ongoing");
                Log.d(TAG, "Cancel retrying for last failed request");

                //Add last failed request to pending requests
                requestsQueue.addRequestToQueue(lastRequest.getJSONObject());

                //Cancel retrival of last failed request
                cancelLastRequest();
            } else {
                Log.d(TAG, "Retrying for last failed request is not going on");
            }
        }

        //If there are pending requests add those to params
        Log.d(TAG, "Checking for pending requests");
        if (requestsQueue.isRequestPending()) {
            try {
                ArrayList<JSONObject> pendingRequests = requestsQueue.getPendingOperations();
                Log.d(TAG, pendingRequests.size() + " Requests are pending");

                //Add Pending Requests to Request Params
                jsonRequest.put("missedNotifications", pendingRequests);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException occurred with detail: " + e.toString());
            }
        } else {
            Log.d(TAG, "No Pending Requests");
        }

        //Success Listner
        Response.Listener<JSONObject> successListner = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG, "Post success");

                //Success --> Clear pending requests
                if (isRetrying) {
                    cancelLastRequest();
                }

                //If Any Pending Requests Sent
                if (jsonRequest.has("missedNotifications")) {
                    //Clear Pending Requests
                    requestsQueue.clearQueue();
                }

                listener.onResponse(jsonObject);
            }
        };

        //Error Listner
        Response.ErrorListener responseErrorListner = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "Post failure");

                ////If Retry Attempt > 0
                Log.d(TAG, "Checking whether to retry or not");
                if (retryAttempts > 0) {
                    Log.d(TAG, "retry attempts : " + retryAttempts);

                    //If this request is already retrying
                    if (lastRequest == null) {
                        Log.d(TAG, "News request, Schedule retry.");
                        ECNetworkRequest request = new ECNetworkRequest(method, jsonRequest, retryAttempts, listener, errorListener);
                        scheduleRetryTimer(request);
                    } else {
                        //Already retrying request
                        Log.d(TAG, "Already retrying for request");
                    }
                } else {
                    //Retry attempt is less than zero --> add to Pending Requests
                    Log.d(TAG, "No Retry attempt pending");
                    Log.d(TAG, "Add request to pending Requests");

                    requestsQueue.addRequestToQueue(lastRequest.getJSONObject());
                    cancelLastRequest();
                    errorListener.onErrorResponse(null);
                    return;
                }
            }
        };


        String url = constructURL(method);
        JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest, successListner, responseErrorListner) {
            @Override
            public Map getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.AIR_QUALITY_INDIA_APP_HEADER, Constants.HEADER_VAL);
                return params;
            }
        };

        //TODO
        int socketTimeout = 1000000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objRequest.setRetryPolicy(policy);

        //Post to server
        mRequestQueue.add(objRequest);
    }

    private void uploadImage(
            Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        //Showing the progress dialog

        String url = constructURL("/upload");
        JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.POST, url, listener, errorListener) {


            @Override
            public Map getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", "client_credentials");
                return params;
            }
        };

        int socketTimeout = 1000000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objRequest.setRetryPolicy(policy);

        mRequestQueue.add(objRequest);
    }

    public void get(String method,
                    Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        String url = constructURL(method);
        JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.GET, url, listener, errorListener) {
            @Override
            public Map getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.AIR_QUALITY_INDIA_APP_HEADER, Constants.HEADER_VAL);
                return params;
            }
        };

        //TODO
        int socketTimeout = 1000000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objRequest.setRetryPolicy(policy);

        mRequestQueue.add(objRequest);
    }

    public void getJsonArray(String method,
                             Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {

        String url = constructURL(method);
        JsonArrayRequest objRequest = new JsonArrayRequest(Request.Method.GET, url, listener, errorListener) {
            @Override
            public Map getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.AIR_QUALITY_INDIA_APP_HEADER, Constants.HEADER_VAL);
                return params;
            }
        };

        //TODO
        int socketTimeout = 1000000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objRequest.setRetryPolicy(policy);

        mRequestQueue.add(objRequest);
    }

    public void publicGetJsonArray(String method,
                             Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {

        String url = publicConstructURL(method);

        final Map<String, String> mHeaders = new ArrayMap<String, String>();
        mHeaders.put(Constants.MASHAPE_HEADER, Constants.MASHAPE_VAL);
        mHeaders.put(Constants.ACCEPT_HEADER, Constants.ACCEPT_H_VAL);

        JsonArrayRequest objRequest = new JsonArrayRequest(Request.Method.GET, url, listener, errorListener) {

            @Override
            public Map getHeaders(){
                return mHeaders;
            }

        };

        //TODO
        int socketTimeout = 1000000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objRequest.setRetryPolicy(policy);

        mRequestQueue.add(objRequest);
    }


    public void getWithParams(String method, final Map<String, String> getParams,
                              Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        String url = method;


        JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.GET, url, listener, errorListener) {
            @Override
            public Map getParams() throws AuthFailureError {
                Map<String, String> params = getParams;
                return params;
            }
        };

        //TODO
        int socketTimeout = 1000000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objRequest.setRetryPolicy(policy);

        mRequestQueue.add(objRequest);
    }

    public void getString(String method,
                          Response.Listener<String> listener, Response.ErrorListener errorListener) {

        String url = method;
        StringRequest objRequest = new StringRequest(Request.Method.GET, url, listener, errorListener) {

        };

        int socketTimeout = 0;//30 seconds - change to what you want
        //RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //objRequest.setRetryPolicy(policy);

        mRequestQueue.add(objRequest);
    }

    public void scheduleRetryTimer(ECNetworkRequest request) {
        lastRequest = request;
        if (lastRequest != null) {
            Log.d(TAG, "Schedule retry mTimer for request");

            resetTimer();

            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    retryLastRequest();
                }
            }, 5000, 5000);
        }
    }

    public void retryLastRequest() {
        if (lastRequest != null) {
            Log.d(TAG, "Retry request for attempt : " + lastRequest.getAttemptCount());
            lastRequest.setAttemptCount(lastRequest.getAttemptCount() - 1);
            post(lastRequest.getPath(), lastRequest.getJSONObject(), lastRequest.getAttemptCount(), lastRequest.getListener(), lastRequest.getErrorListener());
        } else {
            resetTimer();
        }
    }

    public void cancelLastRequest() {
        Log.d(TAG, "Cancel previous request");
        resetTimer();
        lastRequest = null;
    }

    public void resetTimer() {
        Log.d(TAG, "Reset mTimer");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}