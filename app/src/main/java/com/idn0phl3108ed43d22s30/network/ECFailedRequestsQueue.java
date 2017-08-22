package com.idn0phl3108ed43d22s30.network;

import android.content.Context;

import com.idn0phl3108ed43d22s30.etc.UserPrefrenceUtils;

import org.json.JSONObject;

import java.util.ArrayList;


public class ECFailedRequestsQueue {
    private static String TAG = ECFailedRequestsQueue.class.getSimpleName();
    private static ECFailedRequestsQueue instance;
    private ArrayList<JSONObject> mQueue;
    private Context mContext;

    private static String kPendingRequestsArrayKey = "PendingRequestsArray";

    private ECFailedRequestsQueue(Context context) {
        mContext = context;
        mQueue = UserPrefrenceUtils.getJsonArray(kPendingRequestsArrayKey, context);
    }

    public static ECFailedRequestsQueue getInstance(Context context) {
        if (instance == null) {
            instance = new ECFailedRequestsQueue(context);
        }

        return instance;
    }

    public void addRequestToQueue(JSONObject operation) {
        mQueue.add(operation);
        UserPrefrenceUtils.putJsonArray(mQueue, kPendingRequestsArrayKey, mContext);
    }

    public void clearQueue() {
        mQueue.clear();
        UserPrefrenceUtils.remove(kPendingRequestsArrayKey, mContext);
    }

    public ArrayList<JSONObject> getPendingOperations() {
        return (ArrayList<JSONObject>) mQueue.clone();
    }

    public boolean isRequestPending() {
        return (mQueue.size() > 0);
    }
}
