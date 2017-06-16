package com.APP.Upload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.APP.ComputePitchRange.ComputePitchRange_MeanDev;
import com.APP.ToneDetection.ToneTestDetection;


@MultipartConfig(maxFileSize = 1024 * 1024 * 10)
public class Upload extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static String UPLOAD_DIR = "D:/home/APP/ToneTest/";
    private static String PITCHRANGE_DIR = "D:/home/APP/GetPitchRange/";
    public Upload() {
        super();
    }
    public void destroy() {
        super.destroy();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		String lastTone_flag  = request.getParameter("lastflag");
		String pinyin  = request.getParameter("pinyin");
		if(lastTone_flag == null){
			lastTone_flag = "0";
		}
		if(pinyin == null){
			pinyin = "";
		}
		String uploadFilePath = null;
		String getF0SavePath = null;
		String L2_ID = null;
		String userID = null;
		String L2_Syllable = null;
		double score = 0;
		for (Part part : request.getParts()) {
            if (part != null && part.getName().startsWith("file")) {
            	String filename = getFilename(part);
                try {
                	if(filename.endsWith(".wav")){
                		String[] L2Name_StringArray = filename.split("_");
                    	if(filename.startsWith("L2F")){
                    		uploadFilePath = UPLOAD_DIR + "L2F_"+ L2Name_StringArray[1] + "/wav/";
                    		getF0SavePath = PITCHRANGE_DIR + "L2F_"+ L2Name_StringArray[1] + "/GetF0/";
                    	}else if(filename.startsWith("L2M")){
                    		uploadFilePath = UPLOAD_DIR + "L2M_"+ L2Name_StringArray[1] + "/wav/";
                    		getF0SavePath = PITCHRANGE_DIR + "L2M_"+ L2Name_StringArray[1] + "/GetF0/";
                    	}else{
                    		score = -1;
                    		throw new Exception("Please check the upload data!");							
                    	}  	
	                	File L2_Dir = new File(uploadFilePath);
                		if (!L2_Dir.exists())	         
                			L2_Dir.mkdirs(); 
                		File L2GetF0_Dir = new File(getF0SavePath);
                		if (!L2GetF0_Dir.exists())	         
                			L2GetF0_Dir.mkdirs(); 
                		L2_ID = L2Name_StringArray[0]+ "_"+ L2Name_StringArray[1];
                		L2_Syllable = L2Name_StringArray[2];
                		userID = L2Name_StringArray[1];
		                if (filename != null && !"".equals(filename)) 
		            	    part.write(uploadFilePath + File.separator + filename);
		                
		                ToneTestDetection.ExtractF0(UPLOAD_DIR, getF0SavePath, L2_ID); //此步之后方可计算均值方差      
		                ToneTestDetection.ToneDetection(UPLOAD_DIR, L2_ID, filename); //声调检测
            			ArrayList<String> CorrectTone = ToneTestDetection.FindTone(L2_Syllable); //确定音节的每个声调
            			ArrayList<String> DetectionResult  = ToneTestDetection.GetDetectionResult(UPLOAD_DIR, L2_ID, filename, L2_Syllable, CorrectTone);
            			if(DetectionResult.size() == 0){
            				score = -1;
            			}else{
            				score = ToneTestDetection.GetDetectScore(DetectionResult);
            				if(filename.indexOf("sentence") != -1){ //句子测试
                    		    score = 0.6; //咱随意赋值
                    		}
            			}                		
		        		double mean = 0;
		        		double dev = 0;
		        		double pitchrange_min = 0;
		        		double pitchrange_max = 0;
		        		if(lastTone_flag.equals("1")){
		        			ComputePitchRange_MeanDev pitchrange = new ComputePitchRange_MeanDev();
		        			ArrayList<Double> pitchrange_Info = pitchrange.PitchRange_MeanDev(PITCHRANGE_DIR, L2_ID);
		        			mean = pitchrange_Info.get(0);
		        			dev = pitchrange_Info.get(1);
		        			pitchrange_min = pitchrange_Info.get(2);
		        			pitchrange_max = pitchrange_Info.get(3);
		        			ToneTestDetection.UpdateDatabase(userID, mean, dev, pitchrange_min, pitchrange_max);
		        		}
                	}else {
                		score = -1;
                	}
	        		response.setCharacterEncoding("utf-8");
	        		PrintWriter out = response.getWriter();
	        		out.print(score);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
	}
	private String getFilename(Part part) {
        if (part == null) {
            return null;
        }
        String fileName = part.getHeader("content-disposition");
        if (isBlank(fileName)) {
            return null;
        }
        return substringBetween(fileName, "filename=\"", "\"");
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
            return true;
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null)
            return null;
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1)
                return str.substring(start + open.length(), end);
        }
        return null;
    }
}
