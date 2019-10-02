package com.alpha.client.gui;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;

import com.alpha.client.Client;
import com.alpha.client.helper.RegexParser;

public class Editor extends Thread
{
     private JFrame frame;
     private JTextArea textArea;

     private CustomListener lis;

     private BlockingQueue<String> updateCom;
     
     private static boolean windowClosing;

     // Constructor 
     public Editor(Client c)
     {
          super();
          /*try
          {
               // Set metl look and feel 
               UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
          
               // Set theme to ocean 
               MetalLookAndFeel.setCurrentTheme(new OceanTheme());
          } catch (Exception e)
          {
               e.printStackTrace();
          }*/

          lis = new CustomListener(c, this);
          updateCom = new LinkedBlockingQueue<String>();
          windowClosing = false;
          //Frame
          frame = new JFrame("Editor");
          frame.setPreferredSize(new Dimension(500, 500));

          // Text component 
          textArea = new JTextArea();
          textArea.getDocument().addDocumentListener(lis);

          //textArea.addKeyListener(lis);
          //frame.addKeyListener(lis);
          frame.addWindowListener(lis);
          
          setupMenuBar();
          
          JScrollPane sp = new JScrollPane(textArea);
          
          sp.setPreferredSize(new Dimension(500, 500));
          
          frame.add(sp);
          frame.setSize(500, 500);
          frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
          frame.pack();
          frame.setVisible(true);
     }

     private void setupMenuBar()
     {
        //REDO MENU BAR
          //Especially New, Open, Save
          //Function of New, Open, Save will depend on where
          //Document will be saved...
          //either in the server or one of the clients...

          // Create a menubar 
          JMenuBar mb = new JMenuBar();

          // Create a menu for menu 
          JMenu m1 = new JMenu("File");

          // Create menu items 
          JMenuItem mi1 = new JMenuItem("New");
          JMenuItem mi2 = new JMenuItem("Open");
          JMenuItem mi3 = new JMenuItem("Save");
          JMenuItem mi12 = new JMenuItem("Save As");
          JMenuItem mi9 = new JMenuItem("Print");
          JMenuItem mc = new JMenuItem("Close");

          // Add action listener 
          mi1.addActionListener(lis);
          mi2.addActionListener(lis);
          mi3.addActionListener(lis);
          mi9.addActionListener(lis);
          mi12.addActionListener(lis);
          mc.addActionListener(lis);
          m1.add(mi1);
          m1.add(mi2);
          m1.add(mi3); 
          m1.add(mi12);
          m1.add(mi9);
          m1.add(mc);
       
          // Create amenu for menu 
          JMenu m2 = new JMenu("Edit");

          // Create menu items 
          JMenuItem mi4 = new JMenuItem("Cut");
          JMenuItem mi5 = new JMenuItem("Copy");
          JMenuItem mi6 = new JMenuItem("Paste");

          // Add action listener 
          mi4.addActionListener(lis);
          mi5.addActionListener(lis);
          mi6.addActionListener(lis);

          m2.add(mi4);
          m2.add(mi5);
          m2.add(mi6);

         

          JMenu mv = new JMenu("View");
          
          mv.addActionListener(lis);
          
          JMenuItem mi10 = new JMenuItem("Zoom In");
          JMenuItem mi11 = new JMenuItem("Zoom Out");
          
          KeyStroke ctrlPlusKeyStroke = KeyStroke.getKeyStroke('=', InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
          mi10.setAccelerator(ctrlPlusKeyStroke);
          KeyStroke ctrlMinusKeyStroke = KeyStroke.getKeyStroke('-', InputEvent.CTRL_DOWN_MASK);
          mi11.setAccelerator(ctrlMinusKeyStroke);
         
          mi10.addActionListener(lis);
          mi11.addActionListener(lis);
          
          //textArea.addKeyListener(lis);
          //frame.addKeyListener(lis);
         
          mv.add(mi10);
          mv.add(mi11);
          
          mb.add(m1);
          mb.add(m2);  
          mb.add(mv);
         

          frame.setJMenuBar(mb);
     }
     
     @Override
     public void run()
     {
          while (!Editor.getWindowClosing())
          {
               String update = updateCom.poll();
               if(update != null)
               {
                    updateDoc(update);
               }
          }
          frame.dispose();
     }

     public void updateDoc(String com)
     {
          textArea.getDocument().removeDocumentListener(lis);

          ArrayList<String> check = RegexParser.matches("\\[(.*?)\\]\\[([+-0])\\]\\[off(\\d+)\\]\\[len(\\d+)\\]\"(.*?)\"", com);
          
          /*
          for(int i = 1; i < check.size(); i++)
          {
               System.out.println(i + ": " + check.get(i));
          }
          */
          
          int offset = Integer.valueOf(check.get(3)).intValue();
          int length = Integer.valueOf(check.get(4)).intValue();
          String str = check.get(5);

          //"\n\n1" length = 3: str.length(): 15
          if (str.length() != length && !str.equals(""))
          {
               String temp = str;
               int n = str.length() - length;
               n = n / 6;

               int[] offsetArr = new int[n];

               for (int i = 0; i < n; i++)
               {
                    offsetArr[i] = temp.indexOf("newLine");
                    temp = str.substring(offsetArr[i] + 7);
               }

               if (n == 1)
               {
                    str = str.substring(0, offsetArr[0]) + "\n" + str.substring(offsetArr[0] + 7);
               } else
               {
                    String temp2 = str.substring(0, offsetArr[0]);
                    for (int i = 1; i < n; i++)
                    {
                         temp2 += "\n" + str.substring(offsetArr[0] + 7, offsetArr[i]);
                    }
                    temp2 += str.substring(offsetArr[n - 1] + 7);
                    str = temp2;
               }
          }

          try
          {
               if (check.get(2).equals("+"))
               {
                    textArea.getDocument().insertString(offset, str, null);
               } else if (check.get(2).equals("-"))
               {
                    textArea.getDocument().remove(offset, length);
               } else if(check.get(2).equals("0"))
               {
                    textArea.getDocument().remove(0, textArea.getDocument().getLength());
                    textArea.getDocument().insertString(0, str, null);
               }
          } catch (BadLocationException e)
          {
     
               e.printStackTrace();
          }

          textArea.getDocument().addDocumentListener(lis);
     }

     public void addUpdate(String com)
     {
          updateCom.add(com);
     }

     /**
      * @return the frame
      */
     public JFrame getFrame()
     {
          return frame;
     }

     /**
      * @param frame the frame to set
      */
     public void setFrame(JFrame frame)
     {
          this.frame = frame;
     }

     /**
      * @return the textArea
      */
     public JTextArea getTextArea()
     {
          return textArea;
     }

     /**
      * @param textArea the textArea to set
      */
     public void setTextArea(JTextArea textArea)
     {
          this.textArea = textArea;
     }

     public static boolean getWindowClosing()
     {
          return windowClosing;
     }
     
     public static void closeWindow()
     {
          windowClosing = true;
     }
}