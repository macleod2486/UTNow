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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.macleod2486.fragment.Main;
import com.macleod2486.fragment.Map;
import com.macleod2486.fragment.UTWebView;

public class MainActivity extends ActionBarActivity
{
	private DrawerLayout drawer;
	private ActionBarDrawerToggle drawerToggle;
	
	private UTWebView direct = new UTWebView();
	private Main main = new Main();
	private Map map = new Map();
	
	private String directUrl = "https://utdirect.utexas.edu/";
	private String calenderUrl = "http://www.utexas.edu/";
	
	private int index = 0;
	
	//Manages what the back button does
	@Override
	public void onBackPressed()
	{
		if(drawer.isDrawerOpen(GravityCompat.START))
		{
			Log.i("Main","Drawer closed");
			drawer.closeDrawers();
		}
		
		if(index == 0 && !main.isAdded())
		{
			getSupportFragmentManager().beginTransaction().replace(R.id.container, main, "main").commit();
		}
		else
		{
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Configures the drawer
		drawer = (DrawerLayout)findViewById(R.id.drawer);
		drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
		{
			public void onDrawerClosed(View view)
			{
				getSupportActionBar().setTitle(R.string.drawer_close);
				super.onDrawerClosed(view);
			}
			
			public void onDrawerOpened(View drawerView)
			{
				getSupportActionBar().setTitle(R.string.drawer_open);
				super.onDrawerOpened(drawerView);
			}
		};
		drawer.setDrawerListener(drawerToggle);
		drawer.setDrawerLockMode(drawer.LOCK_MODE_UNLOCKED);
		
		//Sets up the listview within the drawer
		String [] menuList = getResources().getStringArray(R.array.list);
		ListView list = (ListView)findViewById(R.id.optionList);
		list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuList));
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView parent, View view, int position, long id)
			{
				Log.i("MainActivity","Position "+position);
				if(position == 0)
				{
					index = 0;
					getSupportFragmentManager().beginTransaction().replace(R.id.container, main, "main").commit();
				}
				else if(position == 1)
				{
					index = 1;
					getSupportFragmentManager().beginTransaction().replace(R.id.container, direct).commit();
				}
				else if(position == 2)
				{
					index = 2;
					getSupportFragmentManager().beginTransaction().replace(R.id.container, map).commit();
				}
				else if(position == 3)
				{
					startActivity(new Intent(MainActivity.this,Preference.class));
				}
				drawer.closeDrawers();
			}
		});
		
		direct.loadUrl(directUrl, false);
		main.loadUrl(calenderUrl);
		
		//Make the actionbar clickable to bring out the drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		//Displays the first fragment
		getSupportFragmentManager().beginTransaction().replace(R.id.container, main, "main").commit();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		if(getIntent().getAction().equals("starthome"))
		{
			Log.i("Main","Resetting fragment");
			getSupportFragmentManager().beginTransaction().replace(R.id.container, main, "main").commit();
		}
	}
	
	@Override
	public void onStop()
	{
		//Start the service in a timely interval
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(shared.getBoolean("notification", false) && shared.getBoolean("notifCancel", true))
		{
			//one second * 60 seconds in a minute * 5
			long fiveMinutes = 1000*60*5;
			
			//Start the alarm manager service
			SharedPreferences.Editor edit = shared.edit();
			Intent service = new Intent(getApplicationContext(),UTBroadcast.class);
			PendingIntent pendingService = PendingIntent.getBroadcast(getApplicationContext(), 0, service, 0);
			AlarmManager newsUpdate = (AlarmManager)getSystemService(ALARM_SERVICE);
			
			//Check for the update every 5 minutes
			newsUpdate.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), fiveMinutes, pendingService);
			edit.putBoolean("notifCancel", false).commit();
			Log.i("UTService","Alarm set "+shared.getBoolean("notification", true));
		}
		
		super.onStop();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	//Methods for the utdirect webview
	public void refresh(View view)
	{
		WebView utdirect = (WebView)findViewById(R.id.webView);
		utdirect.reload();
	}
	public void back(View view)
	{
		WebView utdirect = (WebView)findViewById(R.id.webView);
		if(utdirect.canGoBack())
			utdirect.goBack();
	}
	public void forward(View view)
	{
		WebView utdirect = (WebView)findViewById(R.id.webView);
		if(utdirect.canGoForward())
			utdirect.goForward();
	}
	
}
