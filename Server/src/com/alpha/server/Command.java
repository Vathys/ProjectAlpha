package com.alpha.server;

import java.net.InetAddress;


public class Command
{
     private String rawCommand;
     private ClientThread sentFrom;
     private String outputCommand;

     public Command(ClientThread c, String rawCommand)
     {
          this.sentFrom = c;
          this.rawCommand = rawCommand;
     }

     public void process()
     {
          if (rawCommand.equals("exit"))
          {
               outputCommand = null;
          } 
          else if(rawCommand.equals("sync"))
          {
               outputCommand = rawCommand;
          }
          else
          {
               outputCommand = "{" + rawCommand + "}";
          }
     }

     public ClientThread sentFrom()
     {
          return sentFrom;
     }

     public String output()
     {
          return outputCommand;
     }
}
