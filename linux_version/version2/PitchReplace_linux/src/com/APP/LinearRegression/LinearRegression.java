/* 
 * File        : DataPoint.java 
 * Author      : zhouyujie 
 * Date        : 2012-01-11 16:00:00 
 * Description : Java实现一元线性回归的算法，线性回归测试类，(可实现统计指标的预测) 
 */  
package com.APP.LinearRegression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import com.APP.LinearRegression.RegressionLine;
import com.APP.LinearRegression.DataPoint;

/** 
 *Linear Regression
 * Demonstrate linear regression by constructing the regression line for a set of data points. 
 * require DataPoint.java,RegressionLine.java 
 * 为了计算对于给定数据点的最小方差回线，需要计算SumX,SumY,SumXX,SumXY; (注：SumXX = Sum (X^2)) 
 * 回归直线方程如下： f(x)=a1x+a0 
 * 斜率和截距的计算公式如下：
 * n: 数据点个数 
 * a1=(n(SumXY)-SumX*SumY)/(n*SumXX-(SumX)^2) 
 * a0=(SumY - SumY * a1)/n
 * (也可表达为a0=averageY-a1*averageX) 
 * 画线的原理：两点成一直线，只要能确定两个点即可
 * 第一点：(0,a0) 再随意取一个x1值代入方程，取得y1，连结(0,a0)和(x1,y1)两点即可。 
 * 为了让线穿过整个图,x1可以取横坐标的最大值Xmax，即两点为(0,a0),(Xmax,Y)。如果y=a1*Xmax+a0,y大于 
 * 纵坐标最大值Ymax，则不用这个点。改用y取最大值Ymax，算得此时x的值，使用(X,Ymax)， 即两点为(0,a0),(X,Ymax) 
 */  
