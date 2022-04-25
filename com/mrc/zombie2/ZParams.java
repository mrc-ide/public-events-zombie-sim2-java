package com.mrc.zombie2;

public class ZParams {
  String r0;
  String t_inf;
  String vacc_rad;
  String vacc_prob;
  int vacc_city;
  String n_seeds;
  String n_seedradius;
  int seed_city;
  
  void save(ZGui ZG) {
    r0 = ZG.k_r0.getText();
    t_inf = ZG.h_inf.getText();
    vacc_rad = ZG.i_rad.getText();
    vacc_prob = ZG.i_p.getText();
    vacc_city = ZG.l_cities.getSelected();
    n_seeds = ZG.n_seeds.getText();
    n_seedradius = ZG.n_seedradius.getText();
    seed_city = ZG.l_pcities.getSelected();
  }
  
  void retrieve(ZGui ZG) {
    ZG.k_r0.setText(r0);
    ZG.h_inf.setText(t_inf);
    ZG.i_rad.setText(vacc_rad);
    ZG.i_p.setText(vacc_prob);
    ZG.l_cities.setSelected(vacc_city);
    ZG.n_seeds.setText(n_seeds);
    ZG.n_seedradius.setText(n_seedradius);
    ZG.l_pcities.setSelected(seed_city);
    ZG.gw.requestRepaint();
    ZG.gw.checkRepaint();
  }
}
