package com.example.android.popularmovies.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utilities;

/**
 * Created by anuj on 3/10/17.
 */

public class WebViewActivity extends AppCompatActivity {


    WebView webViewurl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.youtube_web_view);

        getSupportActionBar().hide();
        Utilities.setContext(this);

        String articleUrl = getIntent().getStringExtra("youtubeUrl");

        webViewurl = (WebView) findViewById(R.id.webview);

        startWebView(articleUrl);
    }

    private void startWebView(String url) {

        WebSettings settings = webViewurl.getSettings();

        settings.setJavaScriptEnabled(true);
        webViewurl.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        webViewurl.getSettings().setBuiltInZoomControls(true);
        webViewurl.getSettings().setUseWideViewPort(true);
        webViewurl.getSettings().setLoadWithOverviewMode(true);

        final ProgressDialog progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        webViewurl.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebViewActivity.this, "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });
        webViewurl.loadUrl(url);
    }
}
