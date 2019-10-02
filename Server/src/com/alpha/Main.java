package com.alpha;

import com.alpha.pre_process.ProjectHandler;
import com.alpha.server.HubServer;
import com.alpha.server.gui.ServerGUI;

public class Main
{
     public static ServerGUI gui;
     public static ProjectHandler ph;

     public static void startServer()
     {
          ServerGUI.startServer();
          Thread t = new HubServer(gui);
     }

     public static void main(String[] args)
     {
          gui = new ServerGUI();
     }
}
