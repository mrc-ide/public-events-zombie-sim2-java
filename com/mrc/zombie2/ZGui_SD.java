package com.mrc.zombie2;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.imageio.ImageIO;

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
import com.mrc.GKit.GWindow;

public class ZGui_SD extends ZGui {
  public ZGui_SD(Z app) {
    super(app);
    final String im = "images" + File.separator;
  try {
      movie_frame =  ImageIO.read(new File(im + "uk.png"));
      uk_frame =  ImageIO.read(new File(im + "uk.png"));
    
  } catch (Exception e) { e.printStackTrace(); }
    wid = 1024;
    hei = 700;
  }

  public void setMovieImage(File file) {
    setMovieImage_gen(file, 2);
  }

  GButton add_graph_del(GPanel M, int x, int y) {
      return (GButton) M.addChild(new GButton(x, y, 22, 22, M, gw, parent.CLEAR, "X"));
    }

  private GPanel left_panel_maker(GPanel M, int y, int hei, String n) {
      return (GPanel) M.addChild(new GPanel(10, y, 200, hei, true, n, (byte) 26, false, M, gw));
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
