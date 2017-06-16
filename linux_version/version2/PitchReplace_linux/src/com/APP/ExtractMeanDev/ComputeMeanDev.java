package com.APP.ExtractMeanDev;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ComputeMeanDev {
	public double[] MeanDev(String root_dir, String L2_ID){
		String[] L2_nameArray = L2_ID.toString().trim().split("\\_"); 
		String L2_name = L2_nameArray[0] + "_" + L2_nameArray[1];
		String L2F0_dir = root_dir + "/" + L2_name +"/Get10F0/"; //路径
		double mean = 0;
		double dev = 0;
		String L2_f0_line = null;
		double sumf0 = 0;
		double sumf0_dev = 0;
		int count = 0;
		ArrayList<Double> F0 = new ArrayList<Double>();
		
    	File L2_f0_file = new File(L2F0_dir);
    	String file[] = L2_f0_file.list();	
    	for(int i = 0; i < file.length; i++){ 
    		try {
				BufferedReader L2_f0 = new BufferedReader(new FileReader(L2F0_dir+"/"+file[i]));
				try {
					while((L2_f0_line = L2_f0.readLine())!=null){
						String[] stringArray_f0 = L2_f0_line.toString().trim().split("\\s+");
						for(int j = 1; j < stringArray_f0.length; j++){
							F0.add(Double.parseDouble(stringArray_f0[j]));
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	for(int k = 0; k < F0.size(); k++){
    		sumf0 += F0.get(k);
    	}
    	mean = sumf0/F0.size();
    	
    	for(int k = 0; k < F0.size(); k++){
    		sumf0_dev += (F0.get(k) - mean) * (F0.get(k) - mean); 
    	}	
    	count = F0.size();
    	dev = Math.sqrt(sumf0_dev/(F0.size()-1));
//    	System.out.println("count:"+ count + "mean:" + mean + "dev:" + dev);
//    	System.out.println("sumf0:" + sumf0 + "sumf0_dev:" + sumf0_dev);
    	return new double[]{(double) mean, (double) dev};
	}
}