public class LinearRegression {  
	String Path = null;
	public LinearRegression(String Path){
		this.Path = Path;
	}
	public static void MakeL2Zscore(String uploadPath_root, double mean, double dev, String Chinese_filename, String L2_ID, double pitchrange_min, double pitchrange_max) throws IOException{
		try {
			String[] cmd = new String[9];
			cmd[0] = "perl" ; 
			cmd[1] = "/home/APP/workspace_perl/Regression/MakeL2RegressionCoff.pl" ; 
			cmd[2] = uploadPath_root; 
			/**均值和方差需查数据库获得**/
			cmd[3] = String.format("%.2f", mean).toString(); //mean
			cmd[4] = String.format("%.2f", dev).toString(); //dev 查询数据库获得	
			cmd[5] = Chinese_filename;
			cmd[6] = L2_ID;
			cmd[7] = String.format("%.3f", pitchrange_min).toString(); //pitchrange_min;
			cmd[8] = String.format("%.3f", pitchrange_max).toString(); 
//			System.out.println(uploadPath + Chinese_ID + L2_ID);
			Process pr = Runtime.getRuntime().exec(cmd);		
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static String[] PrepareForRegression(String uploadPath, String Chinese_filename, String L2_filename) throws FileNotFoundException, IOException { 
		String duration = null;
		String syllable_start = null;
		String syllable_end = null;
		String final_start = null;
		String final_end = null;
		
		String syllable_duration_L2 = null;
		String final_duration_L2 = null;		
		double time_start = 0;
		double time_end = 0;
		
    	String L2_Time="";
    	String L2_F0="";
    	String CH_Time="";
    	String CH_F0="";
    	
		String root_dir = uploadPath;
    	String regression_dir = root_dir + L2_filename +"/Regression/"; //路径
    	
    	File L2_file = new File(regression_dir);
    	String file[] = L2_file.list();	
    	
    	for(int i = 0; i < file.length; i++){   
			try {
//				String fileName = file[i].substring(0, file[i].lastIndexOf(".txt"));
    			if(file[i].endsWith("_FiveDegree")){ 

    				String fileName = file[i].substring(0, file[i].lastIndexOf("_FiveDegree"));
    				String fileName_time = fileName + "_regression_time";
    				BufferedReader br_time = new BufferedReader(new FileReader(regression_dir+"/"+fileName_time));
    				String s_time = null;
    				BufferedReader br_timef0 = new BufferedReader(new FileReader(regression_dir+"/"+file[i]));
    				String s_timef0 = null;
    				String timef0file_syllable = null;
    	    		String[] nameArray = file[i].toString().trim().split("\\_"); //二语者命名为L2_用户名_音节声调_等等 
    	    		if(nameArray.length == 3){ //母语者命名:F2_an1_TimeF0
    	    			timef0file_syllable = nameArray[1];
    	    		}
    	        	if(nameArray.length == 4){ //二语者命名:L2F_用户名_an1_time编号_TimeF0
    	        		timef0file_syllable = nameArray[2];
    	        	}
    	        	
    	        	String[] stringArray_syllable = timef0file_syllable.toString().trim().split("X");
    	        	int count = 0;
    	    		while((s_time = br_time.readLine())!=null){ //每一行为一个音节的时间
    	    			String[] stringArray_time = s_time.toString().trim().split("\\s+");
    	    			duration = stringArray_time[0];
    	    			syllable_start = stringArray_time[1]; //音节时长起点
    	    			syllable_end = stringArray_time[2]; 
    	    			final_start = stringArray_time[3]; //韵母时长起点
    	    			final_end = stringArray_time[4]; 
    	    			
    	    			time_start = Float.valueOf(final_start); //取韵母时间
    	    			time_end = Float.valueOf(final_end);

    	    			if(stringArray_syllable[count].indexOf("3") != -1) { 
	        	    		ArrayList<Float> F0 = new ArrayList<Float>();
	        	    		ArrayList<Float> Time = new ArrayList<Float>();
	        	    		int row = 0;
	        				while((s_timef0 = br_timef0.readLine())!=null){//使用readLine方法，一次读一行
	        					if(s_timef0.indexOf("***")!=-1){
	        						break;
	        					}else{
    	        	    			String[] timef0_stringArray = s_timef0.toString().trim().split("\\s+");
    	        				    if(timef0_stringArray.length == 2){

        	        	    			Time.add(Float.valueOf(timef0_stringArray[0]));
        	        	    			F0.add(Float.valueOf(timef0_stringArray[1]));
        	        	    			
    	        					 
    	        	                }else{
    	        	                   	throw new Exception("Please check the data time and f0!"); 
    	        	                }
    	        	    			row++;
	        					}
	        	    		}
	        				CurveFitting curvefitting = new CurveFitting();
	        				double []coff = curvefitting.compute(row, F0, Time);
	        				
	        				double min_time_final = -1*coff[1]/(2*coff[2]);	        				
	        				double min_time_ratio = (min_time_final-Double.valueOf(final_start))/(Double.valueOf(final_end)-Double.valueOf(syllable_start)); 	        				
	        				
	        				double f0_final_s = coff[2]*Double.valueOf(final_start)*Double.valueOf(final_start) + coff[1]*Double.valueOf(final_start) + coff[0]; 
	        				double f0_final_e = coff[2]*Double.valueOf(final_end)*Double.valueOf(final_end) + coff[1]*Double.valueOf(final_end) + coff[0]; 
	        				double min_f0_final = min_time_final*min_time_final*coff[2] + coff[1]*min_time_final + coff[0];        				
	        				
	        				double time_s = 0.1*count + time_start;     				
//	        				double f0_s = coff[2]*time_s*time_s + coff[1]*time_s + coff[0];
	        				double f0_s = f0_final_s;
	        				if(f0_s <= 0){
	        					f0_s = 0;
	        				}else if(f0_s >= 5){
	        					f0_s = 5;
	        				}

	        				double time_e = 0.1*count + time_end;
//	        				double f0_e = coff[2]*time_e*time_e + coff[1]*time_e + coff[0];
	        				double f0_e = f0_final_e;
	        				if(f0_e <= 0){
	        					f0_e = 0;
	        				}else if(f0_e >= 5){
	        					f0_e = 5;
	        				}
	        				
	        				double min_time = 0.1*count + time_start+(time_end-time_start)*min_time_ratio;
	        				double min_f0 = min_f0_final;
	        				if(min_f0 <= 0){
	        					min_f0 = 0;
	        				}else if(min_f0 >= 5){
	        					min_f0 = 5;
	        				}
	        				
	        				if(file[i].startsWith("L2")){
	        					L2_Time = L2_Time + String.format("%.2f", time_s).toString() + " " + String.format("%.2f", min_time).toString() + " " + String.format("%.2f", time_e).toString()+ ";";
	        					L2_F0 = L2_F0 + String.format("%.2f", f0_s).toString() + " " + String.format("%.2f", min_f0).toString() + " " + String.format("%.2f", f0_e).toString()+ ";";
	        				}else{
	        					CH_Time = CH_Time + String.format("%.2f", time_s).toString() + " " + String.format("%.2f", min_time).toString() + " " + String.format("%.2f", time_e).toString()+ ";";
	        					CH_F0 = CH_F0 + String.format("%.2f", f0_s).toString() + " " + String.format("%.2f", min_f0).toString() + " " + String.format("%.2f", f0_e).toString()+ ";";
	        				}

	        	    	}else{
	        	    		RegressionLine line = new RegressionLine();
	        				while((s_timef0 = br_timef0.readLine())!=null){//使用readLine方法，一次读一行
	         					if(s_timef0.indexOf("***")!=-1){
	        						break;
	        					}else{
    	        					String[] timef0_stringArray = s_timef0.toString().trim().split("\\s+");
    	        					if(timef0_stringArray.length == 2){
    	        						float time = Float.valueOf(timef0_stringArray[0]);
    	        						float f0 = Float.valueOf(timef0_stringArray[1]);
    	        						line.addDataPoint(new DataPoint(time, f0));
//    	        						System.out.println("CH_Time"+ time + "CH_F0"+ f0);
    	        	                }else{
    	        	                    throw new Exception("Please check the data time and f0!"); 
    	        	                }
	        					}
	        				}		
	        				
	        				double coff = line.getA1();
	        				double offset = line.getA0();
	        				double time_s = 0.1*count + time_start;
	        				double f0_s = coff*time_s + offset;
	        				
	        				double time_e = 0.1*count + time_end;	
	        				double f0_e = coff*time_e + offset;
	        				
	        				if(f0_s <= 0){
	        					f0_s = 0;
	        				}else if(f0_s >= 5){
	        					f0_s = 5;
	        				}
	        				
	        				if(f0_e <= 0){
	        					f0_e = 0;
	        				}else if(f0_e >= 5){
	        					f0_e = 5;
	        				}
	        				
	        				if(file[i].startsWith("L2")){
	        					L2_Time = L2_Time + String.format("%.2f", time_s).toString() + " " + String.format("%.2f", time_e).toString()+ ";";
	        					L2_F0 = L2_F0 + String.format("%.2f", f0_s).toString() + " " + String.format("%.2f", f0_e).toString()+ ";";
	        				}else{
	        					CH_Time = CH_Time + String.format("%.2f", time_s).toString() + " " + String.format("%.2f", time_e).toString()+ ";";
	        					CH_F0 = CH_F0 + String.format("%.2f", f0_s).toString() + " " + String.format("%.2f", f0_e).toString()+ ";";
	        				}
	        	    	}
        	        	count++;
    	    		}
					br_timef0.close();
    			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	double L2_time_start = 0;
    	String[] CH_time_stringArray = CH_Time.split("\\;");
    	String[] L2_time_stringArray = L2_Time.split("\\;");
    	double L2_CH_duration = 0;
    	double CH_time_each = 0;
    	String CH_time_string = "";
    	for(int k = 0; k < CH_time_stringArray.length; k++){
    		String[] CH_time_st = CH_time_stringArray[k].split("\\s+");
    		String[] L2_time_st = L2_time_stringArray[k].split("\\s+");
    		L2_CH_duration = Double.valueOf(L2_time_st[0]) - Double.valueOf(CH_time_st[0])-L2_time_start;
    		
    		for(int j = 0; j < CH_time_st.length; j++){
    			CH_time_each = Double.valueOf(CH_time_st[j])+L2_CH_duration;
    			if(j == CH_time_st.length -1){
    				CH_time_string = CH_time_string + String.format("%.2f", CH_time_each).toString();
    			}else{
    				CH_time_string = CH_time_string + String.format("%.2f", CH_time_each).toString() + " ";
    			}	
    		}
    		CH_time_string = CH_time_string + ";";
    	}
    	CH_Time = CH_time_string;
		return new String[]{CH_Time, CH_F0, L2_Time, L2_F0};
    }
	private static int GetMinF0(ArrayList<Float> f0) {
    	ArrayList<Float> F0= f0;
    	float min =F0.get(0);
    	int index = 0;
    	for(int i = 0; i < F0.size(); i++){
	    	if(F0.get(i) < min) {
	    		min = F0.get(i); // 判断最小值
	    		index = i; 
	    	}
	    }
		return index;
	}
	
    private static void printSums(RegressionLine line) {  
        System.out.println("\n数据点个数 n = " + line.getDataPointCount());  
        System.out.println("\nSum x  = " + line.getSumX());  
        System.out.println("Sum y  = " + line.getSumY());  
        System.out.println("Sum xx = " + line.getSumXX());  
        System.out.println("Sum xy = " + line.getSumXY());  
        System.out.println("Sum yy = " + line.getSumYY());  
    }   
    private static void printLine(RegressionLine line) {  
        System.out.println("\n回归线公式:  y = " + line.getA1() + "x + " + line.getA0());   
    }     
}  