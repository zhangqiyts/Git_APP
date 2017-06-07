package com.zhangqi.pitchreplace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //WebView browser=(WebView) findViewById(R.id.webView); //if you gave the id as browser
        WebView browser = new WebView(this);
        setContentView(browser);
        browser.getSettings().setJavaScriptEnabled(true); //Yes you have to do it
       // browser.loadUrl("http://172.26.44.222:8080/PitchReplace");
        //String summary = "<html><body>You scored <b>192</b> points.</body></html>";
        //browser.loadData(summary, "text/html", null);
        browser.loadData(GlobalVariable.draw_pitch_html_, "text/html", null);
       // browser.loadDataWithBaseURL(null, "http://localhost:8080/PitchReplace", "text/html", "utf-8", null);
    }
}
