/* GGraphPanel.java, part of the Global Epidemic Simulation v1.0 BETA
/* GKit: A panel with a single line graph. (Simple) 
/*
/* Copyright 2012, MRC Centre for Outbreak Analysis and Modelling
/* 
/* Licensed under the Apache License, Version 2.0 (the "License");
/* you may not use this file except in compliance with the License.
/* You may obtain a copy of the License at
/*
/*       http://www.apache.org/licenses/LICENSE-2.0
/*
/* Unless required by applicable law or agreed to in writing, software
/* distributed under the License is distributed on an "AS IS" BASIS,
/* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/* See the License for the specific language governing permissions and
/* limitations under the License.
*/

package com.mrc.GKit;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;


public class GGraphPanel extends GPanel {
  double max_x,max_y,min_x,min_y;
  BufferedImage gImage;
  int[] graph_func;
  double[][] results;
  Color[] lineColour;
  boolean auto;
  public static final int no_lines = 4;
  boolean[] line_visible;
  boolean plotting=false;
  public void setPlotting(boolean b) { plotting=b; }
  public boolean getPlotting() { return plotting; }  
  
  public void setMinX(double x) { min_x=x; }
  public void setMaxX(double x) { max_x=x; }
  public void setMinY(double y) { min_y=y; }
  public void setMaxY(double y) { max_y=y; }
  public double getMinX() { return min_x; }
  public double getMaxX() { return max_x; }
  public double getMinY() { return min_y; }
  public double getMaxY() { return max_y; }
  public void setAuto(boolean _auto) { auto=_auto; }
  public void setGFunc(int line,int x) { graph_func[line]=x; }
  public void setLineColour(int line_no,Color c) { lineColour[line_no] = new Color(c.getRGB()); }
  public static final byte LINE_GRAPH = 1;
  public static final byte HISTOGRAM = 2;
  
  public void setLineVisible(int i, boolean b) { line_visible[i]=b; }
  
  public void updateAllGraphs() {
    Graphics2D pg = (Graphics2D) gImage.getGraphics();
    pg.setColor(backColour);
    pg.fillRect(0,0,gImage.getWidth(),gImage.getHeight());
    
  
    for (int i=0; i<results.length; i++) {
      updateLineGraph(i);
    }
    calcMinMax();
    for (int i=0; i<results.length; i++) {
      drawLineGraph(i);
    }
  }
  
  public void updateLineGraph(int line_no) {
    int x_border = 40;
    int x_width = gImage.getWidth()-x_border;
  
    for (int i=0; i<x_width; i++) {
      if (i<results[line_no].length) {
        double x_val = min_x+((max_x-min_x)*((double)i/x_width));
        results[line_no][i]=gw.ga.graphFunction(this,graph_func[line_no],x_val);
        if (Double.isNaN(results[line_no][i])) results[line_no][i]=0;
      } else results[line_no][i]=results[line_no][i-1];
    }
    for (int i=x_width; i<results[line_no].length; i++) {
      results[line_no][i]=results[line_no][i-1];
    }
  }
 
  
  public void calcMinMax() {
    max_y=results[0][0];
    min_y=results[0][0];
    if (auto) {
      for (int i=0; i<results.length; i++) 
        if (line_visible[i]) {
        for (int j=0; j<results[i].length; j++) {
          if (results[i][j]>max_y) max_y=results[i][j];
          if (results[i][j]<min_y) min_y=results[i][j];
        }
      }
    }
    if (max_y==min_y) {
      if (max_y==0) {
        max_y=0.5;
        min_y=-0.5;
      } else {
        max_y+=min_y;
        min_y-=min_y;
      }
      
    }
    drawAxis();
  }
  
  
  public void drawLineGraph(int line_no) {
    if (line_visible[line_no]) {
    Graphics2D pg = (Graphics2D) gImage.getGraphics();
    
    int y_border = 20;
    int y_height = gImage.getHeight()-y_border;
    int y_top = 5;
    int x_border = 40;
    int x_width = gImage.getWidth()-x_border;
    pg.setColor(Color.WHITE);
    
    pg.drawLine(x_border,y_top,x_border,y_top+y_height);
    pg.drawLine(x_border,y_top+y_height,x_border+x_width,y_top+y_height);
    pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    pg.setColor(lineColour[line_no]);
    
    double scale_y=y_height/(max_y-min_y);
    double old_y=y_top+y_height-((results[line_no][0]-min_y)*scale_y);
    for (int i=1; i<results[line_no].length; i++) {
      double new_y=y_top+y_height-((results[line_no][i]-min_y)*scale_y);
      pg.drawLine(x_border+i-1,(int)old_y,x_border+i,(int)new_y);
      old_y=new_y;
    }
    }
  }
  
