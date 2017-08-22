package com.idn0phl3108ed43d22s30.TabFragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;

import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.adapter.CommunityDevicesAdapter;
import com.idn0phl3108ed43d22s30.etc.Constants;

import static android.content.ContentValues.TAG;

/**
 * Created by Rutul on 21-06-2016.
 */
public class FragmentDevices extends Fragment {
    View rooView;
    private ListView listView;
    private WebView webView;
    String[] device = {"Breath-i", "Polludrone", "AirOwl"};
    String[] description = {"", "", ""};
    Integer[] imageId = {R.drawable.img_device_airowl, R.drawable.img_device_polludron, R.drawable.img_device_breathi};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rooView = inflater.inflate(R.layout.fragment_devices, container, false);
        configView();
        setWebView();
        return rooView;

    }
    @Override
    public void onResume() {
        super.onResume();
        configView();
        setWebView();
    }
    private void setWebView() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //show you progress image
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "Exception occurred with details : " + exception.toString());
                }

                super.onPageFinished(view, url);
            }
        });

        String webUrl = Constants.DEVICE_URL;

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(webUrl);


    }


    private void configView() {
        CommunityDevicesAdapter newsList = new CommunityDevicesAdapter(getActivity(), device, imageId, description);
        webView = (WebView) rooView.findViewById(R.id.device_webview);
//        listView = (ListView) rooView.findViewById(R.id.listDevice);
//        listView.setAdapter(newsList);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
    }
}
