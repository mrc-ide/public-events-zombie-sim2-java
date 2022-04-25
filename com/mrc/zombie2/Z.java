package com.mrc.zombie2;

// Zombie Sim II - simplified for Special Use, June/July 2014
// Update April 2016 - add option for saving whole GUI as separate frames.
// Update April 2022 - allow Android (or browser) control. And cleanup.

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mrc.GKit.GApp;
import com.mrc.GKit.GItem;
import com.mrc.GKit.GWebServer;
import com.mrc.GKit.GXMLHelper;

public class Z implements GApp {

  static final String Z_ver = "2.2";
  ///////////////////////////////////////////////
  // Overrides for GApp

  public boolean isUnSaved() { return false; }
  public void setUnSaved(boolean b) {}
  public void save() {}

  ///////////////////////////////////////
  // "Globals" for how the app runs.

  boolean GUI_PNG = false;
  int LISTEN_PORT = -1;
  boolean admin = false;
  ZGui ZG;

  ///////////////////////////////////////
  // Web server for android control

  ZListener ZL = null;
  GWebServer WS = null;

  void initNetwork() {
    if (LISTEN_PORT != -1) {
      ZL = new ZListener(this);
      WS = new GWebServer(ZL);
      WS.setPort(LISTEN_PORT);
    }
  }

  //////////////////////////////////////////////////////////////
  // Data underlying the incidence / accumulated cases graphs

  ArrayList<Integer>[] new_incidence;
  ArrayList<Integer>[] accum_inf;

  /////////////////////////////////////////////////////////////////////////
  // Names and locations of cities for the seeding/vaccination chooser

  String[] cities;
  double[] city_lon;
  double[] city_lat;

  ////////////////////////////////////////////////////////////////
  // Which graph are we plotting, and current movie status

  int current_colour;
  int current_step;
  boolean more_steps;
  int gui_frame = 0;
  int movie_no = 0;

  Element root; // The XML document root.

  final double MILL_SCALER = 100000;

  final byte INCIDENCE_GRAPH = 1; // and 2,3,4
  final byte CURRENT_INF_GRAPH = 5;  // and 6,7,8
  final byte SET_FRAME = 9;
  final byte SEL_COL = 10;
  final byte RUN_SIM = 11;
  final byte CLEAR = 18;
  final byte DUMP_XML = 22;
  
  ////////////////////////////////////////////////////////////
  // A way of saving the params for the different sets
  
  @SuppressWarnings("unchecked")
  public Z() {
    ZG = new ZGui(this);

    new_incidence = new ArrayList[4];
    accum_inf = new ArrayList[4];
    for (int i = 0; i<4; i++) {
      new_incidence[i] = new ArrayList<Integer>();
      accum_inf[i] = new ArrayList<Integer>();
    }
  }

  public void doFunction(int func, Object component) {
    if (!ZG.gw.locked) {
      if (func == SET_FRAME) ZG.updateFrameSlider();
      else if (func == SEL_COL) ZG.updateColSelect(component);
      else if (func == RUN_SIM) runSim();
      else if (func == CLEAR) ZG.clearCol(component);
      else if (func == DUMP_XML) dumpXML();
    }
  }

  public void dumpXML() {
    System.out.print("<c n=\"Simulation\" ");
    System.out.print("k_r0=\"" + ZG.k_r0.getText() + "\" ");
    System.out.print("v_city=\"" + ZG.l_cities.getSelected() + "\" ");
    System.out.print("i_rad=\"" + ZG.i_rad.getText() + "\" ");
    System.out.print("i_pc=\"" + ZG.i_p.getText() + "\" ");
    System.out.print("h_inf=\"" + ZG.h_inf.getText() + "\" ");
    System.out.print("s_loc=\"" + ZG.l_pcities.getSelected() + "\" ");
    System.out.print("s_rad=\"" + ZG.n_seedradius.getText() + "\" ");
    System.out.println("n_seeds=\"" + ZG.n_seeds.getText() + "\" />\n");
  }

