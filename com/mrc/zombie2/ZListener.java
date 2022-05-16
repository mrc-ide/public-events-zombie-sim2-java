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
    if (cmd.equals("set")) {
      String param = args.get("param");
      String value = args.get("value");
      if (param.equals("R0")) updateText(parent.ZG.k_r0, value);
      else if (param.equals("Tinf")) updateText(parent.ZG.h_inf, value);
      else if (param.equals("vaccpc")) updateText(parent.ZG.i_p, value);
      else if (param.equals("vaccrad")) updateText(parent.ZG.i_rad, value);
      else if (param.equals("vacccity")) updateSelector(parent.ZG.l_cities, parent.ZG.lh_cities, Integer.parseInt(value));
      else if (param.equals("seeds")) updateText(parent.ZG.n_seeds, value);
      else if (param.equals("seedrad")) updateText(parent.ZG.n_seedradius, value);
      else if (param.equals("seedcity")) updateSelector(parent.ZG.l_pcities, parent.ZG.lh_pcities, Integer.parseInt(value));
      else System.out.println("Error - param "+param+" not known in SET");
      return "OK";

    } else if (cmd.equals("R0")) { parent.ZG.updateColSelect(parent.ZG.gt_col0); parent.runSim(); return "WAIT"; }
      else if (cmd.equals("R1")) { parent.ZG.updateColSelect(parent.ZG.gt_col1);  parent.runSim(); return "WAIT"; }
      else if (cmd.equals("R2")) { parent.ZG.updateColSelect(parent.ZG.gt_col2);  parent.runSim(); return "WAIT"; }
      else if (cmd.equals("R3")) { parent.ZG.updateColSelect(parent.ZG.gt_col3);  parent.runSim(); return "WAIT"; }
      else if (cmd.equals("D0")) { parent.ZG.clearCol(parent.ZG.gb_ecol0); return "STOP_WAITING"; }
      else if (cmd.equals("D1")) { parent.ZG.clearCol(parent.ZG.gb_ecol1); return "STOP_WAITING"; }
      else if (cmd.equals("D2")) { parent.ZG.clearCol(parent.ZG.gb_ecol2); return "STOP_WAITING"; }
      else if (cmd.equals("D3")) { parent.ZG.clearCol(parent.ZG.gb_ecol3); return "STOP_WAITING"; }
      else if (cmd.equals("BUSY")) return String.valueOf(parent.ZG.gw.locked ? "WAIT" : "STOP_WAITING");
    return "OK";
  }
}
