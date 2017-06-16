package com.APP.DrawWave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import com.APP.DrawWave.DrawableAttribute;
import com.APP.DrawWave.DrawableObject;


/**
 * 绘图面板，将绘制好的面板生成为图片
 * 
 * @author RobinTang
 * 
 */
public class PlotImage  {
	public List<DrawableObject> drawableObjectStack = null;
	private int w;
	private int h;
	private DrawableAttribute attribute = null;
	private int point_w = 0;
	
	private double axis_x = 0;
	private double axis_y = 0;
	private double axis_w = 15;
	private double axis_h = 15;
	
	// 信息属性
	public final static int doControl_x = 5;
	public final static int doControl_y = 5;
	public final static int doControl_w = 10;
	public final static int doControl_h = 10;
	
	public PlotImage(int w,int h) {
		this.w=w;
		this.h=h;
		this.drawableObjectStack = Collections.synchronizedList(new ArrayList<DrawableObject>());
	}
	
	//添加drawableObject对象
	public void addDo(DrawableObject e){
		drawableObjectStack.add(e);
	}
	
	// 绘图
		public void paint() {
			w = 500;
			h = 500;
			  File file = new File("rawwavs/imagetttttttttttttt.jpg");
			  BufferedImage bi = new BufferedImage(w, h,
			    BufferedImage.TYPE_INT_RGB);//RGB形式
			  Graphics2D g2 = (Graphics2D) bi.getGraphics();
			  g2.setBackground(Color.WHITE);//设置背景色
			  g2.clearRect(0, 0, w, h);//通过使用当前绘图表面的背景色进行填充来清除指定的矩形。
			  for (int k = 0; k < drawableObjectStack.size(); ++k) {
					if (drawableObjectStack.get(k).show)
						{
						DrawableObject drawableObject=drawableObjectStack.get(k);
						double[] x = drawableObject.x;
						double[] y = drawableObject.y;
						int count = x.length;
						g2.setColor(drawableObject.attribute.lineColor);
						this.attribute = drawableObject.attribute;
						this.point_w = attribute.width;
						boolean lefted = false, righted = false;
						for (int i = 0; i < count; ++i) {
							if (x[i] < axis_x) {
								if (lefted)
									continue;
								else
									lefted = true;
							}
							if (x[i] > (axis_x + axis_w)) {
								if (righted)
									continue;
								else
									righted = true;
							}
							// if (x[i] < axis_x || x[i] > (axis_x + axis_w))
							// continue;
							switch (drawableObject.type) {
							case Line:
								if (i > 0) {
									this.drawLine(g2, x[i - 1], y[i - 1], x[i], y[i]);
								}
								break;
							case Point:
								this.drawPoint(g2, x[i], y[i]);
								break;
							case Mark:
								this.fillPoint(g2, x[i], y[i]);
								break;
							case MarkLine:
								if (i > 0) {
									this.drawLine(g2, x[i - 1], y[i - 1], x[i], y[i]);
								}
								this.fillPoint(g2, x[i], y[i]);
								break;
							case Columnar:
								this.drawColumnar(g2, x[i], y[i]);
								break;
							case Step:
								if (i > 0) {
									this.drawLine(g2, x[i - 1], y[i - 1], x[i], y[i - 1]);
									this.drawLine(g2, x[i], y[i - 1], x[i], y[i]);
								}
								break;
							default:
								break;
							}
						}
						}
				}
			//	drawPlotInfo(g2);
			  try {
				ImageIO.write(bi, "jpeg", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  g2.dispose();
			  System.out.println("test");
		}
		
		/**
		 * 绘制可绘制对象
		 */
		private void paintDrawableObject(Graphics2D g, DrawableObject drawableObject) {
			w = 500;
			h = 500;
			double[] x = drawableObject.x;
			double[] y = drawableObject.y;
			int count = x.length;
			g.setColor(drawableObject.attribute.lineColor);
			this.attribute = drawableObject.attribute;
			this.point_w = attribute.width;
			boolean lefted = false, righted = false;
			for (int i = 0; i < count; ++i) {
				if (x[i] < axis_x) {
					if (lefted)
						continue;
					else
						lefted = true;
				}
				if (x[i] > (axis_x + axis_w)) {
					if (righted)
						continue;
					else
						righted = true;
				}
				// if (x[i] < axis_x || x[i] > (axis_x + axis_w))
				// continue;
				switch (drawableObject.type) {
				case Line:
					if (i > 0) {
						this.drawLine(g, x[i - 1], y[i - 1], x[i], y[i]);
					}
					break;
				case Point:
					this.drawPoint(g, x[i], y[i]);
					break;
				case Mark:
					this.fillPoint(g, x[i], y[i]);
					break;
				case MarkLine:
					if (i > 0) {
						this.drawLine(g, x[i - 1], y[i - 1], x[i], y[i]);
					}
					this.fillPoint(g, x[i], y[i]);
					break;
				case Columnar:
					this.drawColumnar(g, x[i], y[i]);
					break;
				case Step:
					if (i > 0) {
						this.drawLine(g, x[i - 1], y[i - 1], x[i], y[i - 1]);
						this.drawLine(g, x[i], y[i - 1], x[i], y[i]);
					}
					break;
				default:
					break;
				}
			}
		}

		// 绘图方法簇
		private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2) {
			g.drawLine(mappingX(x1), mappingY(y1), mappingX(x2), mappingY(y2));
		}

		private void drawPoint(Graphics2D g, double x, double y) {
			g.drawOval(mappingX(x, true), mappingY(y, true), attribute.width, attribute.width);
		}

		private void drawColumnar(Graphics2D g, double x, double y) {
			if (y > 0) {
				g.fillRect(mappingX(x) - point_w / 2, mappingY(y), point_w, (int) (y * h / (axis_h)) + 1);
			} else {
				y = -y;
				int hh = (int) (y * h / (axis_h)) + 1;
				g.fillRect(mappingX(x) - point_w / 2, mappingY(y) + hh, point_w, hh);
			}
		}

		private void fillPoint(Graphics2D g, double x, double y) {
			g.fillOval(mappingX(x, true), mappingY(y, true), attribute.width, attribute.width);
		}
		
		/**
		 * 坐标映射
		 */
		private int mappingX(double x) {
			return this.mappingX(x, false);
		}

		private int mappingX(double x, boolean b) {
			double kx = w / (axis_w);
			return (int) ((x - axis_x) * kx) - (b ? point_w / 2 : 0);
		}

		private int mappingY(double y) {
			return this.mappingY(y, false);
		}

		private int mappingY(double y, boolean b) {
			double ky = h / (axis_h);
			return h - (int) ((y - axis_y) * ky) - (b ? point_w / 2 : 0);
		}

		private double iMappingX(int x) {
			double kx = axis_w / w;
			return axis_x + x * kx;
		}

		private double iMappingY(int y) {
			double ky = axis_h / h;
			return axis_y + (h - y) * ky;
		}

}
