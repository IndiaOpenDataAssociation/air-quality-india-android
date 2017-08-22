package com.idn0phl3108ed43d22s30.network;

import com.android.volley.Response;

import org.json.JSONObject;


public class ECNetworkRequest {
    private String mPath;
    private JSONObject mJSONObject;
    private int mAttemptCount;
    private Response.Listener<JSONObject> mListener;
    private Response.ErrorListener mErrorListener;

    public ECNetworkRequest(String path, JSONObject jsonObject, int attemptCount, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        mPath = path;
        mJSONObject = jsonObject;
        mAttemptCount = attemptCount;
        mListener = listener;
        mErrorListener = errorListener;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    public void setJSONObject(JSONObject JSONObject) {
        mJSONObject = JSONObject;
    }

    public int getAttemptCount() {
        return mAttemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        mAttemptCount = attemptCount;
    }

    public Response.Listener<JSONObject> getListener() {
        return mListener;
    }

    public void setListener(Response.Listener<JSONObject> listener) {
        mListener = listener;
    }

    public Response.ErrorListener getErrorListener() {
        return mErrorListener;
    }

    public void setErrorListener(Response.ErrorListener errorListener) {
        mErrorListener = errorListener;
    }
}
