package com.zhangqi.pitchreplace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PitchReplaceActivity extends AppCompatActivity {
    private static final String TAG = "PitchReplaceActivity";

    private Button upload;
    private Context mContext;
    private WebView webView;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_replace);
        upload = (Button) findViewById(R.id.button);
        mContext = this;
        setOnclickListener();

        Log.d("Download complete","----------");
    }
    protected void onResume() {
        super.onResume();
    }
    private void setOnclickListener() {
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                uploadThreadTest();
            }
        });
    }
    public void uploadThreadTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    upload();
                    Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(goToNextActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void upload() {
        String url = "http://222.28.86.253:8080/PitchReplace_linux/Upload";
//        String url = "http://172.26.44.222:8080/PitchReplace/Upload";
        List<String> fileList = getCacheFiles();

        if (fileList == null) {
            myHandler.sendEmptyMessage(-1);
        }else {
            PitchReplaceThread myUpload = new PitchReplaceThread();
            //同步请求，直接返回结果，根据结果来判断是否成功。
            String reulstHTML = myUpload.MyUploadMultiFileSync(url, fileList);
            System.out.print("AAAAAAAAAAAAAAAAAA\n" + reulstHTML);
            //myUpload.MyDownloadMultiFileSync();
            GlobalVariable.draw_pitch_html_ = reulstHTML;
            myHandler.sendEmptyMessage(0);
        }


    }

    private List<String> getCacheFiles() {
        List<String> fileList = new ArrayList<String>();
        File  sd = Environment.getExternalStorageDirectory(); //路径：0//storage//emulated//0
        String path = sd.getPath()+"/tonetest";
        File catchPath = new File(path);
        if (catchPath.exists()) {
            File[] files = catchPath.listFiles();
            if (files == null || files.length<1) {
                return null;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() &&(files[i].getAbsolutePath().endsWith(".wav") || files[i].getAbsolutePath().endsWith(".textgrid")) ) {
                    fileList.add(files[i].getAbsolutePath());
                }
            }
            return fileList;
        }
        return null;
    }

    ////////////handler/////
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG,"handleMessage msg===" + msg);
            if (msg.what == -1) {
                Toast.makeText(mContext, "not find file!", Toast.LENGTH_LONG).show();
                return;
            }else {
                Toast.makeText(mContext, "upload success!", Toast.LENGTH_LONG).show();
            }
        }
    };


}
