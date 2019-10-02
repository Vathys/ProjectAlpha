package com.alpha.client;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.alpha.client.gui.Editor;
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
			  if(msg != null)
			  {
				  ArrayList<String> check = RegexParser.matches("\\[([+-0])\\]\\[off(\\d+)\\]\\[len(\\d+)\\]\"(.*?)\"", msg);
			  }
		  }
	}
	
}
