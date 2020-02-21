package com.example.nearest_dept;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Papers_List extends AppCompatActivity {
    RelativeLayout logout_lay;
    TextView activity_title;
    FloatingActionButton download;
    private WebView wv1;
    ProgressDialog pb;
    String currentDateandTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.papers_list);
        logout_lay=findViewById(R.id.logout_lay);
        download=findViewById(R.id.download);
        activity_title=findViewById(R.id.activity_title);
        Intent i=getIntent();
        activity_title.setText(i.getStringExtra("name"));
        pb=new ProgressDialog(this);
        logout_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        wv1=(WebView)findViewById(R.id.webview);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
         currentDateandTime = sdf.format(new Date());


        pb.show();
pb.setMessage("Loading...");

        wv1.loadUrl(i.getStringExtra("paper_url"));
        wv1.setWebViewClient(new MyClient());
        wv1.setWebChromeClient(new GoogleClient());
        WebSettings webSettings = wv1.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv1.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv1.clearCache(true);
        wv1.clearHistory();
        wv1.setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));


                request.setMimeType(mimeType);


                String cookies = CookieManager.getInstance().getCookie(url);


                request.addRequestHeader("cookie", cookies);


                request.addRequestHeader("User-Agent", userAgent);


                request.setDescription("Downloading file...");


                request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                        mimeType));


                request.allowScanningByMediaScanner();


                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir("Papers",currentDateandTime+".pdf");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "File download in your internal storage, Papers folder",
                        Toast.LENGTH_LONG).show();
            }});

    }

    class MyClient extends WebViewClient
    {

        @Override
        public void onPageStarted(WebView view,String url,Bitmap favicon){
            super.onPageStarted(view,url,favicon);

        }
        @SuppressLint("RestrictedApi")
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String Url)
        {
            if (Url.endsWith(".pdf")) {
                pb.show();
                view.loadUrl("https://docs.google.com/gview?embedded=true&url="+Url);
                download.setVisibility(View.VISIBLE);
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.loadUrl(Url);
                    }
                });

                // if want to download pdf manually create AsyncTask here
                // and download file
                return true;
            }else {
                pb.show();
                view.loadUrl(Url);
        }
            return true;

        }
        @Override
        public void onPageFinished(WebView view,String url)
        {
            pb.dismiss();
            super.onPageFinished(view,url);

        }
    }
    class GoogleClient extends WebChromeClient
    {
        @Override
        public void onProgressChanged(WebView view,int newProgress)
        {
            super.onProgressChanged(view,newProgress);

        }
    }
    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {
        if (wv1.canGoBack()){
            download.setVisibility(View.GONE);
            wv1.goBack();}
        else {
            super.onBackPressed();
        }
    }
}
