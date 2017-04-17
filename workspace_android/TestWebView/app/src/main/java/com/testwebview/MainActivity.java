package com.testwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	 private WebView mWebView;  
	 private Button androidCallJSBtn;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void initView() {
    	
        mWebView = (WebView) findViewById(R.id.webview);  
        WebSettings mWebSettings = mWebView.getSettings();  
       
        mWebSettings.setJavaScriptEnabled(true);   //加上这句话才能使用javascript方法  
        mWebView.addJavascriptInterface(new Object() {//增加接口方法,让html页面调用

            @JavascriptInterface //别漏了这个注解
        	public void callJavaMethod() {  
               Toast.makeText(getApplicationContext(), "JS调用Android成功", Toast.LENGTH_LONG).show();
            }
        }, "demo");

        mWebView.loadUrl("file:///android_asset/demo.html");  //加载页面  
        
        androidCallJSBtn = (Button) findViewById(R.id.androidCallJSBtn);  
        androidCallJSBtn.setOnClickListener(new Button.OnClickListener() {  //给button添加事件响应,执行JavaScript的fillContent()方法 
            public void onClick(View v) {  
                mWebView.loadUrl("javascript:pageLoad()");
            }  
        });  
    }  
}
