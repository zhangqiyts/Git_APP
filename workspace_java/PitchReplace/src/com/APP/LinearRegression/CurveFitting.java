package com.APP.LinearRegression;

import java.util.ArrayList;

public class CurveFitting {
	private double[][] a;
	private double[] b;
	private int n = 3;
	private double x[];
	private ArrayList<Float> Time;
	private ArrayList<Float> F0;
	private int Row = 0;
	
	public float[] compute(int row, ArrayList<Float> f0, ArrayList<Float> time){
		Row = row;
		Time = time; 
		F0 = f0;
		a=new double[Row][n];
		for(int i=0;i<Row;i++){
			for(int j=0;j<n;j++){
				a[i][j]=Math.pow(Time.get(i),j);
			}
		}
		x=new double[n];
		b=new double[Row];
		for(int i=0;i<Row;i++){
			b[i]=F0.get(i);
		}
		double[] v=new double[Row];
		double arf=0;
		double bat=0;
		double gama=0;
		for(int k=0;k<n;k++){
			arf=0;
			bat=0;
			gama=0;
			for(int i=k;i<Row;i++){
				arf+=Math.pow(a[i][k],2);
			}
			arf=-(Math.abs(a[k][k])/a[k][k])*Math.sqrt(arf);
			for(int i=0;i<k;i++){
				v[i]=0;
			}
			for(int i=k;i<Row;i++){
				v[i]=a[i][k];
			}
			v[k]-=arf;
			for(int i=0;i<Row;i++){
				bat+=v[i]*v[i];
			}
			if(bat==0)
				continue;
			for(int j=k;j<n;j++){
				gama=0;
				for(int tem=0;tem<Row;tem++){
					gama+=v[tem]*a[tem][j];
				}
				for(int tem=0;tem<Row;tem++){
					a[tem][j]=a[tem][j]-(2*gama/bat)*v[tem];
				}
			}
			gama=0;
			
			for(int tem=0;tem<Row;tem++){
				gama+=v[tem]*b[tem];
			}
			for(int tem=0;tem<Row;tem++){
				b[tem]=b[tem]-(2*gama/bat)*v[tem];
			}
			
			
		}
		for(int j=n-1;j>=0;j--){
			if(a[j][j]==0){
				System.out.println("回代法  出错");
				System.exit(0);
			}
			x[j]=b[j]/a[j][j];
			for(int i=0;i<j;i++){
				b[i]=b[i]-a[i][j]*x[j];
			}
			
		}
//		for(int i=0;i<n;i++){
//			System.out.print(x[i]+" ");
//		}
		return new float[]{(float) x[0], (float) x[1], (float) x[2]};
	}
}
