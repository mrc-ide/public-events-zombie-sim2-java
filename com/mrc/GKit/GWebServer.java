package com.mrc.GKit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class GWebServer {
  private HttpServer server;
  private int port = 8080;
  private GWebListener listener;
    
  public void createServer() {
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/", new WebHandler());
      server.start();
    } catch (Exception e) {
      if (e instanceof java.net.BindException) {
        System.out.println("The network port "+port+" is already in use. Web Server disabled");
        server = null;
      }
    }
  }
    
  public GWebServer(GWebListener wl)  {
    listener = wl;
    createServer();
  }
    
  public void setPort(int p) {
    port = p;
  }
    
  public void setEnabled(boolean en) {
    if (en) { 
      if (server != null) {
        server.start();
      } else createServer();
      
    } else {
      if (server!=null) {
        server.stop(0);
        server = null;
      }
    }
  }
    
  class WebHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
      HashMap<String, String> key_values = new HashMap<String, String>();
      String query = null;
      if (t.getRequestMethod().equals("POST")) {
        InputStreamReader in=null; 
        try {
          in = new InputStreamReader(t.getRequestBody(), "utf-8");
          BufferedReader br = new BufferedReader(in);
          if (br!=null) {
            query = br.readLine();
            if (query != null) {
              query = URLDecoder.decode(query, "UTF-8");
            }
          }
           
        } catch (Exception e) { 
          e.printStackTrace(); 
        } finally {
          if (in!=null) in.close();
        }
      } else if (t.getRequestMethod().equals("GET")) {
        query = t.getRequestURI().getQuery();
      } else {
        System.out.println("Unknown request method "+t.getRequestMethod());
      }
      
      String[] bits = query.split("&");
      for (int i=0; i < bits.length; i++) {
        String[] bit = bits[i].split("=");
        key_values.put(bit[0], bit[1]);
      }

      String response = "OK";
      t.sendResponseHeaders(200, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
      if (listener != null) {
        listener.receiveMessage(key_values);
      }
    }
  }
}
