package com.alpha.server;

import java.net.InetAddress;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.alpha.Main;
import com.alpha.server.gui.ServerGUI;
import com.alpha.server.helper.Data;

public class OutputProcessor extends Thread
{

     private static BlockingQueue<Command> clientOutputQueue;
     private static BlockingQueue<Data> clientInputQueue;
     private static Date latestTime;
     private static InetAddress latestClient;

     public OutputProcessor(HubServer hub)
     {
          clientOutputQueue = new LinkedBlockingDeque<Command>();
          clientInputQueue = new LinkedBlockingDeque<Data>();
          latestTime = new Date();
          latestClient = null;
          Main.gui.addText("Queues Active");
     }

     public void run()
     {
          while (!ServerGUI.getServerClosing())
          {
               Data out = clientInputQueue.poll();
               if (out != null)
               {
                    out.getData().process();
                    System.out.println(out.getData().output() + " " + out.getDiff());
                    OutputProcessor.addToOutputQueue(out);
                    if (out.getDiff() < 1000 && out.getDiff() > 0 && !latestClient.equals(out.getClientSent())) //also check for how close offsets are
                    {
                         //System.out.println("sync sent");
                         Command c = new Command(null, "sync");
                         c.process();
                         OutputProcessor.addToOutputQueue(c);
                    }
                    latestClient = out.getClientSent();
               }
          }
     }

     public static void addToOutputQueue(Data com)
     {
          clientOutputQueue.add(com.getData());
     }

     public static void addToOutputQueue(Command com)
     {
          clientOutputQueue.add(com);
     }

     public static void addToInputQueue(ClientThread clientSent, Command input)
     {
          clientInputQueue.add(new Data(clientSent, input, latestTime));
          latestTime = new Date();
     }

     public static BlockingQueue<Data> getInputQueue()
     {
          return clientInputQueue;
     }

     public static Command takeFromOutputQueue()
     {
          return clientOutputQueue.poll();
     }
}
