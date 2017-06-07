package com.APP.MakeTextGrid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakeTextGrid {
	String PitchReplacePath = null;
	public MakeTextGrid(String Path){
		this.PitchReplacePath = Path;
	}
	public static void MakeScp(String pitchReplacePath) throws IOException {
		String scp_dirname = pitchReplacePath + "/scp/"; //路径
	    File scp_dir = new File(scp_dirname);
	    // 现在创建目录
	    scp_dir.mkdirs();
		String wav_dirname = pitchReplacePath + "/data/"; //路径
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
	
	public static void MakeMlf(String pitchReplacePath) throws IOException {
		String syllable = null;
		String mlf_dirname = pitchReplacePath + "/mlf/"; //路径
	    File mlf_dir = new File(mlf_dirname);
	    // 现在创建目录
	    mlf_dir.mkdirs();
		String scp_dirname = pitchReplacePath + "/scp/"; //路径
		File scp_dir = new File(scp_dirname);
		String scp[] = scp_dir.list();
		for (int i=0; i < scp.length; i++) { 
			String name_mlf = scp[i].substring(0, scp[i].lastIndexOf(".scp"));
			File file_mlf = new File(mlf_dirname + name_mlf + ".mlf");
    	    FileOutputStream fos_mlf = new FileOutputStream(file_mlf);
			OutputStreamWriter writer = new OutputStreamWriter(fos_mlf, "UTF-8");
			writer.append("#!MLF!#");
			writer.append("\r\n");
			writer.append("\"*/" + name_mlf + ".lab\"");
			writer.append("\r\n");
			
        	String[] stringArray_scp = scp[i].toString().trim().split("\\_");
//        	System.out.println(stringArray_scp[2]);
        	if(stringArray_scp[2].indexOf("X")!=-1){
    			String[] stringArray_syllable = stringArray_scp[2].toString().trim().split("X");
    			for(int j = 0; j < stringArray_syllable.length; j++){
    				syllable = MathString(stringArray_syllable[j]);
    				writer.append(syllable);
    				writer.append("\r\n");
    			}
    		}else{
    			syllable = MathString(stringArray_scp[2]);
//    			System.out.println("****"+syllable);
				writer.append(syllable);
				writer.append("\r\n");
    		}
			writer.append(".");
			writer.append("\r\n");
			writer.close();
			fos_mlf.close();
		}	
	}
	
	private static String MathString(String toMathString) {
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
	public static void MakeAlignTextGrid(String pitchReplacePath) throws IOException {
		try {	
			String[] cmd = { "perl", "D:/home/APP/workspace_perl/MakeTextGrid/mkAlignTextGrid.pl", pitchReplacePath};
			Process pr = Runtime.getRuntime().exec(cmd);
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public static void MakeOutTextGrid(String pitchReplacePath) throws IOException{
		try {
			String[] cmd = { "perl", "D:/home/APP/workspace_perl/MakeTextGrid/multiCreateTextGrid.pl", pitchReplacePath};
			Process pr = Runtime.getRuntime().exec(cmd);	
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
}

