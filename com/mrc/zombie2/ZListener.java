package com.mrc.zombie2;

import java.util.HashMap;

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

  @Override
  public void receiveMessage(HashMap<String, String> args) {
    String cmd = args.get("cmd");
    if (cmd.equals("set")) {
      String param = args.get("param");
      String value = args.get("value");
      if (param.equals("R0")) updateText(parent.ZG.k_r0, value);
      else if (param.equals("Tinf")) updateText(parent.ZG.h_inf, value);
      else if (param.equals("vaccpc")) updateText(parent.ZG.i_p, value);
      else if (param.equals("vaccrad")) updateText(parent.ZG.i_rad, value);
      else if (param.equals("vacccity")) parent.ZG.l_cities.setSelected(Integer.parseInt(value));
      else if (param.equals("seeds")) updateText(parent.ZG.n_seeds, value);
      else if (param.equals("seedrad")) updateText(parent.ZG.n_seedradius, value);
      else if (param.equals("seedcity")) parent.ZG.l_pcities.setSelected(Integer.parseInt(value));
      else System.out.println("Error - param "+param+" not known in SET");
    } else if (cmd.equals("T0")) parent.ZG.updateColSelect(parent.ZG.gt_col0);
      else if (cmd.equals("T1")) parent.ZG.updateColSelect(parent.ZG.gt_col1);
      else if (cmd.equals("T2")) parent.ZG.updateColSelect(parent.ZG.gt_col2);
      else if (cmd.equals("T3")) parent.ZG.updateColSelect(parent.ZG.gt_col3);
      else if (cmd.equals("D0")) parent.ZG.clearCol(parent.ZG.gt_col0);
      else if (cmd.equals("D1")) parent.ZG.clearCol(parent.ZG.gt_col1);
      else if (cmd.equals("D2")) parent.ZG.clearCol(parent.ZG.gt_col2);
      else if (cmd.equals("D3")) parent.ZG.clearCol(parent.ZG.gt_col3);
      else if (cmd.equals("GO")) parent.runSim();
      else System.out.println("Unknown command "+cmd);
  }
}
