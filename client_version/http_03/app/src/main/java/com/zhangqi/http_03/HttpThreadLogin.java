package com.zhangqi.http_03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by zhangqi on 2016/11/16.
 */
public class HttpThreadLogin extends Thread {
    String url;
    String name;
    String password;

    public HttpThreadLogin(String url, String name, String password){
        this.url = url;
        this.name = name;
        this.password = password;
    }
    /* 采用GET方式向服务器发送数据，通过url方式发送，不安全，一般请求网页或者发送量小时用doget()，只能支持几kb，中文需转码*/
    private void doGet(){
        try {
            url = url + "?name=" + URLEncoder.encode(name, "utf-8") + "&password=" + URLEncoder.encode(password, "utf-8"); //转码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            URL httpUrl = new URL(url);
            try {
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);// 设置链接超时的时间

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String str;
                    StringBuffer sb = new StringBuffer();

                    while ((str = reader.readLine()) != null){
                        sb.append(str);
                    }
                    String result = sb.toString();
                    System.out.println("result:" + result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
/* 采用POST方式向服务器发送数据，中文无需转码*/
    private void doPost(){
        try {
            URL httpUrl = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            OutputStream out = conn.getOutputStream();
            String content = "name=" + name + "&password=" + password;
            out.write(content.getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null){
                sb.append(str);
            }
            reader.close();
            String result = sb.toString();
            System.out.println("result:" + result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
//        doGet();
        doPost();
    }
}
