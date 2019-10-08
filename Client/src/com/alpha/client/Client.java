package com.alpha.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.alpha.client.gui.Editor;
import com.alpha.client.helper.RegexParser;

public class Client extends Thread
{
     /* 
      * Current protocol
      * 
      * {DocumentName EventType Offset Length StringValue}
      * 
      * The whole message (0)
      * DocumentName -> [DocumentName] (1)
      * EventType -> [+ (EventType.INSERT) OR - (EventType.REMOVE) OR 0 (sync)] (2)
      * Offset -> [off, #] (3)
      * Length -> [len, #] (4)
      * StringValue -> "val" (5)
      * 
      * Example:
      * 
      * {[+][off12][len1]"d"}
      **/

     private Socket clientSocket;
     private String serverName;
     private int port;
     private Editor e;

     private ConcurrentLinkedQueue<String> com;
     private ThreadWriter writer;

     private boolean writerClose;

     public Client(String serverName, int port)
     {
          this.serverName = serverName;
          this.port = port;

          com = new ConcurrentLinkedQueue<String>();
          writer = new ThreadWriter();
          writerClose = false;

          System.out.println("Connecting to " + this.serverName + " on port " + this.port);
          try
          {
               clientSocket = new Socket(InetAddress.getByName(this.serverName), this.port);
               //clientSocket = new Socket(serverName, port);
               System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());

          } catch (Exception e)
          {
               e.printStackTrace();
          }
          
          
          e = new Editor(this);

          //send("Hello from " + clientSocket.getLocalSocketAddress() + " \r\n");

          this.startThreads();
          e.start();
     }

     public void startThreads()
     {
          this.start();
          writer.start();
     }

     @Override
     public void run()
     {
          try (BufferedReader cin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));)
          {
               while (!Editor.getWindowClosing())
               {
                    char temp;
                    String msg = "";
                    while (cin.ready() && !Editor.getWindowClosing())
                    {
                         temp = (char) cin.read();
                         msg += temp;
                         ArrayList<String> check = RegexParser.matches("^\\{(.*)\\}$", msg);
                         if (temp == '\n')
                         {
                              msg = "";
                         }
                         if (!check.isEmpty())
                         {
                              if (check.get(1).equals("exit"))
                              {
                                   Editor.closeWindow();
                              } else
                              {
                                   // System.out.println(this.getName());
                                   // System.out.println("Command: " + check.get(1));
                                   // System.out.println("Time " + check.get(1) + " received: " + getCurrentTimeStamp());
                                   e.addUpdate(check.get(1));
                                   msg = "";
                              }
                         }
                    }
               }
               while (!writerClose)
               {
                    System.out.println("Waiting for ThreadWriter to close...");
               }
               clientSocket.close();
               System.out.println("Socket closed");
          } catch (IOException e)
          {
               e.printStackTrace();
          }
     }

     public void send(String msg)
     {
          msg = "{" + msg + "}";
          com.add(msg);
     }

     private class ThreadWriter extends Thread
     {
          @Override
          public void run()
          {

               try (PrintWriter cpw = new PrintWriter(clientSocket.getOutputStream(), true);)
               {
                    while (!Editor.getWindowClosing())
                    {
                         if (!com.isEmpty())
                         {
                              String s = com.poll();
                              byte[] encoded = s.getBytes(Charset.forName("UTF-8"));
                              //System.out.println(new String(encoded, Charset.forName("UTF-8")));
                              System.out.println("Time " + s + " sent: " + getCurrentTimeStamp());
                              cpw.println(new String(encoded, Charset.forName("UTF-8")));

                         }
                    }
                    byte[] encoded = "{exit}".getBytes(Charset.forName("UTF-8"));
                    cpw.println(new String(encoded, Charset.forName("UTF-8")));
                    writerClose = true;
               } catch (IOException e)
               {
                    e.printStackTrace();
               }
          }
     }

     public static String getCurrentTimeStamp()
     {
          return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
     }

     public static void main(String[] args)
     {
          String serverName = args[0];
          int port = Integer.parseInt(args[1]);

          new Client(serverName, port);
     }
}