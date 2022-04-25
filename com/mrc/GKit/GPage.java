/* GPage.java, part of the Global Epidemic Simulation v1.1 BETA
/* GKit: A page interface: all pages must implement this 
/*
/* Copyright 2012, MRC Centre for Outbreak Analysis and Modelling
/* 
/* Licensed under the Apache License, Version 2.0 (the "License");
/* you may not use this file except in compliance with the License.
/* You may obtain a copy of the License at
/*
/*       http://www.apache.org/licenses/LICENSE-2.0
/*
/* Unless required by applicable law or agreed to in writing, software
/* distributed under the License is distributed on an "AS IS" BASIS,
/* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/* See the License for the specific language governing permissions and
/* limitations under the License.
*/
/*
 * CHANGES:
 * 1.1 - Change to standard Java XML import
 */


package com.mrc.GKit;

import org.w3c.dom.Element;

public interface GPage {
  public int getMinEvent();
  public int getMaxEvent();  
  public void doFunction(int func,Object component);
  public void createDefaultXML(Element root);
  public void loadXML(Element root);
  public void saveXML(Element root);
  public void clear();
}
