package com.alpha.client.helper;

public class Event
{
     String docName;
     int eventType;
     int offset;
     int length;
     String msg;

     public Event(String docName, int eventType, int offset, int length, String msg)
     {
          this.docName = docName;
          this.eventType = eventType;
          this.offset = offset;
          this.length = length;
          this.msg = msg;
     }

     /**
      * @return the docName
      */
     public String getDocName()
     {
          return docName;
     }

     /**
      * @return the eventType
      */
     public int getEventType()
     {
          return eventType;
     }

     /**
      * @return the offset
      */
     public int getOffset()
     {
          return offset;
     }

     /**
      * @return the length
      */
     public int getLength()
     {
          return length;
     }

     /**
      * @return the msg
      */
     public String getMsg()
     {
          return msg;
     }

}
