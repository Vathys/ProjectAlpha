package com.alpha.client;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.alpha.client.gui.Editor;
import com.alpha.client.helper.Event;
import com.alpha.client.helper.RegexParser;

public class InputProcessor extends Thread
{

     public static BlockingQueue<String> inputQueue;

     public InputProcessor()
     {
          inputQueue = new LinkedBlockingDeque<String>();
     }

     public void run()
     {
          while (!Editor.getWindowClosing())
          {
               String msg = inputQueue.poll();
               if (msg != null)
               {
                    Event e = createEvent(msg);
                    	
               }
          }
     }

     public Event createEvent(String raw)
     {
          ArrayList<String> check = RegexParser.matches("\\[(.*?)\\]\\[([+-0])\\]\\[off(\\d+)\\]\\[len(\\d+)\\]\"(.*?)\"", raw);
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
          Event e = null;

          if (check.get(2).equals("+"))
          {
               e = new Event(check.get(1), 1, Integer.valueOf(check.get(3)).intValue(), Integer.valueOf(check.get(4)).intValue(), str);
          } else if (check.get(2).equals("-"))
          {
               e = new Event(check.get(1), -1, Integer.valueOf(check.get(3)).intValue(), Integer.valueOf(check.get(4)).intValue(), str);
          } else if (check.get(2).equals("0"))
          {
               e = new Event(check.get(1), 0, Integer.valueOf(check.get(3)).intValue(), Integer.valueOf(check.get(4)).intValue(), str);
          }
          return e;
     }
}
