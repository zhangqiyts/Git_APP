package com.zhangqi.http_03;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UploadActivity extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //初始化
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String url = "http://192.168.99.125:8080/upload/Upload";
                String url = "http://172.26.43.90:8080/ToneTest/Upload";
                String lastflag = "0";
                String pinyin = "nǐ hǎo";
                try {
                    File file = Environment.getExternalStorageDirectory(); //路径：0//storage//emulated//0
                    File fileAbs = new File(file, "L2F_zhang_ai1_001.wav"); //路径：0//storage//emulated//0//L2F_zhang_ai1_001.wav
                    String fileName = fileAbs.getAbsolutePath();
                    url = url + "?lastflag=" + URLEncoder.encode(lastflag, "utf-8")+ "&pinyin=" + URLEncoder.encode(pinyin, "utf-8"); //转码
                    System.out.println(url);
                    UploadThread thread = new UploadThread(url, fileName);
                    thread.start();
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
