package com.APP.PitchReplace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PitchReplace {
	String PitchReplacePath = null;
	public PitchReplace(String Path){
		this.PitchReplacePath = Path;
	}
	public void ExtractPitchTierToF0() throws IOException{	
		String REGEX = "/";
		String INPUT = PitchReplacePath;
		String REPLACE = "\\\\";
		Pattern p = Pattern.compile(REGEX);
		// 获取 matcher 对象
		Matcher m = p.matcher(INPUT);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			m.appendReplacement(sb,REPLACE);
		}
		m.appendTail(sb);
		
		String dirPath = sb.toString();
		dirPath = dirPath + "\\data\\";	
		try {
			String[] cmd = new String[4];
			cmd[0] = "D:\\usr\\praat\\Praat.exe" ; 
			cmd[1] = "--run" ; 
			cmd[2] = "D:\\home\\APP\\workspace_praat\\GetPitchTierF0.scp" ; 
			cmd[3] = dirPath; 
//			System.out.println(cmd);
			Process pr = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	    	String line;
	    	while ((line = in.readLine()) != null) {
	    		System.out.println(line);
	    	}
	    	in.close();			
			pr.waitFor();
//			System.out.println("end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	public void ExtractTimeF0(String uploadPath_root, String L2_ID) throws IOException{
		try {
			String[] cmd = new String[4];
			cmd[0] = "perl" ; 
			cmd[1] = "D:/home/APP/workspace_perl/PitchReplace/GetTimeF0.pl" ; 
			cmd[2] = uploadPath_root; 
			cmd[3] = L2_ID;
			Process pr = Runtime.getRuntime().exec(cmd);	
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void PitchReplace_final(String uploadPath_root, double mean, double dev, String Chinese_ID, String L2_ID) throws IOException{
		try {
			String[] cmd = new String[7];
			cmd[0] = "perl" ; 
			cmd[1] = "D:/home/APP/workspace_perl/PitchReplace/PitchReplace.pl" ; 
			cmd[2] = uploadPath_root; 
			/**均值和方差需查数据库获得**/
			cmd[3] = String.format("%.2f", mean).toString(); //mean
			cmd[4] = String.format("%.2f", dev).toString(); //dev 查询数据库获得
			cmd[5] = Chinese_ID;
			cmd[6] = L2_ID;
			Process pr = Runtime.getRuntime().exec(cmd);		
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