  public double getDoubleData(int line_no, ArrayList<Double>[] data, double x) {
    int d1 = (int) x;
    int d2 = 1 + d1;
    double result;
    double v1;
    if (d1 < data[line_no].size()) v1 = ((Double) (data[line_no].get(d1))).doubleValue();
    else if (data[line_no].size() > 0) v1=((Double) (data[line_no].get(data[line_no].size() - 1))).doubleValue();
    else v1 = 0.0;
    if (d2 < data[line_no].size()) {
      double v2 = ((Double) (data[line_no].get(d2))).doubleValue();
      result = v1 + ((v2 - v1) * (x - (int) x));
    } else result = v1;
    return result;
  }

  public double getIntData(int line_no, ArrayList<Integer>[] data, double x) {
    int d1 = (int) x;
    int d2 = 1 + d1;
    double result;
    double v1;
    if (d1 < data[line_no].size()) v1 = ((Integer) (data[line_no].get(d1))).intValue();
    else if (data[line_no].size() > 0) v1=((Integer) (data[line_no].get(data[line_no].size() - 1))).intValue();
    else v1 = 0.0;
    if (d2 < data[line_no].size()) {
      double v2 = ((Integer) (data[line_no].get(d2))).intValue();
      result = v1 + ((v2 - v1) * (x - (int) x));
    } else result = v1;
    return result;
  }

  public double graphFunction(GItem gc, int func, double x_val) {
    if ((func >= INCIDENCE_GRAPH) && (func < INCIDENCE_GRAPH + 4)) {
      return getIntData(func - INCIDENCE_GRAPH, new_incidence, x_val) / MILL_SCALER;
    } else if ((func >= CURRENT_INF_GRAPH) && (func < CURRENT_INF_GRAPH + 4)) {
      return getIntData(func - CURRENT_INF_GRAPH, accum_inf, x_val) / MILL_SCALER;
    } else return 0.0;
  }

  public void createINIFile() {}

  public void saveINIFile() {}

