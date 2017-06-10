package com.APP.DrawWave;

import com.APP.DrawWave.Constant;
import com.APP.DrawWave.Plot;
import com.APP.DrawWave.PlotFrame;
import com.APP.DrawWave.WaveFileReader;

public class PlotUtil {


	// JavaPlot 只支持double数组的绘制
	public static double[] Integers2Doubles(int[] raw) {
		double[] res = new double[raw.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = raw[i];
		}
		return res;
	}

	// 绘制波形文件
	// 输入文件为音频，输出文件为图片，宽度，高度设置，是否出现图形界面
	public static void drawWaveImage(String inFileName, String outFileName,
			int w, int h,boolean isVisiable) {
		Constant.iv=isVisiable;
		WaveFileReader reader = new WaveFileReader(inFileName);
		PlotFrame frame=null;
		String[] pamss = new String[] { "-g", "-r", "-b" };
		if (reader.isSuccess()) {
			frame = Plot.figrue("waveaccess");
			frame.setSize(w, h);
		//	frame.setVisible(false);
			Plot.hold_on();
			for (int i = 0; i < reader.getNumChannels(); ++i) {
				// 获取i声道数据
				int[] data = reader.getData()[i];
				// 绘图
				Plot.plot(Integers2Doubles(data), pamss[i % pamss.length],outFileName,w,h,isVisiable);
			}
			
			  System.out.println(outFileName+"  图片已生成。");
			Plot.hold_off();
		} else {
			System.err.println(inFileName + "不是一个正常的wav文件");
		
		}
	

	}

	public static void main(String[] args) {
		String picName="C:/Users/q51/Desktop/V3/ces.jpg";
//		String inFileName="C:/Users/q51/Desktop/V3/wav_20_8_1_pcm.wav";
//		String waveFile=inFileName.replace(".V3", ".wav");
//		if(inFileName.indexOf(".V3")>0){
//			try {
//				new V3FileUtil().convert(inFileName, waveFile);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	
		 drawWaveImage("C:/Users/q51/Desktop/V3/0114073.wav",picName,6000,200,true);
	}
}
