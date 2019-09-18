package com.alpha.server.helper;

import java.util.Date;

import com.alpha.server.Command;

public class Data
{
     private Command data;
     private Date timeSent;
     private long diff;
     
     public Data(Command com, Date prevTime) 
     {
          data = com;
          timeSent = new Date();
          diff = timeSent.getTime() - prevTime.getTime();
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

     
}
