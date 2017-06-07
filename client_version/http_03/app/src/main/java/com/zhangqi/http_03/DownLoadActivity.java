package com.zhangqi.http_03;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by zhangqi on 2016/11/17.
 */
public class DownLoadActivity extends Activity {
    private Button button;
    private TextView textView;
    private int count;
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            int result = msg.what;
            count += result;
            if(count == 3){
                textView.setText("download success");
            }
        };

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        Download download = new Download(handler);
                        download.downLoadFile("http://222.28.84.108:8080/web/music.mp3");
                    }
                }.start();
            }
        });
    }
}
