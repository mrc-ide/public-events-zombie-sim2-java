/* GXMLHelper.java, part of the Global Epidemic Simulation v1.1 BETA
/* GKit: A collection of functions for Java XML (DOM). 
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

package com.mrc.GKit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GXMLHelper {
  
  public static Node getTag(Node root, String name) {
    NodeList nl = root.getChildNodes();
    Node result = null;
    for (int i=0; i<nl.getLength(); i++) {
      if (nl.item(i).getNodeName().equals(name)) {
        result = nl.item(i);
        i=nl.getLength();
      }
    }
    return result;
  }

  
  public static String getTagValue(Node root, String name) {
    Node tag = getTag(root,name);
   return tag.getTextContent();
  }
  
  public static String getTagAttribute(Node root, String name, String attr) {
    return getAttribute(getTag(root,name),attr);
  }  
  
  
  public static Element addTag(Node parent, String name) {
    Element e = parent.getOwnerDocument().createElement(name);
    parent.appendChild(e);
    return e;
  }
  
  public static Element addTag(Node parent, String name,String value) {
    Element e = addTag(parent,name);
    e.setTextContent(value);
    return e;
  }

  
  public static String getAttribute(Node parent, String attname)  {
    Node n = parent.getAttributes().getNamedItem(attname);
    if (n==null) return null;
    else return n.getTextContent();
  }
  
  public static void setAttribute(Node parent, String attrname, String attrvalue) {
    Attr a = parent.getOwnerDocument().createAttribute(attrname);
    a.setTextContent(attrvalue);
    parent.getAttributes().setNamedItem(a);
  }
  
  public static int countChildren(Node parent) {
    int i=0;
    for (int j=0; j<parent.getChildNodes().getLength(); j++) {
      if (parent.getChildNodes().item(j).getNodeType()==Node.ELEMENT_NODE) i++;
    }
    return i;
  }
  
  public static int countChildren(Node parent,String tag) {
    int i=0;
    for (int j=0; j<parent.getChildNodes().getLength(); j++) {
      if (parent.getChildNodes().item(j).getNodeType()==Node.ELEMENT_NODE) {
        if (parent.getChildNodes().item(j).getNodeName().equals(tag)) i++;
      }
    }
    return i;
  }
  
  public static Node getChildNo(Node parent,String tag,int n) {
    int i=0;
    Node result=null;
    for (int j=0; j<parent.getChildNodes().getLength(); j++) {
      if (parent.getChildNodes().item(j).getNodeType()==Node.ELEMENT_NODE) {
        if (parent.getChildNodes().item(j).getNodeName().equals(tag)) {
          if (i==n) {
            result = parent.getChildNodes().item(j);
            j=parent.getChildNodes().getLength();
          }
          i++;
        }
      }
    }
    return result;
  }
  
  public static Node getChildNo(Node parent,int n) {
    int i=0;
    Node result=null;
    for (int j=0; j<parent.getChildNodes().getLength(); j++) {
      if (parent.getChildNodes().item(j).getNodeType()==Node.ELEMENT_NODE) {
        if (i==n) {
          result = parent.getChildNodes().item(j);
          j=parent.getChildNodes().getLength();
        }
        i++;
      }
    }
    return result;
  }
  
  public static Node getTagWhereAttr(Node parent, String tag, String attr, String attrValue) {
    Node resultNode = null;
    int count = countChildren(parent,tag);
    for (int i=0; i<count; i++) {
      Node n = getChildNo(parent,tag,i);
      if (n.getAttributes().getNamedItem(attr).getTextContent().equals(attrValue)) {
        resultNode=n;
        i=count;
      }
    }
    return resultNode;
  }
  
  public static String getAttrFromTagWhereAttr(Node parent, String tagname, String matchattr, String matchval, String resultattr) {
    // If you have... <tag>  <param name="x1" value="abc" /> <param name="x2" value="def"/> ... </tag>, then 
    // call with (tag_node,"param","name","x2","value") to return "def"
    
    String result = null;
    Node resultNode = null;
    int count = countChildren(parent,tagname);
    for (int i=0; i<count; i++) {
      Node n = getChildNo(parent,tagname,i);
      NamedNodeMap nnm = n.getAttributes();
      for (int j=0; j<nnm.getLength(); j++) {
        Node n2 = nnm.getNamedItem(matchattr);
        if (n2.getTextContent().equals(matchval)) {
          resultNode=n;
          j=nnm.getLength();
          i=count;
        }
      }
    }
    if (resultNode!=null) {
      try {
        result=getAttribute(resultNode,resultattr);
      } catch (Exception e) {}
    }
    return result;
  }
  
  public static Element newDocument(String roottag) {
    Element root=null;
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();
      root = doc.createElement(roottag);
      doc.appendChild(root);
    } catch (Exception e) { e.printStackTrace(); }
    return root;
  }
  
  public static Element loadDocument(String file) {
    Element root = null;
    try {
      File f = new File(file);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(f);
      root=doc.getDocumentElement();
      root.normalize();
    } catch (Exception e) { e.printStackTrace(); }
    return root;
  }
  

  
  public static void writeXML(Element root,String file) {
    try {
      TransformerFactory tf = TransformerFactory.newInstance();
      tf.setAttribute("indent-number", 2);
      Transformer t = tf.newTransformer();
      
      t.setOutputProperty(OutputKeys.INDENT,"yes");
      t.setOutputProperty(OutputKeys.METHOD,"xml");
      
      
      StreamResult res = new StreamResult(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
      DOMSource src = new DOMSource(root.getOwnerDocument());
      t.transform(src,res);
      
            
    } catch (Exception e) { e.printStackTrace(); }
    
    
  }
}
