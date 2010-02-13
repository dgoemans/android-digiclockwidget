package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class Launcher extends ListActivity 
{
	private List<String> menuEntries;
	private ArrayList<String> packageNames;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		menuEntries = new ArrayList<String>();		
		packageNames = new ArrayList<String>();

		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		int count = prefs.getInt("launcherCount", 0);
		
		if( count == 0 )
		{
			this.finish();
		}
		
		if( count == 1 )
		{
			Intent defineIntent = getPackageManager().getLaunchIntentForPackage(prefs.getString("launcherPackage", "" ));
			this.startActivity(defineIntent);
			this.finish();
		}
		
		for( int i=0; i<count; i++ )
		{
			// Backward compat
			if( i==0 )
			{
				packageNames.add( prefs.getString("launcherPackage", "" ) );
				menuEntries.add( prefs.getString("launcherPackageDesc", "" ) );
			}
			else
			{
				packageNames.add( prefs.getString("launcherPackage"+i, "" ) );
				menuEntries.add( prefs.getString("launcherPackageDesc"+i, "" ) );
			}
		}
		
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, menuEntries));		
	}
	
	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id )
	{
		super.onListItemClick(l, v, position, id);

		Intent defineIntent = getPackageManager().getLaunchIntentForPackage(packageNames.get(position));
		this.startActivity(defineIntent);

		this.finish();
	}
}
