package com.alpha.server.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class ServerGUI extends JFrame
{
     private static final long serialVersionUID = 1L;

     private JTextArea textArea;
     private static boolean closingServer;
     private CustomListener lis;

     public ServerGUI()
     {
          super("Server");

          // Create an object of JFileChooser class 
          JFileChooser j = new JFileChooser(".\\test");
          j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          
          boolean valid = false;

          // Invoke the showsOpenDialog function to show the save dialog 
          while(!valid)
          {
               int r = j.showOpenDialog(null);
               // If the user selects a file 
               if (r == JFileChooser.APPROVE_OPTION)
               {
                    // Set the label to the path of the selected directory 
                    File fi = new File(j.getSelectedFile().getAbsolutePath());
                    
                    if(fi.isDirectory())
                    {
                         //initialize the ProjectHandler class and send it to HubServer
                         for(int i = 0; i < fi.listFiles().length; i++)
                         {
                              File f = fi.listFiles()[i];
                              if(f.getName().contains(".project"))
                              {
                                   valid = true;
                                   System.out.println("initializing project...");
                                   break;
                              }
                         }
                         if(!valid)
                         {
                              JOptionPane.showMessageDialog(this, "Please pick a valid project folder", "No Project Folder", JOptionPane.ERROR_MESSAGE);
                         }
                    }
                    else
                    {
                         JOptionPane.showMessageDialog(this, "Please pick a folder", "No Valid Folder", JOptionPane.ERROR_MESSAGE);
                    }
               }
               else
               {
                    break;
               }
          }
          
          initializeMainServerGUI();
     }

     public void initializeMainServerGUI()
     {

          lis = new CustomListener();
          closingServer = false;
          
          textArea = new JTextArea();
          textArea.setPreferredSize(new Dimension(500, 200));
          textArea.setEditable(false);
          JScrollPane sp = new JScrollPane(textArea);
          
          add(sp, BorderLayout.CENTER);

          JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
          buttonPane.setPreferredSize(new Dimension(500, 100));

          JButton start = new JButton("Start");
          start.setPreferredSize(new Dimension(200, 100));
          start.addActionListener(lis);
          buttonPane.add(start);
          JButton stop = new JButton("Stop");
          stop.setPreferredSize(new Dimension(200, 100));
          stop.addActionListener(lis);
          buttonPane.add(stop);

          add(buttonPane, BorderLayout.PAGE_END);

          addWindowListener(lis);
          
          setPreferredSize(new Dimension(500, 300));
          setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
          pack();
          setVisible(true);
     }
     
     public void setText(String text)
     {
          textArea.setText(text + "\n");
     }
     
     public void addText(String text)
     {
          textArea.append(text + "\n");
     }

     public static boolean getServerClosing()
     {
          return closingServer;
     }

     public static void closeServer()
     {
          closingServer = true;
     }
     
     public static void startServer()
     {
          closingServer = false;
     }
}
