/*
*
*   UTNow 
*    a simple application for general use of the University of Texas at Austin campus 
*    
*    Copyright (C) 2014  Manuel Gonzales Jr.
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see [http://www.gnu.org/licenses/].
*
*/
package com.macleod2486.utnow;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class NewsUpdate extends IntentService 
{
	
	public NewsUpdate() 
	{
		super("NewsUpdate");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		Update checkNew = new Update();
		checkNew.execute();
		
		Log.i("UTService","Started");
	}

	//Async task that checks for the update
	private class Update extends AsyncTask<Void, Void, Void>
	{
		private boolean different;
		private boolean updated = false;
		
		//Will read from the file that is created
		File data = new File(getBaseContext().getCacheDir().toString()+"/news.txt");
		
		//Executes the following in the background
		@Override
		protected Void doInBackground(Void...go)
		{
			Log.i("UTService","Checking for updates");
			//checks to see if the temp file needs to be created
			boolean isFileEmpty;
			
			//Checks to see if the file is there
			isFileEmpty=isFileNull();
			
			Log.i("String","File path "+getBaseContext().getCacheDir().toString()+"/news.txt");
			
			if(!isFileEmpty)
			{
				Log.i("UTService","File was empty");
				createFile();
			}
			else
			{
				Log.i("UTService","File exists");
				getUpdate();
			}
		
			return null;
		}
		
		@SuppressWarnings("deprecation")
		
		//After the async task completes the execution 
		@Override
		protected void onPostExecute(Void go)
		{
			if(updated)
			{
				Notification notifi;
				NotificationManager notifiManage = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				
				Intent homeIntent = new Intent(getBaseContext(),MainActivity.class);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				homeIntent.setAction("starthome");
				
				PendingIntent homePending = PendingIntent.getActivity(getBaseContext(), 0, homeIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
				
				notifi = new Notification(R.drawable.ic_notification,"UTNow",System.currentTimeMillis());
				notifi.setLatestEventInfo(getApplicationContext(), "UTNow", "New Events!", homePending);
				notifi.ledARGB = 0xBF5700;
				notifi.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
				
				notifiManage.notify(0,notifi);
			}
		}
		
		//Creates the file if the file is found to be empty
		private void createFile()
		{
			try
			{
				FileWriter fw = new FileWriter(data);
				String calenderUrl = "http://www.utexas.edu/";
				Document connect = Jsoup.connect(calenderUrl).get();
				Elements first = connect.select("div.itemlist");
				Elements second = first.select("a[href]");;
				
				fw.write("");
				fw.write(second.toString());
				fw.close();
				
				Log.i("UTService","Completed creating file");
			}
			catch(Exception e)
			{
				Log.i("UTService","Create File Error "+e);
			}
		}
		
		//If the file is not empty then it will grab and check updates
		private void getUpdate()
		{
			String calenderUrl;
			Document connect;
			Elements first;
			Elements second;
			Scanner updateScan;
			FileWriter fw;
			
			try
			{
				updateScan = new Scanner(data);
				updateScan.useDelimiter("\\A");
				
				String temp = updateScan.next();
				
				calenderUrl = "http://www.utexas.edu/";
				connect = Jsoup.connect(calenderUrl).get();
				first = connect.select("div.itemlist");
				second = first.select("a[href]");
				
				if(!second.toString().equals(temp))
						different = true;
				
				updateScan.close();
				
				//If there is a difference then the file is updated
				if(different)
				{
					fw = new FileWriter(data);
					
					fw.write("");
					fw.write(second.toString());
					fw.close();
					
					Log.i("UTService","New updates found!");
					
					updated = true;
				}
				else
				{
					Log.i("UTService","No new updates");
				}
					
			}
			catch(Exception e)
			{
				Log.i("UTService","Getupdate Error "+e);
			}
		}
		
		//Checks to see if the file is new or is empty
		private boolean isFileNull()
		{
			boolean checkNull = true;
			try
			{	
				Scanner temp = new Scanner(data);
				Log.i("UTService","Checking the file");
				
				checkNull = temp.hasNext();
				temp.close();
			}
			catch(Exception e)
			{
				Log.i("UTService","IsFileNull error "+e);
				checkNull = false;
			}
			
			return checkNull;
		}
	}
}
