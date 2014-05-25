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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.macleod2486.utnow.R;

public class NewsEvents extends Fragment 
{
	private TextView contentText;
	private TextView contentTitle;
	private ProgressBar progress;
	private Button reload;
	
	private String url;
	private String title;
	private boolean finished = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.content, container, false);
		contentText = (TextView)view.findViewById(R.id.content);
		contentTitle = (TextView)view.findViewById(R.id.title);
		progress = (ProgressBar)view.findViewById(R.id.progress);
		reload = (Button)view.findViewById(R.id.reload);
		reload.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				reload.setVisibility(View.INVISIBLE);
				progress.setVisibility(View.VISIBLE);
				new getContent().execute();
			}
		});
		
		new getContent().execute();
		
		return view;
	}
	
	public void setInfo(String title, String url)
	{
		this.url = url;
		this.title = title;
	}
	
	private class getContent extends AsyncTask<Void, Void, Void>
	{
		Document document;
		Elements content;
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			try
			{
				document = Jsoup.connect(url).get();
				content = document.select("p");
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
		protected void onPostExecute(Void args)
		{
			if(finished)
			{
				contentTitle.setText(title);
				contentTitle.setTextSize(25);
				contentText.setText(content.text());
				progress.setVisibility(View.INVISIBLE);
			}
			else
			{
				reload.setVisibility(View.VISIBLE);
				progress.setVisibility(View.INVISIBLE);
			}
		}
	}

}
