package com.mrc.zombie2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.imageio.ImageIO;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mrc.GKit.GButton;
import com.mrc.GKit.GGraphPanel;
import com.mrc.GKit.GLabel;
import com.mrc.GKit.GList;
import com.mrc.GKit.GListHeader;
import com.mrc.GKit.GPanel;
import com.mrc.GKit.GSlider;
import com.mrc.GKit.GTextEntry;
import com.mrc.GKit.GTickBox;
import com.mrc.GKit.GWindow;
import com.mrc.GKit.GXMLHelper;

public abstract class ZGui {

  ///////////////////////////////
  // Link to parent

  Z parent;

  ////////////////////////////////////
  // Cache params for different runs

  ZParams[] ParamHolder = new ZParams[4];

  ///////////////////////////////////////////
  // Basic position and size of window
  // defaults - and then load from XML
  // Then we'll try and do some magic resizing...

  int loc_x = 20;
  int loc_y = 20;
  int wid = 1024;
  int hei = 700;

  public void loadINI(Element root) {
    loc_x = Integer.parseInt(GXMLHelper.getAttribute(root, "left"));
    loc_y = Integer.parseInt(GXMLHelper.getAttribute(root, "top"));
  }

  //////////////////////////////////
  // Main parents

  GWindow gw;
  GPanel p_main;

  ///////////////////////////////////////////////////////////////////
  // The two graphs, and associated buttons.

  GGraphPanel gp_inc;  // The incidence graph
  GGraphPanel gp_acc;  // The accumulated cases graph

  GTickBox gt_col0,gt_col1,gt_col2,gt_col3;     // The four tickboxes
  GButton gb_ecol1,gb_ecol2,gb_ecol3,gb_ecol0;  // The four delete boxes

  // Functions to update / clear

  public void updateColSelect(Object comp) {
    ParamHolder[parent.current_colour].save(this);
    gt_col0.setSelected(comp == gt_col0);
    gt_col1.setSelected(comp == gt_col1);
    gt_col2.setSelected(comp == gt_col2);
    gt_col3.setSelected(comp == gt_col3);
    if (comp == gt_col0) parent.current_colour = 0;
    else if (comp == gt_col1) parent.current_colour = 1;
    else if (comp == gt_col2) parent.current_colour = 2;
    else if (comp == gt_col3) parent.current_colour = 3;
    gt_col0.paintOn(gw.bi(), gw.g2d());
    gt_col1.paintOn(gw.bi(), gw.g2d());
    gt_col2.paintOn(gw.bi(), gw.g2d());
    gt_col3.paintOn(gw.bi(), gw.g2d());

    // Rolling back this change, as in practise,
    // it doesn't work to have the params updating
    // when you change colour. A better solution
    // would be a more explicit copy/reload method,
    // but without making the interface horrible.

    // ParamHolder[parent.current_colour].retrieve(this);
  }

  public void clearCol(Object comp) {
    int i=0;
    if (comp == gb_ecol0) i=0;
    else if (comp == gb_ecol1) i=1;
    else if (comp == gb_ecol2) i=2;
    else if (comp == gb_ecol3) i=3;
    parent.accum_inf[i].clear();
    parent.new_incidence[i].clear();
    gp_acc.updateAllGraphs();
    gp_inc.updateAllGraphs();
    gp_inc.paintOn(gw.bi(), gw.g2d());
    gp_acc.paintOn(gw.bi(), gw.g2d());
    gw.requestRepaint();
    gw.checkRepaint();
  }

  ///////////////////////////////////////////////////////
  // The natural history panel:

  GTextEntry k_r0;   // R0
  GTextEntry h_inf;  // infectious period

  ///////////////////////////////////
  // Intervention panel

  GTextEntry i_p;     // Proportion of people to vaccinate
  GTextEntry i_rad;   // Radius from city (km)
  GListHeader lh_cities; // Head for intervention cities
  GList l_cities;        // Dropdown list for intervention cities

  /////////////////////////////////////
  // Mobility panel (only implemented in HD version)

  GTickBox tb_mobility1;
  GTickBox tb_mobility2;
  GTickBox tb_mobility3;
  GTickBox tb_mobility4;

  public void updateMobility(GTickBox clicked) {
    tb_mobility1.setSelected(false);
    tb_mobility2.setSelected(false);
    tb_mobility3.setSelected(false);
    tb_mobility4.setSelected(false);
    clicked.setSelected(true);
    gw.requestRepaint();
    gw.update();
    if (clicked == tb_mobility1) {
      parent.k_a = 4;
      parent.k_b = 6;
      parent.k_cut = 180;
    } else if (clicked == tb_mobility2) {
        parent.k_a = 4;
      parent.k_b = 4;
      parent.k_cut = 180;
    } else if (clicked == tb_mobility3) {
      parent.k_a = 4;
    parent.k_b = 3;
    parent.k_cut = 180;
    } else if (clicked == tb_mobility4) {
      parent.k_a = 4;
    parent.k_b = 0.5;
    parent.k_cut = 3000;
    }
  }


