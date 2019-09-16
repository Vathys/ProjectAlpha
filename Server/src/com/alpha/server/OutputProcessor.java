package com.alpha.server;

import java.util.LinkedList;

import com.alpha.Main;
import com.alpha.server.gui.ServerGUI;
import com.alpha.server.helper.Data;

public class OutputProcessor extends Thread
{

     private static LinkedList<Command> clientOutputQueue;
     private static LinkedList<Data> clientInputQueue;

     public OutputProcessor(HubServer hub)
     {
          clientOutputQueue = new LinkedList<Command>();
          clientInputQueue = new LinkedList<Data>();
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
                    if(out.getDiff() < 10)
                    {
                         OutputProcessor.addToOutputQueue(new Command(null, "sync"));
                         //add a broadcast to synchronize all clients
                    }
                    OutputProcessor.addToOutputQueue(out);
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
          if(clientInputQueue.isEmpty())
          {
               clientInputQueue.add(new Data(input));
          }
          else {
               clientInputQueue.add(new Data(input, clientInputQueue.peekLast()));
          }
          //clientInputQueue.add(input);
     }

     public static LinkedList<Data> getInputQueue()
     {
          return clientInputQueue;
     }

     public static Command takeFromOutputQueue()
     {
          return clientOutputQueue.poll();
     }
}
