package com.smartlab.wpslidingmenu;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebViewFragmentii extends AppCompatActivity {


    String sCOMPID ="";
    String sContractID ="";
    String sAssetID = "";
    String sInfo = "";
    String url;

    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_web_view);
        String sURL ="";

        ProgressBar progressBar;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            sURL = bundle.getString("sURL");

        }

        //url to reload webview
       // url = sURL + "?ok=1&svalue="+ sCOMPID +"&svalue2="+ sContractID +"&svalue3="+ sAssetID;
        url = sURL;
        Log.i("displayUrl",url);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(url);
        progressBar.setProgress(0);

        final ProgressBar finalProgressBar = progressBar;
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100)
                    finalProgressBar.setVisibility(View.INVISIBLE);
                else
                    finalProgressBar.setVisibility(View.VISIBLE);
                finalProgressBar.setProgress(newProgress);
            }
        });

    }
}
