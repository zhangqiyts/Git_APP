package com.APP.Upload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.APP.MakeTextGrid.MakeTextGrid;
import com.APP.PitchReplace.PitchReplace;

@MultipartConfig(maxFileSize = 1024 * 1024 * 10)
public class Upload extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "D:/home/APP/PitchReplace/";
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
		for (Part part : request.getParts()) {
            if (part != null && part.getName().startsWith("file")) {
                String filename = getFilename(part);
                System.out.println( filename);
                //因为上传框有多个，为了避免有的上传框没有选择文件导致出错，这里需要判断filename是否为null或为空
                if (filename != null && !"".equals(filename)) {    	
                	if(filename.endsWith(".wav") && filename.startsWith("L2")){
                		String pattern = "(.+)\\.wav$";
		            	Pattern p = Pattern.compile(pattern);
		            	// 现在创建 matcher 对象
		                Matcher m = p.matcher(filename);
		                
		                if (m.find()) {
		                	PitchReplacePath = UPLOAD_DIR + m.group(1);
		                	uploadFilePath = UPLOAD_DIR + m.group(1) + "/data";
		                	System.out.println(uploadFilePath + File.separator + filename);
		                	File fileSaveDir = new File(uploadFilePath);
	                		if (!fileSaveDir.exists())	         
	                            fileSaveDir.mkdirs();
		                }
                	}
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
		mktextgrid.MakeAlignMlf();
		mktextgrid.MakeOutTextGrid();
		
		PitchReplace extractf0 = new PitchReplace(PitchReplacePath);
		extractf0.ExtractPitchTierToF0();
		extractf0.Extract10F0();
		extractf0.ExtractTime();
		extractf0.PitchReplace_final();
		extractf0.GetReturnData();
		
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print("upload success");
		
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

