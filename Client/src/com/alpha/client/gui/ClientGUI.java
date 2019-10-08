package com.alpha.client.gui;

import java.util.ArrayList;

import com.alpha.client.Client;

public class ClientGUI
{
     private ArrayList<Editor> editors;
     
     Client c;
     
     public ClientGUI(Client c)
     {
          editors = new ArrayList<Editor>();
          this.c = c;
          editors.add(new Editor(c));
     }
     
     public void addEditor(String path)
     {
          editors.add(new Editor(c, path)); 
     }
     public void start()
     {
    	 for(Editor e : editors)
    	 {
    		 e.start();
    	 }
     }
}