  GTextEntry i_sympt; // Not used in Zombie Sim 2

  ////////////////////////////////////
  // Seeding panel

  GTextEntry n_seeds;        // Number of seeds
  GTextEntry n_seedradius;   // Radius from city (km)
  GListHeader lh_pcities; // Head for intervention cities
  GList l_pcities;        // Dropdown list for intervention cities

  /////////////////////////////////////////////////////////////////////
  // Functions to update all the parameters (from a file, or perhaps
  // from a browser/android)

  public void refreshGUI() {
    k_r0.paintOn(gw.bi(),gw.g2d());
    i_rad.paintOn(gw.bi(),gw.g2d());
    i_p.paintOn(gw.bi(),gw.g2d());
    h_inf.paintOn(gw.bi(),gw.g2d());
    lh_cities.paintOn(gw.bi(),gw.g2d());
    n_seeds.paintOn(gw.bi(),gw.g2d());
    n_seedradius.paintOn(gw.bi(),gw.g2d());
    lh_pcities.paintOn(gw.bi(),gw.g2d());
  }


  public void showParams(Node p) {
    k_r0.setText(GXMLHelper.getAttribute(p, "k_r0"));
    h_inf.setText(GXMLHelper.getAttribute(p, "h_inf"));
    i_rad.setText(GXMLHelper.getAttribute(p, "i_rad"));
    i_p.setText(GXMLHelper.getAttribute(p, "i_pc"));
    l_cities.setSelected(Integer.parseInt(GXMLHelper.getAttribute(p,"v_city")));
    n_seeds.setText(GXMLHelper.getAttribute(p, "n_seeds"));
    n_seedradius.setText(GXMLHelper.getAttribute(p, "s_rad"));
    l_pcities.setSelected(Integer.parseInt(GXMLHelper.getAttribute(p,"s_loc")));
    refreshGUI();
    gw.requestRepaint();
  }

  //////////////////////////////////
  // Big spatial image window

  GSlider s_frame;   // The slider for which movie frame.
  GLabel l_days;     // Day No: x
  GButton b_frame;   // The movie frame itself is actually a button..?

  // "file" here is a PNG, which we load, and expand, plotting only the
  // non-zero pixels. For zeroes, we leave them as they were

  abstract void setMovieImage(File file);

  public void setMovieImage_gen(File file, int scale) {
    try {
      if (file != null) {
        BufferedImage fin = b_frame.getLitIcon();
        BufferedImage bnew = ImageIO.read(file);
        for (int i = 0; i < 260; i++) {
          int ii = i * scale;
          for (int j = 0; j < 248; j++) {
          int jj = j * scale;
            int z = bnew.getRGB(i, j);
            if (z == 0) z = uk_frame.getRGB(ii,  jj);
            for (int xs = ii; xs < ii + scale; xs++) {
              for (int ys = jj; ys < jj + scale; ys++) {
              fin.setRGB(xs, ys, z);
              }
            }
          }
        }
      }
      b_frame.paintOn(gw.bi(),gw.g2d());
    } catch (Exception e) { e.printStackTrace(); System.out.println(file);}
  }

  // If the slider is dragged:

  public void updateFrameSlider() {
    int fr = s_frame.getValue();
    String f = String.valueOf(fr);
    if (fr<1000) f="0"+f;
    if (fr<100) f="0"+f;
    if (fr<10) f="0"+f;
    setMovieImage(new File("job"+File.separator+"mov"+f+".png"));
    l_days.unPaint(gw.bi(), gw.g2d());
    l_days.setText("Day No: "+fr);
    l_days.paintOn(gw.bi(),gw.g2d());
    s_frame.paintOn(gw.bi(),gw.g2d());
  }

  /////////////////////////////////////////
  // GO BUTTON!!

  GButton b_execute;
  GButton b_ic;

  public void prepareToRun() {
    gw.locked = true;
    b_execute.setIcon(go_grey_button, null);
    b_execute.paintOn(gw.bi(), gw.g2d());
  }

  /////////////////////////////////////////////////////
  // Admin mode only:

  GButton b_xml;     // Button for saving current XML.

  //////////////////////////////////////////
  // Buffered image singletons:

  BufferedImage uk_frame;        // The frame loaded from the simulator
  BufferedImage movie_frame;     // The frame displayed on the screen
  BufferedImage go_button;       // The go button
  BufferedImage go_grey_button;  // The disabled go button
  BufferedImage ic_button;       // IC Logo

