package com.alpha.pre_process.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.alpha.server.helper.RegexParser;

public class ProjectHandler
{
     private File projectConfig;
     private HashMap<Path, PlainDocument> files;
     private Path parent;
     
     public ProjectHandler(File parent, boolean newProject)
     {
          files = new LinkedHashMap<Path, PlainDocument>();
          
          this.parent = parent.toPath();
          
          if(newProject)
          {
               File projectConfig = new File(parent, "alpha.project");
               try
               {
                    projectConfig.createNewFile();
               } catch (IOException e)
               {
                    e.printStackTrace();
               }
          }
          for(File f : parent.listFiles())
          {
               if(f.getName().contains("alpha.project"))
               {
                    projectConfig = f;
               }
               else if(f.isDirectory())
               {
                    files.putAll(expandDirectory(f));
               }
               else if(f.isFile())
               {
                    files.put(this.parent.relativize(f.toPath()),convertToPlainDocument(f));
               }
          }
          
          for(Path path : files.keySet())
          {
               System.out.println(path.toString());
          }
     }          
     
     private HashMap<Path, PlainDocument> expandDirectory(File directory)
     {
          HashMap<Path, PlainDocument> files = new LinkedHashMap<Path, PlainDocument>();
          
          for(File f : directory.listFiles())
          {
               if(f.isFile())
               {
                    files.put(parent.relativize(f.toPath()), convertToPlainDocument(f));
               }
               else
               {
                    files.putAll(expandDirectory(f));
               }
          }
          return files;
     }
     
     private PlainDocument convertToPlainDocument(File f)
     {
          PlainDocument pd = new PlainDocument();
          String save = "";
          int i;
          try
          {
               FileReader fr = new FileReader(f);
               while((i = fr.read()) != -1)
               {
                    save += (char)i;
               }
               fr.close();
               pd.insertString(0, save, null);
          } catch (Exception e)
          {
               e.printStackTrace();
          }
          return pd;
     }

     public void updateDocument(String name, String com)
     {
          PlainDocument pd = files.get(name);
          
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

     
     public void saveToFile()
     {
          for(Path path : files.keySet())
          {
               PrintWriter pw;
               try
               {
                    pw = new PrintWriter(path.toString());
                    String text = files.get(path).getText(0, files.get(path).getLength());
                    text = text.replaceAll("(?!\\r)\\n", "\r\n");

                    pw.write(text);

                    pw.close();
               } catch (FileNotFoundException | BadLocationException e)
               {
                    e.printStackTrace();
               }
          }
     }

     
     public Set<Path> getFileNames()
     {
          return files.keySet();
     }

     public PlainDocument getDocument(Path path)
     {
          return files.get(path);
     }

     public void createNewFile(File parent, String name)
     {
          File nf = new File(parent, name);
          try
          {
               nf.createNewFile();
          } catch (IOException e)
          {
               e.printStackTrace();
          }
          files.put(this.parent.relativize(nf.toPath()), new PlainDocument());
     }
}
