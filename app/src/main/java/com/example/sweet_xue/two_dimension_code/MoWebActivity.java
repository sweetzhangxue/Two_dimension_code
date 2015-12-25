package com.example.sweet_xue.two_dimension_code;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MoWebActivity extends AppCompatActivity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mo_web);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url_");

        webView = (WebView) findViewById(R.id.webView_);
        //用于相应js控件
        webView.getSettings().setJavaScriptEnabled(true);

        loadhtml(url);

    }

    private void loadhtml(String url){
        webView.setWebViewClient(new WebViewClient(){
            //网页加载开始时调用
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            //网页加载完成时调用
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            //网页加载失败时调用
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
        webView.loadUrl(url);
    }
}