  public void loadINIFile() {
    try {
      root = GXMLHelper.loadDocument("z_conf.xml");

      // Load the position/size of the window

      ZG.loadINI(root);

      // Load the info on the cities. (cities->c)

      Node cities_node = GXMLHelper.getTag(root, "cities");
      int no_cities = GXMLHelper.countChildren(cities_node, "c");
      cities = new String[no_cities];
      city_lon = new double[no_cities];
      city_lat = new double[no_cities];

      for (int i = 0; i < no_cities; i++) {
        Node city = GXMLHelper.getChildNo(cities_node, "c", i);
        cities[i] = GXMLHelper.getAttribute(city,"n");
        city_lon[i] = Double.parseDouble(GXMLHelper.getAttribute(city, "x"));
        city_lat[i] = Double.parseDouble(GXMLHelper.getAttribute(city, "y"));
      }

      // Load the default params for the simulation (param_node->p)

      Node param_node = GXMLHelper.getTag(root, "params");
      GXMLHelper.getAttribute(GXMLHelper.getChildNo(param_node, "p", 0), "n");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void resetGraphs() {
    new_incidence[current_colour].clear();
    accum_inf[current_colour].clear();
    int maxX = -1;
    for (int i = 0; i < 4; i++) {
      if (new_incidence[i].size() > maxX) maxX = new_incidence[i].size();
    }
    ZG.gp_acc.setMaxX(maxX);
    ZG.gp_inc.setMaxX(maxX);

  }


  public void runSim() {
    ZG.prepareToRun();
    new Thread(new RunnerThread()).start();
  }
  
  public void goGoGo() {
    try {
      resetGraphs();
      ZG.gp_acc.setLineVisible(current_colour, true);
      ZG.gp_inc.setLineVisible(current_colour, true);
      PrintWriter iniFile = new PrintWriter(new File("job"+File.separator+"zom.ini"));
      iniFile.println("180");  // k_cut
      iniFile.println("4.0");  // k_a
      iniFile.println("3.0");  // k_b
      iniFile.println(ZG.k_r0.to_double_string());
      iniFile.println("0");    // s_amp
      iniFile.println("40.0"); // s_per
      iniFile.println("0.0");  // s_off
      iniFile.println("1.0");  // s_cen
      iniFile.println(String.valueOf(city_lon[ZG.l_cities.getSelected()]));
      iniFile.println(String.valueOf(city_lat[ZG.l_cities.getSelected()]));
      iniFile.println(ZG.i_rad.to_double_string());
      iniFile.println(ZG.i_p.to_double_pc_string());
      iniFile.println("0"); // i_isolate (as a fraction, not percent)
      iniFile.println("0"); // i_limit_travel
      iniFile.println("3"); // h_latent
      iniFile.println(ZG.h_inf.to_double_string());
      iniFile.println("0.9"); // h_sympt
      iniFile.println(ZG.n_seeds.to_double_string());
      iniFile.println("12345"); // n_seed1
      iniFile.println("67890"); // n_seed2
      iniFile.println(ZG.n_seedradius.to_double_string());
      iniFile.println(String.valueOf(city_lon[ZG.l_pcities.getSelected()]));
      iniFile.println(String.valueOf(city_lat[ZG.l_pcities.getSelected()]));

      File[] flist = new File("job"+File.separator).listFiles();
      for (int i = 0; i < flist.length; i++) {
        String fn = flist[i].getName().toUpperCase();
        if ((fn.endsWith(".PNG")) || (fn.endsWith(".TXT")) || (fn.endsWith(".ZOM")) || (fn.endsWith(".TXT2"))) flist[i].delete();
      }
      iniFile.flush();
      iniFile.close();

      // OS detection for different platforms...

      String os = System.getProperty("os.name").toLowerCase();
      boolean ok = true;
      if (os.startsWith("windows")) {
        Runtime.getRuntime().exec("gozom.bat");
      } else if (os.startsWith("linux")) {
        Runtime.getRuntime().exec("./gozom.sh");
      } else if (os.startsWith("mac os x")) {
        Runtime.getRuntime().exec("./gozom_mac.sh");
      } else {
        System.out.println("Unknown operating system: "+os+" - haven't got an executable for that.");
        ok = false;
      }

      if (ok) {
        current_step = 1;
        more_steps = true;
        while (more_steps) {
          if (!ZG.nextStep()) {
            try {
              Thread.sleep(50);
            } catch (Exception e) { e.printStackTrace(); }
          }
        }
      }

      ZG.b_execute.setIcon(ZG.go_button, null);
      ZG.b_execute.paintOn(ZG.gw.bi(), ZG.gw.g2d());
      ZG.gw.requestRepaint();
      ZG.gw.checkRepaint();
      ZG.gw.locked = false;
    } catch (Exception e) { e.printStackTrace(); }
  }

  ///////////////////////////////////////////////////////////////////////////////////////////
  // START HERE!

  public static void main(String[] args) {
    System.out.println("Zombie "+Z.Z_ver+" roaming on "+System.getProperty("os.name").toLowerCase());

    final String[] _args = args;

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Z zom = new Z();

        // Command-line arguments:

        if (_args.length >= 1) {
          for (int i = 0; i < _args.length; i++) {

            // Run as admin - exposes a "Save XML" button

            if (_args[i].toUpperCase().equals("/UNDEAD")) zom.admin = true;

            // Save every frame of the full application as a PNG

            else if (_args[i].toUpperCase().equals("/MOVIE")) zom.GUI_PNG = true;

            // Run web-server on this port, to have our zombie minds controlled by an overlord/android.

            else if (_args[i].startsWith("/PORT:")) zom.LISTEN_PORT = Integer.parseInt(_args[i].substring(6));
          }
        }
        zom.initNetwork();
        zom.loadINIFile();
        zom.ZG.initGUI();
        zom.current_colour = 0;
        zom.new_incidence[zom.current_colour].clear();
        zom.accum_inf[zom.current_colour].clear();
        zom.ZG.showParams(GXMLHelper.getChildNo(GXMLHelper.getTag(zom.root, "params"), "p", 0));
        zom.ZG.gw.requestRepaint();
      }
    });
  }


  class RunnerThread implements Runnable {

    @Override
    public void run() {
      gui_frame = 0;
      movie_no++;
      goGoGo();
    }
  }



}
