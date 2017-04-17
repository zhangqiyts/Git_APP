package com.APP.MakeTextGrid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakeTextGrid {
	String PitchReplacePath = null;
	public MakeTextGrid(String Path){
		this.PitchReplacePath = Path;
	}
	public void MakeScp() throws IOException {
		String scp_dirname = PitchReplacePath + "/scp/"; //路径
	    File scp_dir = new File(scp_dirname);
	    // 现在创建目录
	    scp_dir.mkdirs();
		String wav_dirname = PitchReplacePath + "/data/"; //路径
		File wav = new File(wav_dirname);
		String wavfile[] = wav.list();
	    for (int i=0; i < wavfile.length; i++) {    	
            if(wavfile[i].endsWith(".wav") && wavfile[i].startsWith("L2")){
            	String pattern = "(.+)\\.wav$";
            	Pattern p = Pattern.compile(pattern);
            	// 现在创建 matcher 对象
                Matcher m = p.matcher(wavfile[i]);
                if (m.find()) {             	
            		File file_scp = new File(scp_dirname + m.group(1)+".scp");
            	    FileOutputStream fos_scp = new FileOutputStream(file_scp);
					OutputStreamWriter writer = new OutputStreamWriter(fos_scp, "UTF-8");
					writer.append(wav_dirname + wavfile[i]);
					writer.append("\r\n");
					writer.close();
					fos_scp.close();
                }	            	 
            }
	    }
	}
	
	public void MakeMlf() throws IOException {
		String mlf_dirname = PitchReplacePath + "/mlf/"; //路径
	    File mlf_dir = new File(mlf_dirname);
	    // 现在创建目录
	    mlf_dir.mkdirs();
		String scp_dirname = PitchReplacePath + "/scp/"; //路径
		File scp_dir = new File(scp_dirname);
		String scp[] = scp_dir.list();
		for (int i=0; i < scp.length; i++) { 	
            if(scp[i].endsWith(".scp")){
            	String pattern = "(.+)\\_(.+)\\_(.+)(\\d)\\.scp$";
            	Pattern p = Pattern.compile(pattern);
            	// 现在创建 matcher 对象
                Matcher m = p.matcher(scp[i]);
                if (m.find()) {
                	String name_mlf = m.group(1) +"_"+ m.group(2) + "_" + m.group(3) + m.group(4);
            		File file_mlf = new File(mlf_dirname + name_mlf + ".mlf");
            	    FileOutputStream fos_mlf = new FileOutputStream(file_mlf);
					OutputStreamWriter writer = new OutputStreamWriter(fos_mlf, "UTF-8");
					writer.append("#!MLF!#");
					writer.append("\r\n");
					writer.append("\"*/" + name_mlf + ".lab\"");
					writer.append("\r\n");
					writer.append(m.group(3));
					writer.append("\r\n");
					writer.append(".");
					writer.append("\r\n");
					writer.close();
					fos_mlf.close();
                }
            }
		}	
	}
	
	public void MakeAlignMlf() throws IOException {
		try {	
			String[] cmd = { "perl", "D:/home/APP/workspace_perl/MakeTextGrid/mkAlignMlf.pl", PitchReplacePath};
			Process pr = Runtime.getRuntime().exec(cmd);
//			Process pr = Runtime.getRuntime().exec("perl D:/home/APP/workspace_perl/MakeTextGrid/mkAlignMlf.pl"); //注意路径
	    	BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	    	String line;
	    	while ((line = in.readLine()) != null) {
	    		System.out.println(line);
	    	}
	    	in.close();			
			pr.waitFor();
			System.out.println("end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public void MakeOutTextGrid() throws IOException{
		try {
			String[] cmd = { "perl", "D:/home/APP/workspace_perl/MakeTextGrid/multiCreateTextGrid.pl", PitchReplacePath};
			Process pr = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	    	String line;
	    	while ((line = in.readLine()) != null) {
	    		System.out.println(line);
	    	}
	    	in.close();			
			pr.waitFor();
			System.out.println("end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
}

