package com.alpha.pre_process.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ProjectHandler
{
     private File projectConfig;
     private HashMap<String, File> files;
     
     public ProjectHandler(File parent)
     {
          files = new LinkedHashMap<String, File>();
          
          for(File f : parent.listFiles())
          {
               if(f.getName().contains(".project"))
               {
                    projectConfig = f;
               }
               else if(parent.isDirectory())
               {
                    files.putAll(expandDirectory(f));
               }
          }
     }
     
     private HashMap<String, File> expandDirectory(File directory)
     {
          HashMap<String, File> files = new LinkedHashMap<String, File>();
          
          for(File f : directory.listFiles())
          {
               if(f.isFile())
               {
                    try
                    {
                         files.put(f.getCanonicalPath(), f);
                    } catch (IOException e)
                    {
                         e.printStackTrace();
                    }
               }
               else
               {
                    files.putAll(expandDirectory(f));
               }
          }
          return files;
     }
}
