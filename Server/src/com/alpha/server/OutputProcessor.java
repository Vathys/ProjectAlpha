package com.alpha.server;

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
     
     public OutputProcessor(HubServer hub)
     {
          clientOutputQueue = new LinkedBlockingDeque<Command>();
          clientInputQueue = new LinkedBlockingDeque<Data>();
          latestTime = new Date();
          Main.gui.addText("Queues Active");
     }

     public void run()
     {
          while (!ServerGUI.getServerClosing())
          {
               Data out = clientInputQueue.poll();
               if(out != null)
               {
                    out.getData().process();
                    //System.out.println(out.getData().output() + " " + out.getDiff());
                    OutputProcessor.addToOutputQueue(out);
                    if(out.getDiff() < 50 && out.getDiff() > 0) //also check for how close offsets are
                    {
                         //System.out.println("sync sent");
                         Command c = new Command(null, "sync");
                         c.process();
                         OutputProcessor.addToOutputQueue(c);
                    }
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
     
     public static void addToInputQueue(Command input)
     {
          clientInputQueue.add(new Data(input, latestTime));
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
