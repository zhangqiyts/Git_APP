/** 
 * File        : RegressionLine.java 
 * Author      : zhangqi
 * Description : Java实现一元线性回归的算法，回归线实现类，(可实现统计指标的预测) 
 */  
package com.APP.LinearRegression;  
  
import java.math.BigDecimal;  
import java.util.ArrayList;  
import com.APP.LinearRegression.DataPoint;
  
public class RegressionLine // implements Evaluatable  
{  
    /** sum of x */  
    private float sumX;  
  
    /** sum of y */  
    private float sumY;  
  
    /** sum of x*x */  
    private float sumXX;  
  
    /** sum of x*y */  
    private float sumXY;  
  
    /** sum of y*y */  
    private float sumYY;  
  
    /** sum of sumDeltaY^2 */  
    private float sumDeltaY2;  
  
    private float sst;  
  
    private float E;  
  
    private String[] xy;  
  
    private ArrayList<String> listX;  
  
    private ArrayList<String> listY;  
  
    private float XMin, XMax, YMin, YMax;  
  
    /** line coefficient a0 */  
    private float a0;  
  
    /** line coefficient a1 */  
    private float a1;  
  
    /** number of data points */  
    private int pn;  
  
    /** true if coefficients valid */  
    private boolean coefsValid;  
  
    /** 
     * Constructor. 
     */  
    public RegressionLine() {  
        XMax = 0;  
        YMax = 0;  
        pn = 0;  
        xy = new String[2];  
        listX = new ArrayList<String>();  
        listY = new ArrayList<String>();  
    }  
  
    /** 
     * Constructor. 
     *  
     * @param data 
     *            the array of data points 
     */  
    public RegressionLine(DataPoint data[]) {  
        pn = 0;  
        xy = new String[2];  
        listX = new ArrayList<String>();  
        listY = new ArrayList<String>();  
        for (int i = 0; i < data.length; ++i) {  
            addDataPoint(data[i]);  
        }  
    }  
  
    /** 
     * Return the current number of data points. 
     *  
     * @return the count 
     */  
    public int getDataPointCount() {  
        return pn;  
    }  
  
    /** 
     * Return the coefficient a0. 
     *  
     * @return the value of a0 
     */  
    public float getA0() {  
        validateCoefficients();  
        return a0;  
    }  
  
    /** 
     * Return the coefficient a1. 
     *  
     * @return the value of a1 
     */  
    public float getA1() {  
        validateCoefficients();  
        return a1;  
    }  
  
    /** 
     * Return the sum of the x values. 
     *  
     * @return the sum 
     */  
    public float getSumX() {  
        return sumX;  
    }  
  
    /** 
     * Return the sum of the y values. 
     *  
     * @return the sum 
     */  
    public float getSumY() {  
        return sumY;  
    }  
  
    /** 
     * Return the sum of the x*x values. 
     *  
     * @return the sum 
     */  
    public float getSumXX() {  
        return sumXX;  
    }  
  
    /** 
     * Return the sum of the x*y values. 
     *  
     * @return the sum 
     */  
    public float getSumXY() {  
        return sumXY;  
    }  
  
    public float getSumYY() {  
        return sumYY;  
    }  
  
    public float getXMin() {  
        return XMin;  
    }  
  
    public float getXMax() {  
        return XMax;  
    }  
  
    public float getYMin() {  
        return YMin;  
    }  
  
    public float getYMax() {  
        return YMax;  
    }  
  
    /** 
     * Add a new data point: Update the sums. 
     *  
     * @param dataPoint 
     *            the new data point 
     */  
    public void addDataPoint(DataPoint dataPoint) {  
        sumX += dataPoint.x;  
        sumY += dataPoint.y;  
        sumXX += dataPoint.x * dataPoint.x;  
        sumXY += dataPoint.x * dataPoint.y;  
        sumYY += dataPoint.y * dataPoint.y;  
  
        if (dataPoint.x > XMax) {  
            XMax = (float) dataPoint.x;  
        }  
        if (dataPoint.y > YMax) {  
            YMax = (float) dataPoint.y;  
        }  
  
        // 把每个点的具体坐标存入ArrayList中，备用  
  
        xy[0] = (float) dataPoint.x + "";  
        xy[1] = (float) dataPoint.y + "";  
//        if (dataPoint.x != 0 && dataPoint.y != 0) {  
//        }
        try {  
            // System.out.println("n:"+n);  
            listX.add(pn, xy[0]);  
            listY.add(pn, xy[1]);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        ++pn;  
        coefsValid = false;  
    }  
  
    /** 
     * Return the value of the regression line function at x. (Implementation of 
     * Evaluatable.) 
     *  
     * @param x 
     *            the value of x 
     * @return the value of the function at x 
     */  
    public float at(int x) {  
        if (pn < 2)  
            return Float.NaN;  
  
        validateCoefficients();  
        return a0 + a1 * x;  
    }  
  
    /** 
     * Reset. 
     */  
    public void reset() {  
        pn = 0;  
        sumX = sumY = sumXX = sumXY = 0;  
        coefsValid = false;  
    }  
  
    /** 
     * Validate the coefficients. 计算方程系数 y=ax+b 中的a 
     */  
    private void validateCoefficients() {  
        if (coefsValid)  
            return;  
  
        if (pn >= 2) {  
            float xBar = (float) sumX / pn;  
            float yBar = (float) sumY / pn;  
  
            a1 = (float) ((pn * sumXY - sumX * sumY) / (pn * sumXX - sumX  * sumX));  
            a0 = (float) (yBar - a1 * xBar);  
        } else {  
            a0 = a1 = Float.NaN;  
        }  
  
        coefsValid = true;  
    }  
  
    /** 
     * 返回误差 
     */  
    public float getR() {  
        // 遍历这个list并计算分母  
        for (int i = 0; i < pn - 1; i++) { 
        	
            float Yi = (float) Integer.parseInt(listY.get(i).toString());  
//            System.out.println("Yi:" + listY.get(i).toString());
            float Y = at(Integer.parseInt(listX.get(i).toString()));  
            
            float deltaY = Yi - Y;  
            float deltaY2 = deltaY * deltaY;  
            /* 
             * System.out.println("Yi:" + Yi); System.out.println("Y:" + Y); 
             * System.out.println("deltaY:" + deltaY); 
             * System.out.println("deltaY2:" + deltaY2); 
             */  
  
            sumDeltaY2 += deltaY2;  
            // System.out.println("sumDeltaY2:" + sumDeltaY2);  
  
        }  
  
        sst = sumYY - (sumY * sumY) / pn;  
        // System.out.println("sst:" + sst);  
        E = 1 - sumDeltaY2 / sst;  
  
        return round(E, 4);  
    }  
  
    // 用于实现精确的四舍五入  
    public float round(double v, int scale) {  
  
        if (scale < 0) {  
            throw new IllegalArgumentException(  
                    "The scale must be a positive integer or zero");  
        }  
  
        BigDecimal b = new BigDecimal(Double.toString(v));  
        BigDecimal one = new BigDecimal("1");  
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();  
  
    }  
  
    public float round(float v, int scale) {  
  
        if (scale < 0) {  
            throw new IllegalArgumentException(  
                    "The scale must be a positive integer or zero");  
        }  
  
        BigDecimal b = new BigDecimal(Double.toString(v));  
        BigDecimal one = new BigDecimal("1");  
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();  
  
    }  
}  