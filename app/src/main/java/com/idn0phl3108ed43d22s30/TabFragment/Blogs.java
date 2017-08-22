package com.idn0phl3108ed43d22s30.TabFragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.Constants;

import static android.content.ContentValues.TAG;

/**
 * Created by Rutul on 22-06-2016.
 */
public class Blogs extends Fragment {
    private View rootView;
    private WebView webView;

    @Override
    public void onResume() {
        super.onResume();
        ConfigView();
        setWebView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_blog, container, false);
        ConfigView();
        setWebView();
        return rootView;
    }

    private void ConfigView() {
        webView = (WebView) rootView.findViewById(R.id.blog_webview);
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

        String webUrl = Constants.BLOG_URL;
        webView.loadUrl(webUrl);
        webView.getSettings().setJavaScriptEnabled(true);


    }
}
