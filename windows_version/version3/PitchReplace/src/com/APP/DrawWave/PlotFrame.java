/*    */ package com.APP.DrawWave;
/*    */ 
/*    */ import com.APP.DrawWave.DrawableObject;
/*    */ import java.awt.GraphicsConfiguration;
/*    */ import java.awt.HeadlessException;
/*    */ import java.awt.event.WindowAdapter;
/*    */ import java.awt.event.WindowEvent;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class PlotFrame extends JFrame
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 44 */   private PlotPanle plotPanle = null;
           private boolean iv  = true;
/*    */ 
/*    */   public PlotFrame()
/*    */     throws HeadlessException
/*    */   {
/* 25 */     commonInit();
/*    */   }
/*    */ 
/*    */   public PlotFrame(GraphicsConfiguration gc) {
/* 29 */     super(gc);
/* 30 */     commonInit();
/*    */   }
/*    */ 
/*    */   public PlotFrame(String title, GraphicsConfiguration gc) {
/* 34 */     super(title, gc);
/* 35 */     commonInit();
/*    */   }
/*    */ 
/*    */   public PlotFrame(String title,boolean isVisible) throws HeadlessException {
/* 39 */     super(title);
             this.iv=isVisible;
/* 40 */     commonInit();
/*    */   }

           public PlotFrame(String title) throws HeadlessException {
/* 39 */     super(title);
/* 40 */     commonInit();
/*    */   }
/*    */ 
/*    */   private void commonInit()
/*    */   {
/* 47 */     this.plotPanle = new PlotPanle();
/* 48 */     add(this.plotPanle);
/* 49 */     setSize(300, 200);
/* 50 */     setLocationRelativeTo(null);
/* 51 */     addWindowListener(new WindowAdapter()
/*    */     {
/*    */       public void windowClosing(WindowEvent e) {
/* 54 */         if (!Plot.removePlotFrame(PlotFrame.this))
/* 55 */           System.exit(0);
/*    */       }
/*    */     });
/* 59 */    setVisible(iv);
/*    */   }
/*    */ 
/*    */   public void setHoldOn(boolean holdOn)
/*    */   {
/* 66 */     this.plotPanle.setHoldOn(holdOn);
/*    */   }
/*    */ 
/*    */   public void plot(DrawableObject drawableObject)
/*    */   {
/* 73 */     this.plotPanle.plot(drawableObject);
/*    */   }
/*    */ 
/*    */   public void axis(double sx, double ex, double sy, double ey)
/*    */   {
/* 80 */     this.plotPanle.axis(sx, ex, sy, ey);
/*    */   }
/*    */ 
/*    */   public void suit()
/*    */   {
/* 87 */     this.plotPanle.suit();
/*    */   }
/*    */
public PlotPanle getPlotPanle() {
	return plotPanle;
}
public void setPlotPanle(PlotPanle plotPanle) {
	this.plotPanle = plotPanle;
}
public boolean isIv() {
	return iv;
}
public void setIv(boolean iv) {
	this.iv = iv;
} }

/* Location:           C:\Users\q51\Desktop\temp\download\WaveAccess-master\lib\com.sin.java.plot.1.0.jar
 * Qualified Name:     com.sin.java.plot.PlotFrame
 * JD-Core Version:    0.6.1
 */