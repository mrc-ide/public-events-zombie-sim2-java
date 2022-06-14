package com.mrc.zombie2;

import java.awt.Color;
import java.awt.image.BufferedImage;
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
import com.mrc.GKit.GTickBox;
import com.mrc.GKit.GWindow;

public class ZGui_HD extends ZGui {

  public ZGui_HD(Z app) {
    super(app);
    final String im = "images" + File.separator;
    try {
      uk_frame = new BufferedImage(780, 744, BufferedImage.TYPE_3BYTE_BGR);
      movie_frame = new BufferedImage(780, 744, BufferedImage.TYPE_3BYTE_BGR);
      BufferedImage sd = ImageIO.read(new File(im + "uk.png"));
      for (int i=0; i<780; i++) {
        for (int j=0; j<744; j++) {
          uk_frame.setRGB(i,  j,  sd.getRGB((int)(i/1.5), (int)(j/1.5)));
          movie_frame.setRGB(i,  j,  sd.getRGB((int)(i/1.5), (int)(j/1.5)));
        }
      }
    } catch (Exception e) { e.printStackTrace(); }
      wid = 1536;
    hei = 900;
  }
  // "file" here is a PNG, which we load, and expand, plotting only the
  // non-zero pixels. For zeroes, we leave them as they were

  public void setMovieImage(File file) {
    setMovieImage_gen(file, 3);
  }

  GButton add_graph_del(GPanel M, int x, int y) {
      return (GButton) M.addChild(new GButton(x, y, 22, 22, M, gw, parent.CLEAR, "X"));
    }

  private GPanel left_panel_maker(GPanel M, int y, int hei, String n) {
  return (GPanel) M.addChild(new GPanel(10, y, 275, hei, true, n, (byte) 26, false, M, gw));
  }


