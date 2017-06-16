package com.APP.Upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.APP.ComputePitchRange.ComputePitchRange_MeanDev;
import com.APP.DrawWave.PlotUtil;
import com.APP.FileProcess.FileProcess;
import com.APP.LinearRegression.LinearRegression;
import com.APP.MakeTextGrid.MakeTextGrid;
import com.APP.PitchReplace.PitchReplace;
import com.APP.ToneLearnDetection.ToneLearnDetection;

@MultipartConfig(maxFileSize = 1024 * 1024 * 10)
public class Upload extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static String UPLOAD_DIR = "/home/APP/PitchReplace/";
    private static String PITCHRANGE_DIR = "/home/APP/GetPitchRange/";
    private static String PROJECT_DIR = "/opt/tomcat/webapps/PitchReplace_linux/data/";
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
			pinyin = "nǐ hǎo";
		}
		String uploadFilePath = null;
		String PitchReplacePath = null;
		String getF0SavePath = null;
		String Chinese_filename = null;
		String Chinese_wavname = null;
		String Chinese_filedir = null;
		String UPLOAD_DIR_NEW =null;
		String L2_ID = null;
		String userID = null;
		String L2_Syllable = null;
		double score = 0;
		for (Part part : request.getParts()) {
            if (part != null && part.getName().startsWith("file")) {
            	String wavname = getFilename(part);
                try {
                	if(wavname.endsWith(".wav")){	
                		String L2_filename = wavname.substring(0, wavname.lastIndexOf(".wav"));
                		String[] L2Name_StringArray = L2_filename.split("_");
                    	if(wavname.startsWith("L2F")){
                    		Chinese_filedir = "CHF";
                    		Chinese_filename = "CHF_"+ L2Name_StringArray[2];
                    		UPLOAD_DIR_NEW = UPLOAD_DIR + "L2F_"+ L2Name_StringArray[1] + "/";
                    		getF0SavePath = PITCHRANGE_DIR + "L2F_"+ L2Name_StringArray[1] + "/GetF0/";
                    	}else if(wavname.startsWith("L2M")){
                    		Chinese_filedir = "CHM";
                    		Chinese_filename = "CHM_"+ L2Name_StringArray[2];
                    		UPLOAD_DIR_NEW = UPLOAD_DIR + "L2M_"+ L2Name_StringArray[1] + "/";
                    		getF0SavePath = PITCHRANGE_DIR + "L2M_"+ L2Name_StringArray[1] + "/GetF0/";
                    	}else{	
                    		throw new Exception("Please check the upload data!");							
                    	}  
                    	
                    	Chinese_wavname = Chinese_filename + ".wav";         	
                    	uploadFilePath = UPLOAD_DIR_NEW + L2_filename + "/data";
                    	PitchReplacePath = UPLOAD_DIR_NEW + L2_filename;
                    	
	                	File L2_Dir = new File(UPLOAD_DIR_NEW);
                		if (!L2_Dir.exists())	         
                			L2_Dir.mkdirs();
                		File L2GetF0_Dir = new File(getF0SavePath);
                		if (!L2GetF0_Dir.exists())	         
                			L2GetF0_Dir.mkdirs(); 
		                File fileSaveDir = new File(uploadFilePath);
                		if (!fileSaveDir.exists())	         
                            fileSaveDir.mkdirs();
                		
                		L2_ID = L2Name_StringArray[0]+ "_"+ L2Name_StringArray[1];
                		L2_Syllable = L2Name_StringArray[2];
                		userID = L2Name_StringArray[1];
                		
		               //因为上传框有多个，为了避免有的上传框没有选择文件导致出错，这里需要判断filename是否为null或为空
                		if (wavname != null && !"".equals(wavname)) 
                			part.write(uploadFilePath + File.separator + wavname);
                		
                		String L2_pngname = L2_filename + ".png";
            			PlotUtil.drawWaveImage(uploadFilePath + "/" + wavname, uploadFilePath + "/" + L2_pngname,600,200,false);
            			
                		ToneLearnDetection.ToneDetection(UPLOAD_DIR, L2_ID, wavname); //声调检测
                		ArrayList<String> CorrectTone = ToneLearnDetection.FindTone(L2_Syllable); //确定音节的每个声调
                		ArrayList<String> DetectionResult  = ToneLearnDetection.GetDetectionResult(UPLOAD_DIR, L2_ID, wavname, L2_Syllable, CorrectTone);
                		ArrayList<String> DetectionTone = ToneLearnDetection.DetectionTone(DetectionResult);
                		score = ToneLearnDetection.GetDetectScore(DetectionResult);
                	
                		String toneResultFlag = ToneLearnDetection.CompareTone(CorrectTone, DetectionTone);
                		int feedbackFlag = ToneLearnDetection.GetFeedBackFlag(toneResultFlag); //0代表存在错误，需要给反馈，1代表全部正确
                		
                		if(feedbackFlag == 1){
                			String L2_wav_oldpath = uploadFilePath + "/" + wavname;
                			String L2_wav_newpath = PROJECT_DIR + L2_filename +"/" + wavname;
                			String L2_png_oldpath = uploadFilePath + "/" + L2_pngname;
                			String L2_png_newpath = PROJECT_DIR + L2_filename +"/" + L2_pngname;
                			String CH_wav_oldpath = UPLOAD_DIR + Chinese_filedir + "/" + Chinese_filename + "/" + Chinese_wavname;
                			String CH_wav_newpath = PROJECT_DIR + L2_filename +"/" + Chinese_wavname;
                			System.out.println(CH_wav_oldpath);
                			FileProcess.CopyFile(L2_wav_oldpath, L2_wav_newpath);
                			FileProcess.CopyFile(L2_png_oldpath, L2_png_newpath);
                			FileProcess.CopyFile(CH_wav_oldpath, CH_wav_newpath);
                			
                			request.setAttribute("PinYin", pinyin);
                			System.out.println(score);	
                			request.setAttribute("Score", score);              			
                			request.setAttribute("L2_ID", L2_filename);
                			request.getRequestDispatcher("CorrectFeedBack.jsp").forward(request, response);
                			response.setContentType("text/html;charset=utf-8");
                		}else{
                			MakeTextGrid.MakeScp(PitchReplacePath);
                			MakeTextGrid.MakeMlf(PitchReplacePath);
                			MakeTextGrid.MakeAlignTextGrid(PitchReplacePath);	  			
                			PitchReplace.ExtractTimeF0(UPLOAD_DIR, getF0SavePath, L2_filename); //此步之后方可计算均值方差	
                			double mean = 0;
                			double dev = 0;
                			double pitchrange_min = 0;
                			double pitchrange_max = 0;
                			ArrayList<Double> pitchrange_Info = null;
                			if(lastTone_flag.equals("1")){
                				pitchrange_Info = ComputePitchRange_MeanDev.PitchRange_MeanDev(PITCHRANGE_DIR, L2_ID);
                				mean = pitchrange_Info.get(0);
                				dev = pitchrange_Info.get(1);
                				pitchrange_min = pitchrange_Info.get(2);
                				pitchrange_max = pitchrange_Info.get(3);
                				ToneLearnDetection.UpdateDatabase(userID, mean, dev, pitchrange_min, pitchrange_max);
                			}else {
                				//查询数据库
                				pitchrange_Info = ToneLearnDetection.SelectDatabase(userID);
                				mean = pitchrange_Info.get(0);
                				dev = pitchrange_Info.get(1);
                				pitchrange_min = pitchrange_Info.get(2);
                				pitchrange_max = pitchrange_Info.get(3);
                			}

                			PitchReplace.PitchReplace_final(UPLOAD_DIR, mean, dev, Chinese_filename, L2_filename);
                			LinearRegression.MakeL2Zscore(UPLOAD_DIR, mean, dev, Chinese_filename, L2_filename, pitchrange_min, pitchrange_max);
                			String[] CH_L2_Time_F0 = LinearRegression.PrepareForRegression(UPLOAD_DIR_NEW, Chinese_filename, L2_filename);
                			
                			if(CH_L2_Time_F0.length == 4){
                				request.setAttribute("CH_Time", CH_L2_Time_F0[0]); 
                				request.setAttribute("CH_F0", CH_L2_Time_F0[1]);
                				request.setAttribute("L2_Time", CH_L2_Time_F0[2]);
                				request.setAttribute("L2_F0", CH_L2_Time_F0[3]);
                				request.setAttribute("L2_ID", L2_filename);
                				request.setAttribute("Chinese_ID", Chinese_filename);
                				request.setAttribute("PinYin", pinyin);
                				request.setAttribute("ToneResultFlag", toneResultFlag);
                				System.out.println(pinyin + toneResultFlag);
                				System.out.println("CH_Time"+ CH_L2_Time_F0[0] + "CH_F0"+ CH_L2_Time_F0[1]);
                				System.out.println("L2_Time"+ CH_L2_Time_F0[2] + "L2_F0"+ CH_L2_Time_F0[3]);
                			}
                			request.getRequestDispatcher("DrawPitch.jsp").forward(request, response);
                			response.setContentType("text/html;charset=utf-8");
                		}
                	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