  public void drawAxis() {
    Graphics2D pg = (Graphics2D) gImage.getGraphics();
    pg.setFont(GWindow.TGRAPH_FONT);
    FontMetrics fm = pg.getFontMetrics();
    int y_border = 20;
    int y_height = gImage.getHeight()-y_border;
    int y_top = 5;
    int x_border = 40;
    int x_width = gImage.getWidth()-x_border;
    int no_x_axis_points = x_width/28;
    pg.setColor(backColour);
    pg.fillRect(2,2,x_border-6,y_height+6);
    pg.fillRect(2,y_height+10,x_width+(x_border-2),y_border+10);
    pg.setColor(Color.WHITE);
    
    for (int i=0; i<=no_x_axis_points; i++) {
      int x_mid = (int) ((double)i*(x_width/no_x_axis_points));
      String x_val = String.valueOf((int)Math.round(10.0*(min_x+  ((max_x-min_x)*((double)i/(double)no_x_axis_points)))));
      while (x_val.length()<2) x_val="0"+x_val;
      x_val=x_val.substring(0,x_val.length()-1)+"."+x_val.substring(x_val.length()-1);
      pg.drawString(x_val,x_border+x_mid-2,y_top+y_height+12);
      pg.drawLine(x_border+x_mid,y_top+y_height,x_border+x_mid,y_top+y_height+2);
    }
    
    
    int no_y_axis_points = y_height/16;
    for (int j=0; j<=no_y_axis_points; j++) {
      int y_mid = y_height - (int) ((double)j*(y_height/no_y_axis_points));
      String y_val = String.valueOf((Math.round(100.0*(min_y+  ((max_y-min_y)*((double)j/(double)no_y_axis_points)))))/100.0);
      pg.drawString(y_val,(x_border-6)-(fm.stringWidth(y_val)),y_mid+y_top+3);
      pg.drawLine(x_border,y_top+y_mid,x_border-2,y_top+y_mid);
    }
    
   
    paintOn(gw.bi,gw.g2d);
    gw.requestRepaint();
  }

  public GGraphPanel(int posX, int posY, int width, int height, boolean title, String titleString, 
      byte titleHeight, boolean enableDrag, GContainer parent,GWindow gw, int[] _func) {

    super(posX,posY,width, height, title, titleString, titleHeight, enableDrag, parent,gw);
    gImage = new BufferedImage(width-6,height-(6+titleHeight),BufferedImage.TYPE_3BYTE_BGR);
    graph_func=new int[no_lines];
    for (int i=0; i<no_lines; i++) graph_func[i]=_func[i];
    results = new double[no_lines][width-21];
    lineColour = new Color[no_lines];
    line_visible = new boolean[no_lines];
    for (int i=0; i<no_lines; i++) lineColour[i]=Color.white;
  }
  
  public synchronized void paintOn(BufferedImage bi,Graphics2D g) {
    
    if (isVisible()) {
      super.paintOn(bi,g);
      int par_x=0,par_y=0;
      if (parent!=null) {
        par_x = parent.get_parent_pos_x();
        par_y = parent.get_parent_pos_y();
      }
      
      g.drawImage(gImage,pos_x+par_x+3,pos_y+par_y+3+titleHeight ,null);
    }
  }
}
