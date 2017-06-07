package com.APP.LinearRegression;

public class PitchLine {
	public PitchLine()
	{
		type_ = -1;
	}
	public DataPoint point1_;
	public DataPoint point2_;
	public DataPoint point3_;
	int type_;
	
	@Override
	public String toString(){
		if (3 != type_)
			return String.format("%f %f; %f %f", point1_.x, point1_.y, point2_.x, point2_.y);
		else
			return String.format("%f %f; %f %f %f", point1_.x, point1_.y, point2_.x, point2_.y, point3_.x, point3_.y);
	}
}
