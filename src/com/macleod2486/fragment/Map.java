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
import java.util.Arrays;
import java.util.Collections;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.macleod2486.utnow.R;

public class Map extends Fragment 
{
	private GoogleMap UT;
	private AutoCompleteTextView search;
	
	private LatLng UTLoc= new LatLng(30.284961, -97.734113);
	
	private ArrayList <String> buildingList = new ArrayList <String>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.map, container, false);
		
		//Gets the building lists into an arraylist
		buildingList.clear();
		buildingList.addAll(Arrays.asList(getResources().getStringArray(R.array.fraternity)));
		buildingList.addAll(Arrays.asList(getResources().getStringArray(R.array.sorority)));
		Collections.sort(buildingList,String.CASE_INSENSITIVE_ORDER);
		
		//Only puts the building name for the autocomplete text
		for(int index = 0; index < buildingList.size(); index ++)
		{
			buildingList.set(index, buildingList.get(index).substring(0, buildingList.get(index).indexOf(',')));
		}
		
		search = (AutoCompleteTextView)view.findViewById(R.id.mapSearch);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, buildingList);
		search.setThreshold(1);
		search.setAdapter(adapter);
		search.setOnItemClickListener(new OnItemClickListener()
		{
			
		    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId)
		    {
		    	Double lat;
				Double lon;
				
				String selection = (String)parent.getItemAtPosition(position);
				String latitude;
				String longitude;
				//Array with complete coordinates
		    	ArrayList <String> completeList = new ArrayList <String>();
		    	completeList.addAll(Arrays.asList(getResources().getStringArray(R.array.fraternity)));
		    	completeList.addAll(Arrays.asList(getResources().getStringArray(R.array.sorority)));
		    	Collections.sort(completeList,String.CASE_INSENSITIVE_ORDER);
		    	
		    	latitude = completeList.get(buildingList.indexOf(selection));
		    	latitude = latitude.substring(latitude.indexOf(",")+1,latitude.lastIndexOf(","));
		    	longitude = completeList.get(buildingList.indexOf(selection));
		    	longitude = longitude.substring(longitude.lastIndexOf(",")+1);
		    	
		    	lat = Double.parseDouble(latitude);
		    	lon = Double.parseDouble(longitude);
		    	
		    	UT.clear();
				UT.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(selection));
				UT.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 17));
		        
		        Log.i("Map","Selected "+search.getText()+selection);
		        Log.i("Map","Selected index "+completeList.get(buildingList.indexOf(selection)));
		    }
		});
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		setUpUT();
	}
	
	@Override
	public void onDestroyView()
	{
		Log.i("Google","Destroy view called");
		super.onDestroyView();
		
		try
		{
			Log.i("Google","Destroy executing");
			Fragment frag = (getFragmentManager().findFragmentById(R.id.google_map));
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			ft.remove(frag);
			ft.commit();
		}
		catch(Exception e)
		{
			Log.i("Google","Error in destroying UT "+e);
		}
		Log.i("Google","On destroy complete!");
	}
	
	//Sets up the UT when loaded
	private void setUpUT()
	{
		Log.i("Google","oncreate called!");
		
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		String UTSel = shared.getString("UTSelect", "1");
		
		if(UT==null)
		{
			UT=((SupportMapFragment)getFragmentManager().findFragmentById(R.id.google_map)).getMap();
			Log.i("Google","UT recieved");
		}
		if(UT!=null)
		{
			UT=((SupportMapFragment)getFragmentManager().findFragmentById(R.id.google_map)).getMap();
			
			//Sets the options for the user to show their current location
			UT.setMyLocationEnabled(true);
			
			UT.animateCamera(CameraUpdateFactory.newLatLngZoom(UTLoc, 16));
			
			//Sets the camera view as determined by the users settings
			if(UTSel.contains("1"))
			{	
				UT.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			}
			else if(UTSel.contains("2"))
			{	UT.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
			else if(UTSel.contains("3"))
			{	
				UT.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			}
			
			Log.i("Google","UT setting set");
		}
	}
}
