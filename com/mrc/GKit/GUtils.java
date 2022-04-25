package com.mrc.GKit;

import java.awt.image.BufferedImage;


public class GUtils {
  public static void blur(BufferedImage bi, int x1, int y1, int x2, int y2) {
    BufferedImage bi2 = new BufferedImage(bi.getWidth(),bi.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
    for (int i=x1+1; i<x2; i++) {
      for (int j=y1+1; j<y2; j++) {
        double r=0;
        double g=0;
        double b=0;
               
        int v=0;
        double d=0;
        double scale=0;
        for (int _i=i-1; _i<=i+1; _i++) {
          for (int _j=j-1; _j<=j+1; _j++) {
            v=bi.getRGB(_i,_j);
            if ((_i==i) && (_j==j)) scale=100;
            else if ((_i==i) || (_j==j)) scale=5;
            else scale=5;
            
            b+=(v & 255)*scale;
            v = v >>>8;
            g+=(v & 255)*scale;
            v = v >>>8;
            r+=(v & 255)*scale;
            
            d+=scale;
          }
        }
        r/=d;
        b/=d;
        g/=d;
        v=(int)b+((int)g*256)+((int)r*65536)+0xFF000000;
        bi2.setRGB(i,j,v);
      }
    }
    for (int i=x1+1; i<x2; i++) {
      for (int j=y1+1; j<y2; j++) {
        //if (bi.getRGB(i,j)!=-1) 
          bi.setRGB(i, j, bi2.getRGB(i,j));
      }
    }
  }
}