  public void initGUI() {
    gw = new GWindow(wid, hei, loc_x, loc_y, parent, GColourScheme.getDefault());
    p_main = new GPanel(0, 0, wid - 2,hei - 2, true, "Imperial College Zombie Outbreak Simulator", (byte)46,true,null,gw);
    int pic_wid = 260 * 3;
    int pic_hei = 248 * 3;
    //520, 496
    b_frame = (GButton) (p_main.addChild(new GButton(wid-(pic_wid + 21),60,pic_wid + 6,pic_hei + 9,p_main,gw,0,"")));
    b_frame.setShowIcon(true);
    b_frame.setAlwaysLit(true);
    b_frame.setIcon(movie_frame,null);
    s_frame = (GSlider) p_main.addChild(new GSlider(wid-(pic_wid + 21),60 + pic_hei + 16, pic_wid + 6,GSlider.SLIDE_HORIZ,p_main,1,1,1,parent.SET_FRAME,gw));
    l_days = (GLabel) p_main.addChild(new GLabel(wid-(pic_wid + 5),60 + pic_hei + 36,"Day No: ",GLabel.LEFT_ALIGN,-1,p_main,gw));

    int top = 90;

    // Three panels on the left, from top to bottom.

    GPanel p_hist = left_panel_maker(p_main, 60, 150, "Virus Properties");
    p_hist.setTitleColour(new Color(50, 61, 55));
    top += 130 + 10;

    GPanel p_intv = left_panel_maker(p_main, top, 150, "Vaccination");
    p_intv.setTitleColour(new Color(11, 31, 50));
    top += 160 + 10;

    GPanel p_mob = left_panel_maker(p_main, top, 206, "Zombie Mobility");
    p_mob.setTitleColour(new Color(50,31,11));
    top += 216 + 10;

    GPanel p_seed = left_panel_maker(p_main, top, 150, "Seeding");
    p_seed.setTitleColour(new Color(26, 10, 44));
    top += 160 + 10;

    /////////////////////////////////////////////////////////
    // The GO Button!! (And Imperial College logo)

    b_ic = (GButton) (p_main.addChild(new GButton(10, top, 170, 90, p_main, gw, 0, "")));
    b_ic.setShowIcon(true);
    b_ic.setAlwaysLit(true);
    b_ic.setIcon(ic_button, null);
    b_ic.setBackColour(new Color(0,0,0));
    b_ic.setHighlightBackColour(new Color(0,0,0));

    b_execute = (GButton) (p_main.addChild(new GButton(192,top,90,90,p_main,gw, parent.RUN_SIM,"")));
    b_execute.setShowIcon(true);
    b_execute.setAlwaysLit(true);
    b_execute.setIcon(go_button,null);
    b_execute.setBackColour(new Color(0,0,0));
    b_execute.setHighlightBackColour(new Color(0,0,0));

    ////////////////////////////////////////////////
    // Create the incidence graph - four lines...
    int gwid = 420;
    int ghei = 375;
    int gleft = wid - (pic_wid + 21) - (gwid + 16);

    gp_inc = (GGraphPanel) p_main.addChild(new GGraphPanel(gleft, 60, gwid, ghei, true, "New Infections Daily (millions)",
        (byte)26, false, p_main, gw, new int[] { parent.INCIDENCE_GRAPH, parent.INCIDENCE_GRAPH + 1,
                                                 parent.INCIDENCE_GRAPH + 2,parent.INCIDENCE_GRAPH + 3}));

    init_graph(gp_inc);
    gp_inc.setTitleColour(new Color(50,8,8));

    ////////////////////////////////////////////
    // Same for the accumulated graph

    gp_acc= (GGraphPanel) p_main.addChild(new GGraphPanel(gleft,65 + ghei,gwid,ghei,true,"Accumulated Infections (millions)",
        (byte)26,false,p_main,gw,new int[] {parent.CURRENT_INF_GRAPH,parent.CURRENT_INF_GRAPH+1,
            parent.CURRENT_INF_GRAPH+2,parent.CURRENT_INF_GRAPH+3}));
    init_graph(gp_acc);
    gp_acc.setTitleColour(new Color(50, 11, 50));

    // Create the tickboxes and delete buttons
    int boxes_y = 65 + ghei + ghei + 10;
    gt_col0 = add_graph_tick(p_main, gleft, boxes_y);
    gt_col1 = add_graph_tick(p_main, gleft+65, boxes_y);
    gt_col2 = add_graph_tick(p_main, gleft+130, boxes_y);
    gt_col3 = add_graph_tick(p_main, gleft+195, boxes_y);
    gb_ecol0 = add_graph_del(p_main, gleft+25, boxes_y);
    gb_ecol1 = add_graph_del(p_main, gleft+90, boxes_y);
    gb_ecol2 = add_graph_del(p_main, gleft+155, boxes_y);
    gb_ecol3 = add_graph_del(p_main, gleft+220, boxes_y);

    boxes_y -= 4;

    // And the coloured lines above each one

    p_main.addChild(new GLine(gleft, boxes_y, gleft+46, boxes_y, Color.RED, p_main, gw));
    p_main.addChild(new GLine(gleft+65, boxes_y, gleft+65+46, boxes_y, Color.GREEN, p_main, gw));
    p_main.addChild(new GLine(gleft+130, boxes_y, gleft+130+46, boxes_y, Color.YELLOW, p_main, gw));
    p_main.addChild(new GLine(gleft+195, boxes_y, gleft+195+46, boxes_y, Color.CYAN, p_main, gw));

    parent.current_colour = 0;
    gt_col0.setSelected(true);
    gt_col1.setSelected(false);
    gt_col2.setSelected(false);
    gt_col3.setSelected(false);


    ////////////////////////////////////////////////////////////////////////
    // The natural history panel (R0, infectious period)

    int left_col = 40;
    int right_col = 160;

    p_hist.addChild(new GLabel(left_col,44,"Reproduction",GLabel.LEFT_ALIGN,0,p_hist,gw));
    p_hist.addChild(new GLabel(left_col,64,"Number (R0)",GLabel.LEFT_ALIGN,0,p_hist,gw));

    p_hist.addChild(new GLabel(left_col,94,"Infectious",GLabel.LEFT_ALIGN,0,p_hist,gw));
    p_hist.addChild(new GLabel(left_col,114,"Period (days)",GLabel.LEFT_ALIGN,0,p_hist,gw));

    k_r0 = (GTextEntry) p_hist.addChild(new GTextEntry(right_col,47,60,p_hist,gw,"1.8",0));
    h_inf = (GTextEntry) p_hist.addChild(new GTextEntry(right_col,98,60,p_hist,gw,"3",0));

    /////////////////////////////////////////////////////////////////////////
    // The intervention / vaccination panel

    i_p=(GTextEntry) p_intv.addChild(new GTextEntry(left_col,40,40,p_intv,gw,"50",0));
    p_intv.addChild(new GLabel(88,44,"% of people",GLabel.LEFT_ALIGN,0,p_intv,gw));
    i_rad=(GTextEntry) p_intv.addChild(new GTextEntry(left_col,75,40,p_intv,gw,"20",0));
    p_intv.addChild(new GLabel(88,79,"km from",GLabel.LEFT_ALIGN,0,p_intv,gw));
    parent.cities[0] = "- No Vacc -";
    l_cities = (GList) p_intv.addHiddenChild(new GList(left_col - 5,-130,120,320,p_intv,parent.cities,gw,GList.SINGLE_SELECTION,0));
    lh_cities = (GListHeader) p_intv.addChild(new GListHeader(left_col,110,120,p_intv,gw,l_cities,GList.SINGLE_SELECTION));

    /////////////////////////////////////////////////////////////////////////
    // The mobility panel

    tb_mobility1 = (GTickBox) p_mob.addChild(new GTickBox(40, 40, p_mob, gw, parent.MOB_TICK));
    tb_mobility2 = (GTickBox) p_mob.addChild(new GTickBox(40, 80, p_mob, gw, parent.MOB_TICK));
    tb_mobility3 = (GTickBox) p_mob.addChild(new GTickBox(40, 120, p_mob, gw, parent.MOB_TICK));
    tb_mobility4 = (GTickBox) p_mob.addChild(new GTickBox(40, 160, p_mob, gw, parent.MOB_TICK));
    p_mob.addChild(new GLabel(70, 44, "Slow crawl", GLabel.LEFT_ALIGN, 0, p_mob, gw));
    p_mob.addChild(new GLabel(70, 84, "Medium pace", GLabel.LEFT_ALIGN, 0, p_mob, gw));
    p_mob.addChild(new GLabel(70, 124, "Fast Stride", GLabel.LEFT_ALIGN, 0, p_mob, gw));
    p_mob.addChild(new GLabel(70, 164, "Airborne", GLabel.LEFT_ALIGN, 0, p_mob, gw));
    tb_mobility1.setSelected(false);
    tb_mobility2.setSelected(true);
    tb_mobility3.setSelected(false);
    tb_mobility4.setSelected(false);


    //////////////////////////////////////////////////////////////////////////
    // The seeding panel
    parent.cities[0] = "- Random -";
    p_seed.addChild(new GLabel(88,44,"seeds",GLabel.LEFT_ALIGN,0,p_seed,gw));
    n_seeds = (GTextEntry) p_seed.addChild(new GTextEntry(left_col,40,40,p_seed,gw,"5",0));
    n_seedradius = (GTextEntry) p_seed.addChild(new GTextEntry(left_col,75,40,p_seed,gw,"10",0));
    p_seed.addChild(new GLabel(88,79,"km from",GLabel.LEFT_ALIGN,0,p_seed,gw));
    l_pcities = (GList) p_seed.addHiddenChild(new GList(left_col-5,-100,120,320,p_seed,parent.cities,gw,GList.SINGLE_SELECTION,0));
    lh_pcities = (GListHeader) p_seed.addChild(new GListHeader(left_col,110,130,p_seed,gw,l_pcities,GList.SINGLE_SELECTION));



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
