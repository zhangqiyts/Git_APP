package com.zhangqi.http_03;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by zhangqi on 2016/11/17.
 */
public class Download {
    public Handler handler;
    public Download(Handler handler){
        this.handler = handler;
    }
    private Executor threadPool = Executors.newFixedThreadPool(3); //创建线程池，3个线程

    static class DownLoadRunnable implements Runnable{
        private String url;
        private String fileName;
        private long start;
        private long end;
        private Handler handler;

        //指定构造方法
        public DownLoadRunnable(String url, String fileName, long start, long end, Handler handler){
            this.url = url;
            this.fileName = fileName;
            this.start = start;
            this.end = end;
            this.handler = handler;
        }
        @Override
        public void run() {
            try {
                URL httpUrl = new URL(url);
                try {
                    HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
                    RandomAccessFile accessFile = new RandomAccessFile(new File(fileName), "rwd");
                    accessFile.seek(start);
                    InputStream in = conn.getInputStream();
                    byte[] b = new byte[4*1024];
                    int len = 0;
                    while ((len = in.read(b))!= -1){
                        accessFile.write(b, 0, len);
                    }
                    if(accessFile != null){
                        accessFile.close();
                    }
                    if(in != null){
                        in.close();
                    }

                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }
    public void downLoadFile(String url){
        try {
            URL httpUrl = new URL(url);
            try {
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                int count = conn.getContentLength(); //拿去下载图片的总体长度
                int block = count/3; //每个线程下载大小

                String fileName = getFileName(url);
                File parent = Environment.getExternalStorageDirectory();
                File fileDownLoad = new File(parent, fileName);


                /**假设文件长度为11个字节，开取3个线程 11/3 = 3 余 2
                 * 第一个线程 0-2
                 * 第二个线程 3-5
                 * 第三个线程 6-10
                 */
                for (int i = 0; i < 3; i++){
                    long start = i * block;
                    long end = (i+1) * block - 1;
                    if(i == 2){
                        end = count;
                    }
                    DownLoadRunnable runnable = new DownLoadRunnable(url, fileDownLoad.getAbsolutePath(), start, end, handler);
                    threadPool.execute(runnable); //通过线程池提交任务
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getFileName(String url){
        return url.substring(url.lastIndexOf("/")+1);
    }
}
