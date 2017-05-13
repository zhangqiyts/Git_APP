package com.APP.Upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.APP.ExtractMeanDev.ComputeMeanDev;
import com.APP.LinearRegression.LinearRegression;
import com.APP.MakeTextGrid.MakeTextGrid;
import com.APP.PitchReplace.PitchReplace;

@MultipartConfig(maxFileSize = 1024 * 1024 * 10)
public class Upload extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static String UPLOAD_DIR = "D:/home/APP/PitchReplace/";
    private static final String return_file = "D:/workspace_java/PitchReplace/WebContent/DrawPitch.jsp";
    
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
		response.setCharacterEncoding("utf-8");	
		String uploadFilePath = null;
		String PitchReplacePath = null;
		String Chinese_ID = null;
		String L2_ID = null;
		String UPLOAD_DIR_NEW =null;
		for (Part part : request.getParts()) {
            if (part != null && part.getName().startsWith("file")) {
                String filename = getFilename(part);
//                System.out.println( filename);
                //因为上传框有多个，为了避免有的上传框没有选择文件导致出错，这里需要判断filename是否为null或为空
                try {
                	if (filename != null && !"".equals(filename)) { 
                    	if(filename.endsWith(".wav")){
                    		String[] L2Name_StringArray = filename.split("_");
                        	if(filename.startsWith("L2F")){
                        		Chinese_ID = "F2_"+ L2Name_StringArray[2];
                        		UPLOAD_DIR_NEW = UPLOAD_DIR + "L2F_"+ L2Name_StringArray[1] + "/";
                        	}else if(filename.startsWith("L2M")){
                        		Chinese_ID = "M1_"+ L2Name_StringArray[2];
                        		UPLOAD_DIR_NEW = UPLOAD_DIR + "L2M_"+ L2Name_StringArray[1] + "/";
                        	}else{	
                        		throw new Exception("Please check the upload data!");							
                        	}  	
//                        	System.out.println(UPLOAD_DIR_NEW);
		                	File L2_Dir = new File(UPLOAD_DIR_NEW);
	                		if (!L2_Dir.exists())	         
	                			L2_Dir.mkdirs();
	                		
                    		String pattern = "(.+)\\.wav$";
    		            	Pattern p = Pattern.compile(pattern);
    		            	// 现在创建 matcher 对象
    		                Matcher m = p.matcher(filename);
    		                if (m.find()) {
    		                	L2_ID = m.group(1);
    		                	PitchReplacePath = UPLOAD_DIR_NEW + m.group(1);
    		                	uploadFilePath = UPLOAD_DIR_NEW + m.group(1) + "/data";
//    		                	System.out.println(PitchReplacePath);
    		                	File fileSaveDir = new File(uploadFilePath);
    	                		if (!fileSaveDir.exists())	         
    	                            fileSaveDir.mkdirs();
    		                } 
                    	}
                	}	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
		
		for (Part part : request.getParts()) {
			if (part != null && part.getName().startsWith("file")) {
                String filename = getFilename(part);
                //因为上传框有多个，为了避免有的上传框没有选择文件导致出错，这里需要判断filename是否为null或为空
               
                if (filename != null && !"".equals(filename)) 
                	part.write(uploadFilePath + File.separator + filename);
            }
		}
        
		MakeTextGrid mktextgrid = new MakeTextGrid(PitchReplacePath);			
		mktextgrid.MakeScp();
		mktextgrid.MakeMlf();
		mktextgrid.MakeAlignTextGrid();
//		mktextgrid.MakeOutTextGrid();
		
		PitchReplace extractf0 = new PitchReplace(PitchReplacePath);
//		extractf0.ExtractPitchTierToF0();
		extractf0.ExtractTimeF0(UPLOAD_DIR, L2_ID); //此步之后方可计算均值方差
		
		ComputeMeanDev meandev = new ComputeMeanDev();
		double[] mean_dev = meandev.ComputeMeanDev(UPLOAD_DIR, L2_ID);
		
		extractf0.PitchReplace_final(UPLOAD_DIR, mean_dev[0], mean_dev[1], Chinese_ID, L2_ID);
		
		LinearRegression linearregression = new LinearRegression(UPLOAD_DIR_NEW);
		linearregression.MakeL2Zscore(UPLOAD_DIR, mean_dev[0], mean_dev[1], Chinese_ID, L2_ID);
		String[] CH_L2_Time_F0 = linearregression.PrepareForRegression(UPLOAD_DIR_NEW, Chinese_ID, L2_ID);
		
		if(CH_L2_Time_F0.length == 4){
			request.setAttribute("CH_Time", CH_L2_Time_F0[0]); 
			request.setAttribute("CH_F0", CH_L2_Time_F0[1]);
			request.setAttribute("L2_Time", CH_L2_Time_F0[2]);
			request.setAttribute("L2_F0", CH_L2_Time_F0[3]);
			request.setAttribute("L2_ID", L2_ID);
			request.setAttribute("Chinese_ID", Chinese_ID);
		}
		request.getRequestDispatcher("DrawPitch.jsp").forward(request, response);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
//		File file = new File(return_file);
//		if(!file.exists()){
//			throw new ServletException("File doesn't exists on server.");
//		}
//		System.out.println("File location on server::"+file.getAbsolutePath());
//		ServletContext ctx = getServletContext();
//		InputStream fis = new FileInputStream(file);
//		String mimeType = ctx.getMimeType(file.getAbsolutePath());
//		response.setContentType(mimeType != null? mimeType:"application/octet-stream");
//		response.setContentLength((int) file.length());
//		String fileName = "DrawPitch.jsp";
//		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//
//		ServletOutputStream os = response.getOutputStream();
//		byte[] bufferData = new byte[1024];
//		int read=0;
//		while((read = fis.read(bufferData))!= -1){
//			os.write(bufferData, 0, read);
//		}
//		os.flush();
//		os.close();
//		fis.close();
//		System.out.println("File downloaded at client successfully");
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

