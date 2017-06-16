package com.APP.PitchReplace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PitchReplace {
	String PitchReplacePath = null;
	public PitchReplace(String Path){
		this.PitchReplacePath = Path;
	}
	public void ExtractPitchTierToF0() throws IOException{	
		String dirPath = PitchReplacePath + "/data/";	
		try {
			String[] cmd = new String[4];
			cmd[0] = "/usr/praat/praat" ; 
			cmd[1] = "--run" ; 
			cmd[2] = "/home/APP/workspace_praat/GetPitchTierF0.scp" ; 
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
	public static void ExtractTimeF0(String uploadPath_root, String getF0SavePath, String L2_filename) throws IOException{
		try {
			String[] cmd = new String[5];
			cmd[0] = "perl" ; 
			cmd[1] = "/home/APP/workspace_perl/PitchReplace/GetTimeF0.pl" ; 
			cmd[2] = uploadPath_root; 
			cmd[3] = L2_filename;
			cmd[4] = getF0SavePath;
			Process pr = Runtime.getRuntime().exec(cmd);	
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void PitchReplace_final(String uploadPath_root, double mean, double dev, String Chinese_filename, String L2_filename) throws IOException{
		try {
			String[] cmd = new String[7];
			cmd[0] = "perl" ; 
			cmd[1] = "/home/APP/workspace_perl/PitchReplace/PitchReplace.pl" ; 
			cmd[2] = uploadPath_root; 
			/**均值和方差需查数据库获得**/
			cmd[3] = String.format("%.2f", mean).toString(); //mean
			cmd[4] = String.format("%.2f", dev).toString(); //dev 查询数据库获得
			cmd[5] = Chinese_filename;
			cmd[6] = L2_filename;
			Process pr = Runtime.getRuntime().exec(cmd);		
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
