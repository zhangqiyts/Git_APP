package com.APP.ToneLearnDetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToneLearnDetection {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://172.26.24.48:3306/BLCU";
 
    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "123";
    public static ArrayList<Double> SelectDatabase(String userID) throws IOException{
    	Connection conn = null;
		Statement stmt = null;
        String select_sql;
		double mean = 0;
		double dev = 0;
		double pitchrange_min = 0;
		double pitchrange_max = 0;
		ArrayList<Double> pitchrange_Info = new ArrayList<Double>();
	    try{
		    // 注册 JDBC 驱动
		    Class.forName("com.mysql.jdbc.Driver");   
		    // 打开链接
	        System.out.println("连接数据库...");
	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
	        // 执行查询
	        System.out.println(" 实例化Statement对...");
	        stmt = conn.createStatement();
	        select_sql = "SELECT * FROM users WHERE unique_id = '" + userID + "';"; //暂时为unique_id，应该是id
	        ResultSet select_rs = stmt.executeQuery(select_sql); //executeQuery()用于select
	        // 展开结果集数据库
	        select_rs.last();
	        System.out.println(select_sql);
	        System.out.println(select_rs.getRow());
	        if(select_rs.getRow() <= 0){
	        	throw new Exception("user does not exist!");
	        }else{
	        	mean = select_rs.getDouble("mean");
	            dev = select_rs.getDouble("dev");
	            pitchrange_min = select_rs.getDouble("pitchrange_min");
	            pitchrange_max = select_rs.getDouble("pitchrange_max");
	    		pitchrange_Info.add(mean);
	    		pitchrange_Info.add(dev);
	    		pitchrange_Info.add(pitchrange_min);
	    		pitchrange_Info.add(pitchrange_max);
	        }
	        select_rs.close();
	        stmt.close();
	        conn.close();
	    }catch(SQLException se){
	        se.printStackTrace();
	    }catch(Exception e){
	        // 处理 Class.forName 错误
	        e.printStackTrace();
	    }finally{
	        // 关闭资源
	        try{
	        	if(stmt!=null) stmt.close();
	        }catch(SQLException se2){
	        }// 什么都不做
	        try{
	            if(conn!=null) conn.close();
	        }catch(SQLException se){
	            se.printStackTrace();
	        }
	    }
		return pitchrange_Info;
    }
    
	public static void UpdateDatabase(String userID, double mean, double dev, double pitchrange_min, double pitchrange_max) throws IOException{
		Connection conn = null;
		Statement stmt = null;
        String update_sql;
        String select_sql;
	    try{
		    // 注册 JDBC 驱动
		    Class.forName("com.mysql.jdbc.Driver");   
		    // 打开链接
	        System.out.println("连接数据库...");
	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
	        // 执行查询
	        System.out.println(" 实例化Statement对...");
	        stmt = conn.createStatement();
	        select_sql = "SELECT * FROM users WHERE unique_id = '" + userID + "';"; //暂时为unique_id，应该是id
	        ResultSet select_rs = stmt.executeQuery(select_sql); //executeQuery()用于select
	        // 展开结果集数据库
	        select_rs.last();
	        if(select_rs.getRow() <= 0){
	        	throw new Exception("user does not exist!");
	        }else{
	        	update_sql = "UPDATE users SET mean = " + mean + ", dev = " + dev + ", pitchrange_min = " + pitchrange_min + ", pitchrange_max = " + pitchrange_max + " where unique_id = '" + userID + "';";
		        System.out.println(update_sql);
		        stmt.executeUpdate(update_sql);
	        }
	        select_rs.close();
	        stmt.close();
	        conn.close();
	    }catch(SQLException se){
	        // 处理 JDBC 错误
	        se.printStackTrace();
	    }catch(Exception e){
	        // 处理 Class.forName 错误
	        e.printStackTrace();
	    }finally{
	        // 关闭资源
	        try{
	        	if(stmt!=null) stmt.close();
	        }catch(SQLException se2){
	        }// 什么都不做
	        try{
	            if(conn!=null) conn.close();
	        }catch(SQLException se){
	            se.printStackTrace();
	        }
	    }
	}
	public static void ToneDetection(String uploadPath_root, String L2_ID, String wavname) throws IOException{
		try {
			String filename = wavname.substring(0, wavname.lastIndexOf(".wav"));
			String[] cmd = new String[4];
			cmd[0] = "sh" ; 
			cmd[1] = "/home/APP/kaldi-master/egs/tedlium/s5_r2_wsj/decode_one.sh" ; 
			cmd[2] = uploadPath_root + L2_ID + "/" + filename + "/data/" + wavname; 
			cmd[3] = uploadPath_root + L2_ID + "/" + filename + "/data/" + filename + ".detection";
			Process pr = Runtime.getRuntime().exec(cmd);	
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static ArrayList<String> FindTone(String l2_Syllable) {
		ArrayList<String> Tone = new ArrayList<String>();
		ArrayList<String> Syllable = new ArrayList<String>();
		if(l2_Syllable.indexOf("X")!=-1){
			String[] stringArray_syllable = l2_Syllable.trim().split("X");
			for(int j = 0; j < stringArray_syllable.length; j++){
				// 现在创建 matcher 对象
				String tone = MathTone(stringArray_syllable[j]);
				Tone.add(tone);
				String syllalbe = MathSyllable(stringArray_syllable[j]);
				Syllable.add(syllalbe);
			}
		}else{
			String tone = MathTone(l2_Syllable);
			Tone.add(tone);
			String syllalbe = MathSyllable(l2_Syllable);
			Syllable.add(syllalbe);
		}
		return Tone;	
	}
	private static String MathTone(String toMathString) {
		// TODO Auto-generated method stub
    	String pattern = "(.+)(\\d)";
    	Pattern p = Pattern.compile(pattern);
    	// 现在创建 matcher 对象
        Matcher m = p.matcher(toMathString);
        if (m.find()) {
        	return m.group(2);
        }else{
        	return null;
        }
	}
	
	private static String MathSyllable(String toMathString) {
		// TODO Auto-generated method stub
    	String pattern = "(.+)(\\d)";
    	Pattern p = Pattern.compile(pattern);
    	// 现在创建 matcher 对象
        Matcher m = p.matcher(toMathString);
        if (m.find()) {
        	return m.group(1);
        }else{
        	return null;
        }
	}
	public static ArrayList<String> DetectionToneLine(String uploadPath_root, String L2_ID, String wavname) throws IOException {
		ArrayList<String> DetectionToneLine = new ArrayList<String>();
		String filename = wavname.substring(0, wavname.lastIndexOf(".wav"));
		File file = new File(uploadPath_root + L2_ID + "/" + filename + "/data/" + filename + ".detection");
        if(file.isFile() && file.exists()){
			try {
				InputStreamReader read;
				read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);
	            String lineTxt = null;
	            while((lineTxt = bufferedReader.readLine())!=null){
	            	DetectionToneLine.add(lineTxt);
	            }
	            bufferedReader.close();
	            read.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}        	
        }
		return DetectionToneLine;
	}
	public static ArrayList<String> GetDetectionResult(String uploadPath_root, String L2_ID, String wavname, String l2_Syllable, ArrayList<String> Tone) throws Exception {
		ArrayList<String> DetectionResult = new ArrayList<String>();
		ArrayList<String> DetectionToneLine = DetectionToneLine(uploadPath_root, L2_ID, wavname);
		String tone = "";
		double score_average = 0;
		double[] score = new double[Tone.size()];
        String[] ToneDetection = new String[Tone.size()];
		if(Tone.size() == DetectionToneLine.size()){
			for(int i = 0; i < Tone.size(); i++){
				int correct_tone_number = 0;
	        	tone = Tone.get(i);
	    		score[i] = 0;
	    		String[] stringArray_tonedetection = DetectionToneLine.get(i).split("\\s+");
	    		ToneDetection[i] = stringArray_tonedetection[0];
	        	if(DetectionToneLine.get(i).indexOf(tone)!=-1){    		
	        		for(int j = 0; j < stringArray_tonedetection.length; j++){
	        			if(stringArray_tonedetection[j].equals(tone)){
	        				correct_tone_number = j + 1;
	        				break;
	        			}
	        		}
	        		switch(correct_tone_number){
	        			case 1:
	        				score[i] = 1;
	        				ToneDetection[i] = stringArray_tonedetection[0];
	        				break;
	        			case 2:
	        				score[i] = 0.8;
	        				ToneDetection[i] = stringArray_tonedetection[1];
	        				break;
	        			case 3:	
	        				score[i] = 0.6;
	        				ToneDetection[i] = stringArray_tonedetection[2];
	        				break;
	        			case 4:	
	        				score[i] = 0;
	        				ToneDetection[i] = stringArray_tonedetection[0]; //取前三个声调，第4个匹配时认为此声调为错误
	        				break;
	        			case 5:	
	        				score[i] = 0;
	        				ToneDetection[i] = stringArray_tonedetection[0]; //取前三个声调，第5个匹配时认为此声调为错误
	        				break;
	        		}		            	
	        	}
	        	DetectionResult.add(String.valueOf(ToneDetection[i]));
        	}
			double score_sum = 0;
	        for(int k = 0; k < score.length; k++){
	        	score_sum += score[k];
	        }
	        score_average = score_sum/score.length;
	        DetectionResult.add(String.valueOf(score_average));
		}else {
			throw new Exception("Please check the upload data!");	
		}
		return DetectionResult;
	}
	public static ArrayList<String> DetectionTone(ArrayList<String> DetectionResult) throws Exception {
		ArrayList<String> DetectionTone = new ArrayList<String>();
		for(int i = 0; i < DetectionResult.size() - 1; i++){
			DetectionTone.add(DetectionResult.get(i));
		}
		return DetectionTone;
	}
	public static double GetDetectScore(ArrayList<String> DetectionResult) throws Exception {
		double score = Double.parseDouble(DetectionResult.get(DetectionResult.size()-1));
		return score;
	}

	public static String CompareTone(ArrayList<String> correctTone, ArrayList<String> detectionTone) throws Exception {
		// TODO Auto-generated method stub
		String correctToneFlag = ""; //0错误 1正确
		if(correctTone.size() == detectionTone.size()){
			for(int i = 0; i < correctTone.size(); i++){
				if (correctTone.get(i).equals(detectionTone.get(i))){
					correctToneFlag += "1 ";
				}else {
					correctToneFlag += "0 ";
				}
			}
		}else {
			throw new Exception("Please check the upload data!");	
		}
		return correctToneFlag;
	}

	public static int GetFeedBackFlag(String toneResultFlag) {
		// TODO Auto-generated method stub
		String[] stringArray_toneResultFlag = toneResultFlag.split("\\s+");
		int feedbackFlag = 0;
		for(int i = 0; i < stringArray_toneResultFlag.length; i++){
			if(stringArray_toneResultFlag[i].equals("0")){
				feedbackFlag = 0;
				break;
			}else {
				feedbackFlag = 1;
			}
		}
		return feedbackFlag;
	}
}
