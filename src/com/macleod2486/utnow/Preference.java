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
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preference extends PreferenceActivity
{
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	@Override
	public void onStop()
	{
		Log.i("Preferences","On stop called.");
		
		//Start the service in a timely interval
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		if(shared.getBoolean("notification", false) && shared.getBoolean("notifCancel", true))
		{
			//one second * 60 seconds in a minute * 5
			long fiveMinutes = 1000*60*5;
			
			//Start the alarm manager service
			SharedPreferences.Editor edit = shared.edit();
			Intent service = new Intent(getApplicationContext(),UTBroadcast.class);
			PendingIntent pendingService = PendingIntent.getBroadcast(getApplicationContext(),0,service,0);
			AlarmManager newsUpdate = (AlarmManager)getSystemService(ALARM_SERVICE);
			
			//Check for the update every 5 minutes
			newsUpdate.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), fiveMinutes, pendingService);
			edit.putBoolean("notifCancel", false).commit();
			Log.i("UTService","Alarm set "+shared.getBoolean("notifCancel", true));
		}
		
		//If the service is set to be cancelled then it will cancel the service
		if(!shared.getBoolean("notification", true)&&!shared.getBoolean("notifCancel", false))
		{
			SharedPreferences.Editor edit = shared.edit();
			//Cancel the alarm manager service
			Intent service = new Intent(getBaseContext(),UTBroadcast.class);
			PendingIntent pendingService = PendingIntent.getBroadcast(getBaseContext(),0,service,0);
			AlarmManager newsUpdate = (AlarmManager)getSystemService(ALARM_SERVICE);
			newsUpdate.cancel(pendingService);
			edit.putBoolean("notifCancel", true).commit();
			Log.i("UTService","Service cancelled "+shared.getBoolean("notifCancel", false));	
		}
		
		super.onStop();
	}
}
