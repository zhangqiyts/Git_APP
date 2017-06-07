package com.zhangqi.http_03;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zhangqi on 2016/11/18.
 */
public class UploadThread extends Thread {
    private String fileName;
    private String url;

    public UploadThread(String url, String fileName){
        this.url = url;
        this.fileName = fileName;
    }
    @Override
    public void run() {
        String boundary = "----WebKitFormBoundaryalcDABe31XZCe9tE";
        String prefix = "--";
        String end = "\r\n";

        URL httpUrl = null;
        try {
            httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary="+ boundary);


            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.writeBytes(prefix+boundary+end);
            outputStream.writeBytes("Content-Disposition: form-data; " + "name=\"file\"; filename=\"L2F_zhang_ai1_001.wav" + "\"" + end);
            outputStream.writeBytes(end);
            FileInputStream fileInputStream = new FileInputStream(new File(fileName));
            byte[] b = new byte[1024*4];
            int len;
            while ((len = fileInputStream.read(b)) != -1){
                outputStream.write(b, 0, len);
            }
            outputStream.writeBytes(end);
            outputStream.writeBytes(prefix+boundary+prefix+end);
            outputStream.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null){
                sb.append(str);
            }

            System.out.println("response:" + sb.toString());
            if(outputStream != null){
                outputStream.close();
            }
            if(reader != null){
                reader.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
