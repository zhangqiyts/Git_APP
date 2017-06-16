/** 
 * File        : DataPoint.java 
 * Author      : zhangqi
 * Description : Java实现一元线性回归的算法，座标点实体类，(可实现统计指标的预测) 
 */  
package com.APP.LinearRegression;  
  
public class DataPoint {  
  
    /** the x value */  
    public float x;  
    /** the y value */  
    public float y;   
    public DataPoint(float x, float y) {  
        this.x = x;  
        this.y = y;  
    }  
    public DataPoint()
    {
    	this.x = -1;
    	this.y = -1;
    }
}  
