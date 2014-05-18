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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.macleod2486.fragment.Main;
import com.macleod2486.fragment.Map;
import com.macleod2486.fragment.UTWebView;

public class MainActivity extends FragmentActivity
{
	private DrawerLayout drawer;
	
	private UTWebView direct = new UTWebView();
	private Main main = new Main();
	private Map map = new Map();
	
	private String directUrl = "https://utdirect.utexas.edu/";
	private String calenderUrl = "http://calendar.utexas.edu/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Configures the drawer
		drawer = (DrawerLayout)findViewById(R.id.drawer);
		drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() 
		{
			@Override
			public void onDrawerClosed(View drawerView)
			{
				super.onDrawerClosed(drawerView);
			}
			
			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
			}
		});
		drawer.setDrawerLockMode(drawer.LOCK_MODE_UNLOCKED);
		
		//Sets up the listview within the drawer
		String [] menuList = getResources().getStringArray(R.array.list);
		ListView list = (ListView)findViewById(R.id.optionList);
		list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuList));
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView parent, View view, int position, long id)
			{
				Log.i("MainActivity","Position "+position);
				
				if(position == 0)
				{
					getSupportFragmentManager().beginTransaction().replace(R.id.container, main).commit();
				}
				else if(position == 1)
				{
					getSupportFragmentManager().beginTransaction().replace(R.id.container, direct).commit();
				}
				else if(position == 2)
				{
					getSupportFragmentManager().beginTransaction().replace(R.id.container, map).commit();
				}
				drawer.closeDrawers();
			}
		});
		
		direct.loadUrl(directUrl, false);
		main.loadUrl(calenderUrl);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.container, main).commit();
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
