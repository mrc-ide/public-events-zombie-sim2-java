package com.mrc.zombie2;

import java.util.HashMap;

import com.mrc.GKit.GList;
import com.mrc.GKit.GListHeader;
import com.mrc.GKit.GTextEntry;
import com.mrc.GKit.GWebListener;

public class ZListener implements GWebListener {
  private Z parent;

  ZListener(Z parent) {
    this.parent = parent;
  }

  private void updateText(GTextEntry g, String v) {
    g.setText(v);
    g.paintOn(parent.ZG.gw.bi(), parent.ZG.gw.g2d());
    parent.ZG.gw.requestRepaint();
    parent.ZG.gw.checkRepaint();
  }

  private void updateSelector(GList gl, GListHeader glh, int index) {
    gl.setSelected(index);
    glh.paintOn(parent.ZG.gw.bi(), parent.ZG.gw.g2d());
    parent.ZG.gw.requestRepaint();
    parent.ZG.gw.checkRepaint();
  }

  @Override
  public String receiveMessage(HashMap<String, String> args) {
    String cmd = args.get("cmd");
    if (cmd.equals("BUSY")) return String.valueOf(parent.ZG.gw.locked ? "WAIT" : "STOP_WAITING");

    if (cmd.equals("set")) {

      String[] params = args.get("param").split(";");
      String[] values = args.get("value").split(";");

      for (int i = 0; i < params.length; i++) {
        if (params[i].equals("R0")) updateText(parent.ZG.k_r0, values[i]);
        else if (params[i].equals("Tinf")) updateText(parent.ZG.h_inf, values[i]);
        else if (params[i].equals("vaccpc")) updateText(parent.ZG.i_p, values[i]);
        else if (params[i].equals("vaccrad")) updateText(parent.ZG.i_rad, values[i]);
        else if (params[i].equals("vacccity")) updateSelector(parent.ZG.l_cities, parent.ZG.lh_cities, Integer.parseInt(values[i]));
        else if (params[i].equals("seeds")) updateText(parent.ZG.n_seeds, values[i]);
        else if (params[i].equals("seedrad")) updateText(parent.ZG.n_seedradius, values[i]);
        else if (params[i].equals("seedcity")) updateSelector(parent.ZG.l_pcities, parent.ZG.lh_pcities, Integer.parseInt(values[i]));
        else if (params[i].equals("mobility")) {

          int val = Integer.parseInt(values[i]);
          if (val == 1) {
            parent.k_cut = 180;
            parent.k_a = 4;
            parent.k_b = 6;

          } else if (val == 2) {
            parent.k_cut = 180;
            parent.k_a = 4;
            parent.k_b = 4;

          } else if (val == 3) {
            parent.k_cut = 180;
            parent.k_a = 4;
            parent.k_b = 3;

          } else if (val == 4) {
            parent.k_cut = 3000;
            parent.k_a = 4;
            parent.k_b = 0.5;
          }
        }

        else if (params[i].equals("net_msg")) { /* Ignore, but recognise */ }
        else System.out.println("Error - param "+params[i]+" not known in SET");
      }

      for (int i = 0; i < params.length; i++) {
        if (params[i].equals("net_msg")) { 
          if (values[i].equals("R0")) { parent.ZG.updateColSelect(parent.ZG.gt_col0); parent.runSim(); return "WAIT"; }
          else if (values[i].equals("R1")) { parent.ZG.updateColSelect(parent.ZG.gt_col1);  parent.runSim(); return "WAIT"; }
          else if (values[i].equals("R2")) { parent.ZG.updateColSelect(parent.ZG.gt_col2);  parent.runSim(); return "WAIT"; }
          else if (values[i].equals("R3")) { parent.ZG.updateColSelect(parent.ZG.gt_col3);  parent.runSim(); return "WAIT"; }
          else if (values[i].equals("D0")) { parent.ZG.clearCol(parent.ZG.gb_ecol0); return "STOP_WAITING"; }
          else if (values[i].equals("D1")) { parent.ZG.clearCol(parent.ZG.gb_ecol1); return "STOP_WAITING"; }
          else if (values[i].equals("D2")) { parent.ZG.clearCol(parent.ZG.gb_ecol2); return "STOP_WAITING"; }
          else if (values[i].equals("D3")) { parent.ZG.clearCol(parent.ZG.gb_ecol3); return "STOP_WAITING"; }
        }
      }
    }
    return "OK";
  }
}