  public ZGui(Z app) {
    ParamHolder = new ZParams[4];
    for (int i=0; i<4; i++) ParamHolder[i] = new ZParams();

    try {
      this.parent = app;
      final String im = "images" + File.separator;
      go_button = ImageIO.read(new File(im + "go.png"));
      ic_button = ImageIO.read(new File(im + "ic.png"));
      go_grey_button = ImageIO.read(new File(im + "go_grey.png") );
    } catch (Exception e) { e.printStackTrace(); }
  }


  protected void init_graph(GGraphPanel G) {
    G.setMinX(0);
    G.setMaxX(0);
    G.setMinY(0);
    G.setMaxY(0);
    G.setLineColour(0,Color.RED);
    G.setLineColour(1,Color.GREEN);
    G.setLineColour(2,Color.YELLOW);
    G.setLineColour(3,Color.CYAN);
    G.setLineVisible(0, false);
    G.setLineVisible(1, false);
    G.setLineVisible(2, false);
    G.setLineVisible(3, false);
    G.setAuto(true);
  }

  protected GTickBox add_graph_tick(GPanel M, int x, int y) {
    return (GTickBox) M.addChild(new GTickBox(x, y, M, gw, parent.SEL_COL));
  }

  abstract GButton add_graph_del(GPanel M, int x, int y);


  abstract void initGUI();

  //////////////////////////////////////////////////////
  // When one new step of the simulation is done:

  public boolean nextStep() {
    // Frame number 0001

    String s = String.valueOf(parent.current_step);
    if (parent.current_step < 1000) s = "0" + s;
    if (parent.current_step < 100) s = "0" + s;
    if (parent.current_step < 10) s = "0" + s;

    File f = new File("job/data" + s + ".txt");

    if (f.exists()) {
      setMovieImage(new File("job" + File.separator + "mov" + s + ".png"));
      s_frame.setMax(parent.current_step);
      s_frame.setValue(parent.current_step);
      s_frame.updateSliderSize(parent.current_step, 1);
      s_frame.paintOn(gw.bi(), gw.g2d());
      parent.current_step++;

      File f2 = new File("job/data"+s+".txt2");
      while (!f2.exists()) {
        try {
          Thread.sleep(50);
        } catch (Exception e) { e.printStackTrace(); }
      }

      try {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String[] bits = br.readLine().split("\t");
        int new_inc = Integer.parseInt(bits[2]);
        parent.new_incidence[parent.current_colour].add(new_inc);
        int acc;
        if (parent.accum_inf[parent.current_colour].size() <= 1) acc = new_inc;
        else acc = new_inc + ((Integer)parent.accum_inf[parent.current_colour].get(parent.accum_inf[parent.current_colour].size() - 1)).intValue();
        parent.accum_inf[parent.current_colour].add(acc);

        // Below, scale graphs so top of the graph is 1.1 times the max - a bit of head room.

        gp_inc.setMaxX(Math.max(gp_inc.getMaxX(), parent.current_step));
        gp_inc.setMaxY(Math.max(gp_inc.getMaxY(), (double)(new_inc*1.1) / parent.MILL_SCALER));
        gp_inc.updateAllGraphs();
        gp_inc.paintOn(gw.bi(), gw.g2d());

        gp_acc.setMaxX(Math.max(gp_acc.getMaxX(), parent.current_step));
        gp_acc.setMaxY(Math.max(gp_acc.getMaxY(), (double) (acc * 1.1) / parent.MILL_SCALER));
        gp_acc.updateAllGraphs();
        gp_acc.paintOn(gw.bi(),gw.g2d());
        br.close();

        // Update the "Day No" label

        l_days.unPaint(gw.bi(), gw.g2d());
        l_days.setText("Day No: "+parent.current_step);
        l_days.paintOn(gw.bi(), gw.g2d());
      } catch (Exception e) { e.printStackTrace(); }

      gw.requestRepaint();
      gw.checkRepaint();

      // Optionally - we can also save the entire GUI as PNG - used for creating sample
      // movies of the whole app in operation.

      if (parent.GUI_PNG) {
        parent.gui_frame++;
        try {
          ImageIO.write(gw.bi(), "PNG", new File("gui_"+parent.movie_no+"_"+parent.gui_frame+".png"));
        } catch (Exception e) { e.printStackTrace(); }
      }

      return true;

    // Detect the end of the simulation - no more steps, and return control

    } else if (new File("job"+File.separator+"end.zom").exists()) {
      parent.more_steps = false;
      return false;

    // Otherwise - the simulation isn't over, but the next timestep isn't ready yet.

    } else return false;
  }

}
