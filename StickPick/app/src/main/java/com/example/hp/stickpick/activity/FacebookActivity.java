package com.example.hp.stickpick.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.hp.stickpick.utils.HelperActivity;
import com.example.hp.stickpick.R;

public class FacebookActivity extends AppCompatActivity {

    ProgressDialog dialog;
    String dataUrl = "http://facebook.com/shadabazam.it";
    String buUrl="https://www.bujhansi.ac.in/index.aspx";
    String resultUrl="https://exam.bujhansi.ac.in/frmViewCampusCurrentResult.aspx";
    String stackOverUrl="http://stackoverflow.com/questions/tagged/java";
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        webView = (WebView) findViewById(R.id.webView);
        dialog=new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();
        HelperActivity.setStatusBar(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWebView();
    }

    private void setWebView(){
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (HomeActivity.checkBuFb==1){
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(buUrl);
        }
        else if (HomeActivity.checkBuFb==2){
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(dataUrl);
        }
        else if(HomeActivity.checkBuFb==3) {
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(resultUrl);
        }
        else if(HomeActivity.checkBuFb==4) {
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(stackOverUrl);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            WebSettings webSettings = view.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            dialog.dismiss();
        }
    }

    @Override public void onBackPressed() {

        if(webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
