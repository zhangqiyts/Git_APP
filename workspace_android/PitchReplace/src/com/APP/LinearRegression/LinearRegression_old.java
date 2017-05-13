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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
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
public class LinearRegression_old {  
	String Path = null;
	public LinearRegression_old(String Path){
		this.Path = Path;
	}
	public void MakeL2Zscore(String uploadPath, String Chinese_ID, String L2_ID) throws IOException{
		try {
			String[] cmd = new String[7];
			cmd[0] = "perl" ; 
			cmd[1] = "D:/home/APP/workspace_perl/Regression/MakeL2RegressionCoff.pl" ; 
			cmd[2] = uploadPath; 
			/**均值和方差需查数据库获得**/
			cmd[3] = "241.02"; //mean
			cmd[4] = "46.31"; //dev 查询数据库获得
			
			cmd[5] = Chinese_ID;
			cmd[6] = L2_ID;
			System.out.println(uploadPath + Chinese_ID + L2_ID);
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
	public void PrepareForRegression(String uploadPath, String Chinese_ID, String L2_ID) throws FileNotFoundException, IOException { 
		String duration = null;
		String syllable_start = null;
		String syllable_end = null;
		String final_start = null;
		String final_end = null;
		
		String root_dir = uploadPath;
    	String regression_dir = root_dir + L2_ID +"/Regression/"; //路径
    	String result_dir = root_dir + L2_ID +"/result/"; //路径
    	String cofeslope_dir = root_dir + L2_ID + "/result/"; //路径
	    File cofeslope_dirname = new File(cofeslope_dir);
	    cofeslope_dirname.mkdirs();// 创建目录   
	    String cofeslope_file_name = null;
	       
    	File L2_file = new File(regression_dir);
    	String file[] = L2_file.list();	
		
    	for(int i = 0; i < file.length; i++){   
			try {
//				String fileName = file[i].substring(0, file[i].lastIndexOf(".txt"));
    			if(file[i].endsWith("_TimeF0")){
    				String fileName = file[i].substring(0, file[i].lastIndexOf("_TimeF0"));
    				System.out.println(fileName);
    				String fileName_time = fileName + "_regression_time";
    				BufferedReader br_time = new BufferedReader(new FileReader(regression_dir+"/"+fileName_time));
    				String s_time = null;
    		        cofeslope_file_name =  fileName + "_cofeslope";
    		        File cofeslope_file = new File(cofeslope_dir + cofeslope_file_name);
    			    FileOutputStream fos_cofeslope = new FileOutputStream(cofeslope_file);
    				OutputStreamWriter writer = new OutputStreamWriter(fos_cofeslope, "UTF-8");
    				
    				BufferedReader br_timef0 = new BufferedReader(new FileReader(regression_dir+"/"+file[i]));
    				String s_timef0 = null;
    				String timef0file_syllable = null;
    	    		String[] nameArray = file[i].toString().trim().split("\\_"); //二语者命名为L2_用户名_音节声调_等等 
    	    		if(nameArray.length == 3){ //母语者命名
    	    			timef0file_syllable =nameArray[1];
    	    		}
    	        	if(nameArray.length == 5){ //二语者命名
    	        		timef0file_syllable =nameArray[2];
    	        	}
    	        	int count = 0;
    	    		while((s_time = br_time.readLine())!=null){
    	    			String[] stringArray_time = s_time.toString().trim().split("\\s+");
    	    			duration = stringArray_time[0];
    	    			syllable_start = stringArray_time[1]; //音节时长起点
    	    			syllable_end = stringArray_time[2]; 
    	    			final_start = stringArray_time[3]; //韵母时长起点
    	    			final_end = stringArray_time[4]; 

        	        	if(timef0file_syllable.indexOf("X")!=-1){
        	        		String[] stringArray_syllable = timef0file_syllable.toString().trim().split("X");
    	        			if(stringArray_syllable[count].indexOf("3") != -1) {
    	        	    		RegressionLine line1 = new RegressionLine();
    	        	    		RegressionLine line2 = new RegressionLine();
    	        	    		ArrayList<Float> F0 = new ArrayList<Float>();
    	        	    		String s_getminf0= br_timef0.readLine();
    	        	    		float minF0_time = 0;
    	        	    		int row = 0;

    	        				while((s_timef0 = br_timef0.readLine())!=null){//使用readLine方法，一次读一行
    	        					if(s_timef0.indexOf("***")!=-1){
    	        						break;
    	        					}else{
        	        	    			String[] s_getminf0_stringArray = s_getminf0.toString().trim().split("\\s+");
        	        	    			F0.add(Float.valueOf(s_getminf0_stringArray[1]));
        	        	    			int minF0_index = GetMinF0(F0);
        	        				    String[] timef0_stringArray = s_timef0.toString().trim().split("\\s+");
        	        				    if(timef0_stringArray.length == 2){
        	        						float time = Float.valueOf(timef0_stringArray[0]);
        	        					    float f0 = Float.valueOf(timef0_stringArray[1]);
        	        						    
        	        					    if(row < minF0_index){
        	        					    	line1.addDataPoint(new DataPoint(time, f0));
        	        					    }else if(row > minF0_index){
        	        					    	line2.addDataPoint(new DataPoint(time, f0));
        	        					    }else{
        	        					    	minF0_time = time;
        	        					    	line1.addDataPoint(new DataPoint(time, f0));
        	        					    	line2.addDataPoint(new DataPoint(time, f0));
        	        					    }
        	        	                }else{
        	        	                   	throw new Exception("Please check the data time and f0!"); 
        	        	                }
        	        	    			row++;
    	        					}
    	        	    		}
    	        				String.format("%.2f", line1.getA1()).toString();
    	        				writer.append(String.format("%.2f", line1.getA1()).toString() +",");
    	        				writer.append(String.format("%.2f", line1.getA0()).toString()+",");
    	        				writer.append(syllable_start+",");
    	        				writer.append(Float.toString(minF0_time)+",");
//    	        				writer.append("\r\n");
    	        				writer.append(String.format("%.2f", line2.getA1()).toString()+",");
    	        				writer.append(String.format("%.2f", line2.getA0()).toString()+",");
    	        				writer.append(Float.toString(minF0_time)+",");
    	        				writer.append(syllable_end);
    	        				writer.append("\r\n");
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
        	        						System.out.println("file:"+ file[i]+ "minF0_index:" + time + f0);
        	        						line.addDataPoint(new DataPoint(time, f0));
        	        	                }else{
        	        	                    throw new Exception("Please check the data time and f0!"); 
        	        	                }
    	        					}
    	        				}		
    	        	            printSums(line);  
    	        				writer.append(String.format("%.2f", line.getA1()).toString()+",");
    	        				writer.append(String.format("%.2f", line.getA0()).toString()+",");
    	        				writer.append(syllable_start+",");
    	        				writer.append(syllable_end);
    	        				writer.append("\r\n");
    	        	    	}
        	        	}else{
        	        		//一个音节
        	        		if(nameArray[2].indexOf("3") != -1) {
        	        			RegressionLine line1 = new RegressionLine();
    	        	    		RegressionLine line2 = new RegressionLine();
    	        	    		ArrayList<Float> F0 = new ArrayList<Float>();
    	        	    		String s_getminf0= br_timef0.readLine();
    	        	    		float minF0_time = 0;
    	        	    		int row = 0;

    	        				while((s_timef0 = br_timef0.readLine())!=null){//使用readLine方法，一次读一行
    	        					if(s_timef0.indexOf("***")!=-1){
    	        						break;
    	        					}else{
        	        	    			String[] s_getminf0_stringArray = s_getminf0.toString().trim().split("\\s+");
        	        	    			F0.add(Float.valueOf(s_getminf0_stringArray[1]));
        	        	    			int minF0_index = GetMinF0(F0);
        	        					String[] timef0_stringArray = s_timef0.toString().trim().split("\\s+");
        	        					if(timef0_stringArray.length == 2){
        	        						float time = Float.valueOf(timef0_stringArray[0]);
        	        						float f0 = Float.valueOf(timef0_stringArray[1]);
        	        						    
        	        						if(row < minF0_index){
        	        						    line1.addDataPoint(new DataPoint(time, f0));
        	        						}else if(row > minF0_index){
        	        						    line2.addDataPoint(new DataPoint(time, f0));
        	        						}else{
        	        						    minF0_time = time;
        	        						    line1.addDataPoint(new DataPoint(time, f0));
        	        						    line2.addDataPoint(new DataPoint(time, f0));
        	        						}
        	        	                }else{
        	        	                    throw new Exception("Please check the data time and f0!"); 
        	        	                }
        	        	    			row++;
    	        					}
    	        	    		}
    	        					
    	        				String.format("%.2f", line1.getA1()).toString();
    	        				writer.append(String.format("%.2f", line1.getA1()).toString() +",");
    	        				writer.append(String.format("%.2f", line1.getA0()).toString()+",");
    	        				writer.append(syllable_start+",");
    	        				writer.append(Float.toString(minF0_time)+",");
    	        				writer.append(String.format("%.2f", line2.getA1()).toString()+",");
    	        				writer.append(String.format("%.2f", line2.getA0()).toString()+",");
    	        				writer.append(Float.toString(minF0_time)+",");
    	        				writer.append(syllable_end);
    	        				writer.append("\r\n");

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
        	        						System.out.println("file:"+ file[i]+ "minF0_index:" + time + f0);
        	        						line.addDataPoint(new DataPoint(time, f0));
        	        	                }else{
        	        	                    throw new Exception("Please check the data time and f0!"); 
        	        	                }
    	        					}
    	        				}		
    	        	            printSums(line);  
    	        				writer.append(String.format("%.2f", line.getA1()).toString()+",");
    	        				writer.append(String.format("%.2f", line.getA0()).toString()+",");
    	        				writer.append(syllable_start+",");
    	        				writer.append(syllable_end);
    	        	    	}
        	        	}
        	        	count++;
    	    		}
					writer.append("\r\n");
					writer.close();
					br_timef0.close(); 
					fos_cofeslope.close();
    			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} 
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

	/** 
     * Print the computed sums. 
     *  
     * @param line 
     *            the regression line 
     */  
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
        //System.out.println("误差：     R^2 = " + line.getR());  
    }     
}  