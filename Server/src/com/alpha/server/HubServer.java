package com.alpha.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ServerSocketFactory;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.alpha.Main;
import com.alpha.server.gui.ServerGUI;
import com.alpha.server.helper.RegexParser;

public class HubServer extends Thread
{

     private ServerSocket server;
     private ClientCollector collector;
     private OutputProcessor op;
     private ArrayList<ClientThread> connectedClients;
     private PlainDocument pd;
     private File test;

     public HubServer(ServerGUI handle)
     {
          op = new OutputProcessor(this);
          collector = new ClientCollector(this);
          connectedClients = new ArrayList<ClientThread>();
          ServerSocketFactory fact = ServerSocketFactory.getDefault();

          try
          {
               server = fact.createServerSocket(5000);
          } catch (IOException e)
          {
               e.printStackTrace();
          }

          pd = new PlainDocument();
          test = new File(".\\test\\testProject\\saveTest");
          /*String save = "";
          try
          {
               FileReader fr = new FileReader(test);
               int i;
               while((i = fr.read()) != -1)
               {
                    save += (char)i;
               }
               fr.close();
          } catch (FileNotFoundException e)
          {
               // TODO Auto-generated catch block
               e.printStackTrace();
          } catch (IOException e)
          {
               // TODO Auto-generated catch block
               e.printStackTrace();
          }
          //System.out.println(save);
          try
          {
               pd.insertString(0, save, null);
          } catch (BadLocationException e)
          {
               // TODO Auto-generated catch block
               e.printStackTrace();
          }*/
          

          this.start();
          op.start();
          collector.start();
     }

     /**
      * <h1> HubServer Run </h1>
      * This function keeps a running while that takes from the 
      * Output Processor output queue and relays messages to all 
      * the clients according to instructions.
      * */
     public void run()
     {
          while (!ServerGUI.getServerClosing())
          {
               Command com = OutputProcessor.takeFromOutputQueue();

               if (com != null)
               {
                    int size = connectedClients.size();
                    if(com.output() != null)
                    {
                         for(int i = 0; i < size; i++)
                         {
                              ClientThread client = connectedClients.get(i);
                              if(com.output().equals("sync")) // send sync msg to everyone if msg is sync
                              {
                                   System.out.println("Time when sync sent to Client " + (i+1) + ": " + getCurrentTimeStamp());
                                   client.talkToClient(syncMsg());
                              }
                              else if(!client.equals(com.sentFrom())) // if not, send to all client that have not sent the msg
                              {
                                   client.talkToClient(com.output());
                              }
                         }
                         updateFile(com.output());
                    }
                    else
                    {
                         connectedClients.remove(connectedClients.indexOf(com.sentFrom()));
                         updateTextArea();
                         System.out.println(com.sentFrom().getClient().getInetAddress() + " removed"); //System print
                    }
               }
               if (connectedClients.size() == 0)
               {
                    Main.gui.setText("No clients connected");
               }
          } // end while
          try
          {
               server.close();
          } catch (IOException e)
          {
               e.printStackTrace();
          }
          //Saving text from Plain Document to the saved file
          //This will be replaced by a call to ProjectHandler to complete saving files
          try
          {
               PrintWriter pw = new PrintWriter(test);
               String text = pd.getText(0, pd.getLength());
               text = text.replaceAll("(?!\\r)\\n", "\r\n");

               pw.write(text);

               pw.close();
          } catch (FileNotFoundException e)
          {
               e.printStackTrace();
          } catch (BadLocationException e)
          {
               e.printStackTrace();
          }
     }
     
     /**
      * @return ArrayList<ClientThread> returns a list of all connected Clients
      * */
     public ArrayList<ClientThread> getConnectedClients()
     {
          return connectedClients;
     }
     /**
      * Adds Clients to the list of clients connected to the server
      * @param e ClientThread to add
      */
     private void addClient(ClientThread e)
     {
          connectedClients.add(e);
          connectedClients.get(connectedClients.size() - 1).startThreads();
     }

     /**
      * Updates the Server GUI TextArea
      */
     private void updateTextArea()
     {
          int size = connectedClients.size();
          String text = "";
          for (int i = 0; i < size; i++)
          {
               text += connectedClients.get(i).getClient().getRemoteSocketAddress().toString() + "\n";
          }
          Main.gui.setText(text);
     }
     
     /**
      * Updates the Plain Document with changes
      * This function will probably be moved to ProjectHandler
      * @param com msg in protocol to add
      */
     private void updateFile(String com)
     {
          ArrayList<String> check = RegexParser.matches("\\[([+|-])\\]\\[off(\\d+)\\]\\[len(\\d+)\\]\"(.*?)\"", com);
          /*
          for(int i = 1; i < check.size(); i++)
          {
               System.out.println(i + ": " + check.get(i));
          }
          */
          if(check.size() <= 1)
          {
               return;
          }
          int offset = Integer.valueOf(check.get(2)).intValue();
          int length = Integer.valueOf(check.get(3)).intValue();
          String str = check.get(4);

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
               if (check.get(1).equals("+"))
               {
                    pd.insertString(offset, str, null);
               } else if (check.get(1).equals("-"))
               {
                    pd.remove(offset, length);
               }
          } catch (BadLocationException e)
          {
               e.printStackTrace();
          }
     }

     /**
      * 
      * @param c
      */
     private void updateClient(ClientThread c)
     {
          try
          {
               String[] content = pd.getText(0, pd.getLength()).split("\\n");
               
               for(String s : content)
               {
                    System.out.println(s);
               }
               
               int off = 0;

               for (int i = 0; i < content.length; i++)
               {
                    String msg = "";

                    msg += "[+]";

                    msg += "[off" + off + "]";
                    msg += "[len" + content[i].length();
                    
                    if(i != content.length - 1)
                    {
                         msg += "newLine]";
                    }
                    else
                    {
                         msg += "]";
                    }

                    msg += "\"" + content[i] + "\"";

                    c.talkToClient(msg);
                    off += content[i].length() + 1;
               }
          } catch (BadLocationException e)
          {
               e.printStackTrace();
          }
     }

     private String syncMsg()
     {
          String msg = "";
          try
          {
               msg = pd.getText(0, pd.getLength());
               msg = "[0][off0][len" + pd.getLength() + "]" + "\"" + msg + "\"";
               msg = "{" + msg + "}";
          } catch (BadLocationException e)
          {
               e.printStackTrace();
          }
          return msg;
     }
     
     private class ClientCollector extends Thread
     {
          private HubServer hub;

          public ClientCollector(HubServer hub)
          {
               this.hub = hub;
          }

          public void run()
          {
               try
               {
                    hub.server.getInetAddress();
                    Main.gui.addText("Waiting for client on port " + hub.server.getLocalPort() + " at address " + "192.168.1.86");
                    while (!ServerGUI.getServerClosing())
                    {
                         Socket client = null;
                         client = hub.server.accept();

                         System.out.println("Connected to " + client.getRemoteSocketAddress());
                         ClientThread ct = new ClientThread(client);
                         hub.addClient(ct);
                         hub.updateClient(ct);
                         updateTextArea();
                    }
               } catch (IOException e)
               {
                    Main.gui.addText("Not accepting any Clients now...");
                    //e.printStackTrace();
               }
          }
     }

     public static String getCurrentTimeStamp() {
          return LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
     }
}
