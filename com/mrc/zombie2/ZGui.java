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
import com.mrc.GKit.GColourScheme;
import com.mrc.GKit.GGraphPanel;
import com.mrc.GKit.GLabel;
import com.mrc.GKit.GLine;
import com.mrc.GKit.GList;
import com.mrc.GKit.GListHeader;
import com.mrc.GKit.GPanel;
import com.mrc.GKit.GSlider;
import com.mrc.GKit.GTextEntry;
import com.mrc.GKit.GTickBox;
import com.mrc.GKit.GWindow;
import com.mrc.GKit.GXMLHelper;

public class ZGui {

  ///////////////////////////////
  // Link to parent

  Z parent;
  
  ////////////////////////////////////
  // Cache params for different runs
  
  ZParams[] ParamHolder = new ZParams[4];

  ///////////////////////////////////////////
  // Basic position and size of window
  // defaults - and then load from XML

  int loc_x = 20;
  int loc_y = 20;
  int wid = 1024;
  int hei = 700;

  public void loadINI(Element root) {
    loc_x = Integer.parseInt(GXMLHelper.getAttribute(root, "left"));
    loc_y = Integer.parseInt(GXMLHelper.getAttribute(root, "top"));
    wid = Integer.parseInt(GXMLHelper.getAttribute(root, "width"));
    hei = Integer.parseInt(GXMLHelper.getAttribute(root, "height"));
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

  public void setMovieImage(File file) {
    try {
      if (file != null) {
        BufferedImage fin = b_frame.getLitIcon();
        BufferedImage bnew = ImageIO.read(file);
        for (int i = 0; i < 520; i += 2) {
          for (int j = 0; j < 496; j += 2) {
            int z = bnew.getRGB(i / 2,j / 2);
            if (z != 0) {
              fin.setRGB(i,j,z);
              fin.setRGB(i+1,j,z);
              fin.setRGB(i,j+1,z);
              fin.setRGB(i+1,j+1,z);
            } else {
              z = uk_frame.getRGB(i, j);
              //fin.setRGB(i, j, uk_frame.getRGB(i, j));
              //fin.setRGB(i + 1, j, uk_frame.getRGB(i + 1, j));
              //fin.setRGB(i, j + 1, uk_frame.getRGB(i, j + 1));
              //fin.setRGB(i + 1, j + 1, uk_frame.getRGB(i + 1, j + 1));
              fin.setRGB(i, j, z);
              fin.setRGB(i + 1, j, z);
              fin.setRGB(i, j + 1, z);
              fin.setRGB(i + 1, j + 1, z);
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

  public ZGui(Z app) {
    ParamHolder = new ZParams[4];
    for (int i=0; i<4; i++) ParamHolder[i] = new ZParams();
        
    try {
      this.parent = app;
      final String im = "images" + File.separator;
      uk_frame =  ImageIO.read(new File(im + "uk.png"));
      movie_frame =  ImageIO.read(new File(im + "uk.png"));
      go_button = ImageIO.read(new File(im + "go.png"));
      go_grey_button = ImageIO.read(new File(im + "go_grey.png") );
    } catch (Exception e) { e.printStackTrace(); }
  }

  ////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////
  //
  // Set up the GUI - all the ugly code here...
  // Will try and make some helper functions to keep it less ugly.

  private GPanel left_panel_maker(GPanel M, int y, int hei, String n) {
    return (GPanel) M.addChild(new GPanel(10, y, 200, hei, true, n, (byte) 26, false, M, gw));
  }

  private void init_graph(GGraphPanel G) {
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

  private GTickBox add_graph_tick(GPanel M, int x, int y) {
    return (GTickBox) M.addChild(new GTickBox(x, y, M, gw, parent.SEL_COL));
  }

  private GButton add_graph_del(GPanel M, int x, int y) {
    return (GButton) M.addChild(new GButton(x, y, 22, 22, M, gw, parent.CLEAR, "X"));
  }


  public void initGUI() {
    gw = new GWindow(wid, hei, loc_x, loc_y, parent, GColourScheme.getDefault());
    p_main = new GPanel(0, 0, wid - 2,hei - 2, true, "MRC Centre Interactive Spatial Simulator", (byte)46,true,null,gw);
    b_frame = (GButton) (p_main.addChild(new GButton(wid-541,60,526,507,p_main,gw,0,"")));
    b_frame.setShowIcon(true);
    b_frame.setAlwaysLit(true);
    b_frame.setIcon(movie_frame,null);
    s_frame = (GSlider) p_main.addChild(new GSlider(wid-541,572,526,GSlider.SLIDE_HORIZ,p_main,1,1,1,parent.SET_FRAME,gw));
    l_days = (GLabel) p_main.addChild(new GLabel(wid-525,592,"Day No: ",GLabel.LEFT_ALIGN,-1,p_main,gw));

    int top = 90;

    // Three panels on the left, from top to bottom.

    GPanel p_hist = left_panel_maker(p_main, 60, 150, "Natural History");
    p_hist.setTitleColour(new Color(50, 61, 55));
    top += 130 + 10;

    GPanel p_intv = left_panel_maker(p_main, top, 166, "Intervention");
    p_intv.setTitleColour(new Color(11, 31, 50));
    top += 176 + 10;

    GPanel p_seed = left_panel_maker(p_main, top, 150, "Seeding");
    p_seed.setTitleColour(new Color(26, 10, 44));
    top += 150 + 10;

    ////////////////////////////////////////////////
    // Create the incidence graph - four lines...

    gp_inc = (GGraphPanel) p_main.addChild(new GGraphPanel(wid - 809, 60, 263, 251, true, "New Infections Daily (millions)",
        (byte)26, false, p_main, gw, new int[] { parent.INCIDENCE_GRAPH, parent.INCIDENCE_GRAPH + 1,
                                                 parent.INCIDENCE_GRAPH + 2,parent.INCIDENCE_GRAPH + 3}));

    init_graph(gp_inc);
    gp_inc.setTitleColour(new Color(50,8,8));

    ////////////////////////////////////////////
    // Same for the accumulated graph

    gp_acc= (GGraphPanel) p_main.addChild(new GGraphPanel(wid-809,316,263,251,true,"Accumulated Infections (millions)",
        (byte)26,false,p_main,gw,new int[] {parent.CURRENT_INF_GRAPH,parent.CURRENT_INF_GRAPH+1,
            parent.CURRENT_INF_GRAPH+2,parent.CURRENT_INF_GRAPH+3}));
    init_graph(gp_acc);
    gp_acc.setTitleColour(new Color(50, 11, 50));

    // Create the tickboxes and delete buttons

    gt_col0 = add_graph_tick(p_main, 223, top);
    gt_col1 = add_graph_tick(p_main, 288, top);
    gt_col2 = add_graph_tick(p_main, 353, top);
    gt_col3 = add_graph_tick(p_main, 418, top);
    gb_ecol0 = add_graph_del(p_main, 248, top);
    gb_ecol1 = add_graph_del(p_main, 313, top);
    gb_ecol2 = add_graph_del(p_main, 378, top);
    gb_ecol3 = add_graph_del(p_main, 443, top);

    top -= 4;

    // And the coloured lines above each one

    p_main.addChild(new GLine(223, top, 269, top, Color.RED, p_main, gw));
    p_main.addChild(new GLine(288, top, 334, top, Color.GREEN, p_main, gw));
    p_main.addChild(new GLine(353, top, 399, top, Color.YELLOW, p_main, gw));
    p_main.addChild(new GLine(418, top, 464, top, Color.CYAN, p_main, gw));

    parent.current_colour = 0;
    gt_col0.setSelected(true);
    gt_col1.setSelected(false);
    gt_col2.setSelected(false);
    gt_col3.setSelected(false);


    ////////////////////////////////////////////////////////////////////////
    // The natural history panel (R0, infectious period)

    p_hist.addChild(new GLabel(20,44,"Reproduction",GLabel.LEFT_ALIGN,0,p_hist,gw));
    p_hist.addChild(new GLabel(20,64,"Number (R0)",GLabel.LEFT_ALIGN,0,p_hist,gw));

    p_hist.addChild(new GLabel(20,94,"Infectious",GLabel.LEFT_ALIGN,0,p_hist,gw));
    p_hist.addChild(new GLabel(20,114,"Period (days)",GLabel.LEFT_ALIGN,0,p_hist,gw));

    k_r0 = (GTextEntry) p_hist.addChild(new GTextEntry(140,48,40,p_hist,gw,"1.8",0));
    h_inf = (GTextEntry) p_hist.addChild(new GTextEntry(140,98,40,p_hist,gw,"3",0));

    /////////////////////////////////////////////////////////////////////////
    // The intervention / vaccination panel

    p_intv.addChild(new GLabel(20,44,"Vaccinate",GLabel.LEFT_ALIGN,0,p_intv,gw));
    i_p=(GTextEntry) p_intv.addChild(new GTextEntry(20,70,40,p_intv,gw,"50",0));
    p_intv.addChild(new GLabel(68,74,"% of people",GLabel.LEFT_ALIGN,0,p_intv,gw));
    i_rad=(GTextEntry) p_intv.addChild(new GTextEntry(20,100,40,p_intv,gw,"20",0));
    p_intv.addChild(new GLabel(68,104,"km from",GLabel.LEFT_ALIGN,0,p_intv,gw));

    l_cities = (GList) p_intv.addHiddenChild(new GList(15,-100,120,320,p_intv,parent.cities,gw,GList.SINGLE_SELECTION,0));
    lh_cities = (GListHeader) p_intv.addChild(new GListHeader(20,130,120,p_intv,gw,l_cities,GList.SINGLE_SELECTION));

    //////////////////////////////////////////////////////////////////////////
    // The seeding panel

    p_seed.addChild(new GLabel(68,44,"seeds",GLabel.LEFT_ALIGN,0,p_seed,gw));
    n_seeds = (GTextEntry) p_seed.addChild(new GTextEntry(20,40,40,p_seed,gw,"5",0));
    n_seedradius = (GTextEntry) p_seed.addChild(new GTextEntry(20,70,40,p_seed,gw,"10",0));
    p_seed.addChild(new GLabel(68,74,"km from",GLabel.LEFT_ALIGN,0,p_seed,gw));
    l_pcities = (GList) p_seed.addHiddenChild(new GList(15,-100,120,320,p_seed,parent.cities,gw,GList.SINGLE_SELECTION,0));
    lh_pcities = (GListHeader) p_seed.addChild(new GListHeader(20,100,130,p_seed,gw,l_pcities,GList.SINGLE_SELECTION));
    l_pcities.removeEntry(0);
    l_pcities.addEntry(0,"- Random -");

    /////////////////////////////////////////////////////////
    // The GO Button!!

    b_execute = (GButton) (p_main.addChild(new GButton(10,590,90,90,p_main,gw, parent.RUN_SIM,"")));
    b_execute.setShowIcon(true);
    b_execute.setAlwaysLit(true);
    b_execute.setIcon(go_button,null);
    b_execute.setBackColour(new Color(0,0,0));
    b_execute.setHighlightBackColour(new Color(0,0,0));


    ///////////////////////////////
    // If in admin mode, we can also have a Save XML button

    if (parent.admin)
      b_xml = (GButton) p_main.addChild(new GButton(wid-613,650,36,24,p_main,gw, parent.DUMP_XML,"XML"));

    setMovieImage(null);
    for (int i=0; i<4; i++) ParamHolder[i].save(this);

    gw.setMaster(p_main);
    gw.update();
    gw.setVisible(true);
    gw.startFade();

  }

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
