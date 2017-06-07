package com.zhangqi.pitchreplace;

/**
 * Created by zhangqi on 2017/3/14.
 */

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PitchReplaceThread {
    private static final String[] str={"L2F_zhang_ai1_001.wav"};
    // ////////////////////同步上传多个文件/////////
    public String MyUploadMultiFileSync(String actionUrl, List<String> fileList) {

        String reulstCode = "";
        String boundary = "----WebKitFormBoundaryalcDABe31XZCe9tE";
        String prefix = "--";
        String end = "\r\n";
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type","multipart/form-data; boundary="+ boundary);
            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
            for (int i = 0; i < fileList.size(); i++) {
                String filePath = fileList.get(i);
                int endFileIndex = filePath.lastIndexOf("/");
                String fileName = filePath.substring(endFileIndex + 1);
                // 取得文件的FileInputStream
                outputStream.writeBytes(prefix+boundary+end);
                outputStream.writeBytes("Content-Disposition: form-data; " + "name=\"file\"; filename=\"" + fileName +"\""+ end);
                outputStream.writeBytes(end);
                FileInputStream fileInputStream = new FileInputStream(filePath);
                byte[] bufferSize = new byte[1024];
                int len;
                while ((len = fileInputStream.read(bufferSize)) != -1){
                    outputStream.write(bufferSize, 0, len);
                }
                outputStream.writeBytes(end);
            }
            outputStream.writeBytes(prefix+boundary+prefix+end);
            outputStream.flush();

            StringBuffer sb = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String str;
            while ((str = reader.readLine()) != null){
                sb.append(str);
            }
            if(reader != null){
                reader.close();
            }
            reulstCode = sb.toString().trim();
            if(outputStream != null){
                outputStream.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reulstCode;
    }
    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Pitch");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String newUrl = fileUrl + "?" + "filename="+fileName;
            FileDownloader.downloadFile(newUrl, pdfFile);
            return null;
        }
    }

    public void  MyDownloadMultiFileSync(){
        for(int i = 0; i < str.length; i++){
            new DownloadFile().execute("http://172.26.44.222:8080/PitchReplace/Download", str[i]);
        }
    }
}
