package com.APP.ComputePitchRange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ComputePitchRange_MeanDev {
	public static ArrayList<Double> PitchRange_MeanDev(String root_dir, String L2_ID){
		String L2F0_dir = root_dir + "/" + L2_ID +"/GetF0/"; //路径	
		double mean = 0;
		double dev = 0;
		String L2_f0_line = null;
		double pitchrange_min = 0;
		double pitchrange_max = 0;
		
		ArrayList<Double> F0 = new ArrayList<Double>();
    	File L2_f0_file = new File(L2F0_dir);
    	String file[] = L2_f0_file.list();	
    	for(int i = 0; i < file.length; i++){ 
    		try {
				BufferedReader L2_f0 = new BufferedReader(new FileReader(L2F0_dir+"/"+file[i]));
				try {
					while((L2_f0_line = L2_f0.readLine())!=null){
						String[] stringArray_f0 = L2_f0_line.toString().trim().split("\\s+");
						for(int j = 0; j < stringArray_f0.length; j++){
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
    	double[] mean_dev = Compute_MeanDev(F0);
    	double[] pitchrange = Compute_PitchRange(F0);
  
		mean = mean_dev[0];
		dev = mean_dev[1];
		pitchrange_min = pitchrange[0];
		pitchrange_max = pitchrange[1];
    	
		ArrayList<Double> pitchrange_Info = new ArrayList<Double>();
		pitchrange_Info.add(mean);
		pitchrange_Info.add(dev);
		pitchrange_Info.add(pitchrange_min);
		pitchrange_Info.add(pitchrange_max);
    	
    	return pitchrange_Info;
	}
	
	public static double[] Compute_MeanDev(ArrayList<Double> F0){//选择排序算法 
		double mean = 0;
		double dev = 0;
		double sumf0 = 0;
		double sumf0_dev = 0;
		for(int k = 0; k < F0.size(); k++){
    		sumf0 += F0.get(k);
    	}
    	mean = sumf0/F0.size();
    	
    	for(int k = 0; k < F0.size(); k++){
    		sumf0_dev += (F0.get(k) - mean) * (F0.get(k) - mean); 
    	}	
    	dev = Math.sqrt(sumf0_dev/(F0.size()-1));
    	return new double[]{(double) mean, (double) dev};
	}
	
	public static double[] Compute_PitchRange(ArrayList<Double> F0){//选择排序算法 
		double pitchrange_min = 0;
		double pitchrange_max = 0;
		int min_number = 0;
		int max_number = F0.size();
		ArrayList<Double> sortF0 = selectSort(F0);
		
		min_number = (int) Math.ceil(sortF0.size()*0.05);
		max_number = (int) Math.floor(sortF0.size()-sortF0.size()*0.05)-1;
		
		pitchrange_min = sortF0.get(min_number);
		pitchrange_max = sortF0.get(max_number);
//		System.out.println(pitchrange_min + " + "+ pitchrange_max);
		return new double[]{(double) pitchrange_min, (double) pitchrange_max};
	}
	
	public static ArrayList<Double> selectSort(ArrayList<Double> F0){//选择排序算法   
        for (int i = 0; i < F0.size()-1; i++ ){   
        	int min = i;   
            for (int j = i + 1; j < F0.size(); j++ ){   
            	if (F0.get(min) > F0.get(j)){   
            		min = j;   
                }
            }   
            if (min != i){  
            	double temp = F0.get(i);   
            	F0.set(i, F0.get(min));
            	F0.set(min, temp);  
            }  
        }  
        return F0;  
	}
}
