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
package com.macleod2486.fragment;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.macleod2486.utnow.R;

public class Main extends Fragment 
{
	
	private ListView titleList;
	private ProgressBar progress;
	private Button reload;
	
	private String url;
	private boolean finished = false;
	private ArrayList<String> titles = new ArrayList<String>();
	private ArrayList<String> links = new ArrayList<String>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.main, container, false);
		progress = (ProgressBar)view.findViewById(R.id.progress);
		reload = (Button)view.findViewById(R.id.reload);
		reload.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				reload.setVisibility(View.INVISIBLE);
				progress.setVisibility(View.VISIBLE);
				new getEvents().execute();
			}
		});
		titleList = (ListView)view.findViewById(R.id.title);
		titleList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				NewsEvents events = new NewsEvents();
				events.setInfo(titles.get(arg2), links.get(arg2));
				getFragmentManager().beginTransaction().replace(R.id.container,events).commit();
			}
			
		});
		titles.clear();
		links.clear();
		new getEvents().execute();
		return view;
	}
	
	public void loadUrl(String url)
	{
		this.url = url;
	}
	
	private class getEvents extends AsyncTask <Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			try
			{
				Document connect = Jsoup.connect(url).get();
				Elements first = connect.select("div.itemlist");
				Elements second = first.select("a[href]");
								
				for(Element newsEvents: second)
				{
					if(!newsEvents.text().contains("Explore all news") && 
							!newsEvents.text().contains("Explore all events") && 
							newsEvents.text() != "" && 
							!newsEvents.text().contains("Emergency Information") &&
							!newsEvents.text().contains("ITS Alerts"))
					{
						titles.add(newsEvents.text());
						links.add(newsEvents.attr("abs:href").toString());
					}
						
				}
				finished = true;
			}
			catch(Exception e)
			{
				finished = false;
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		public void onPostExecute(Void arg0)
		{
			if(finished)
			{
				progress.setVisibility(View.INVISIBLE);
				reload.setVisibility(View.INVISIBLE);
				ArrayAdapter<String> adaptor = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,titles);
				titleList.setAdapter(adaptor);
			}
			else
			{
				reload.setVisibility(View.VISIBLE);
				progress.setVisibility(View.INVISIBLE);
			}
		}
		
	}
}
