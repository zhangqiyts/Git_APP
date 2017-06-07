package com.zhangqi.http_03;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zhangqi on 2016/9/29.
 */
public class HttpTHread extends Thread {
    private String url; //参数
    private Handler handler;
    private ImageView imageView;
    public HttpTHread(String url, ImageView imageView, Handler handler){
        //初始化操作
        this.url = url;
        this.imageView = imageView;
        this.handler = handler;
    }
    //run方法处理网络耗时的操作
    public void run() {
        try {
            URL httpUrl = new URL(url);
            try {
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);//得到输入流
                InputStream in = conn.getInputStream();

                FileOutputStream out = null;
                File downloadFile = null;
                String fileName = String.valueOf(System.currentTimeMillis());

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断SD卡是否存在,Environment.getExternalStorageState()为sd卡挂载状态
                     File parent = Environment.getExternalStorageDirectory();
                     downloadFile = new File(parent, fileName);
                     out = new FileOutputStream(downloadFile);
                }
                byte[] b = new byte[2*1024];
                int len;
                if(out != null){
                    while ((len = in.read(b))!= -1){
                        out.write(b, 0, len);
                    }
                }
                final Bitmap bitmap = BitmapFactory.decodeFile(downloadFile.getAbsolutePath());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        super.run();
    }
}
