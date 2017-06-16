package com.APP.DrawWave;

import com.APP.DrawWave.PlotUtil;

public class TestMain {
	public static void main(String[] args) throws Exception {
		//如果是V3格式需要转换成wav
		//new FileUtil().convertV2W("rawwavs/0628424.V3" , "rawwavs/0628424.wav");
		//将wav文件生成图片，600是宽带，200是高度，false 是不显示图形界面,true 显示 
		PlotUtil.drawWaveImage("rawwav/L2F_zhang_ai1_001.wav","rawwav/L2F_zhang_ai1_001.jpg",600,200,false);
	}

}
