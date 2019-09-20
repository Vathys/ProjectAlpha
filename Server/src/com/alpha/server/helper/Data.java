package com.alpha.server.helper;

import java.net.InetAddress;
import java.util.Date;

import com.alpha.server.ClientThread;
import com.alpha.server.Command;
import com.alpha.server.HubServer;

public class Data
{
     private Command data;
     private Date timeSent;
     private long diff;
     private InetAddress clientSent;
     
     public Data(ClientThread clientSent, Command com, Date prevTime) 
     {
          this.clientSent = clientSent.getClient().getInetAddress();
          data = com;
          timeSent = new Date();
          diff = timeSent.getTime() - prevTime.getTime();
          System.out.println("Time Data is initialized: " + HubServer.getCurrentTimeStamp());
     }

     public Data(Command com)
     {
          data = com;
          timeSent = new Date();
          diff = -1;
     }
     
     /**
      * @return the data
      */
     public Command getData()
     {
          return data;
     }

     /**
      * @return the timeSent
      */
     public Date getTimeSent()
     {
          return timeSent;
     }
     
     public long getDiff()
     {
          return diff;
     }
     

     public InetAddress getClientSent()
     {
          return clientSent;
     }
}
