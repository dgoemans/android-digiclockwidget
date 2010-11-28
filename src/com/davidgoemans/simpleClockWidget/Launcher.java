package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
		UpdateFunctions.Invalidate(getApplicationContext());
		startService(new Intent(this, SimpleClockUpdateService.class));
		
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

		try
		{
			Intent defineIntent;
			defineIntent = getPackageManager().getLaunchIntentForPackage(packageNames.get(position));
			
			this.startActivity(defineIntent);
			this.finish();
		}
		catch( ActivityNotFoundException e )
		{
			Log.d("DigiClock", "Error, app not found: " + e.getMessage());
			showError();
		}
		catch( Exception e )
		{
			Log.d("DigiClock", "Error, general launch error: " + e.getMessage());
			showError();
		}
	}
	
	void showError()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.launch_error);
		builder.setMessage(R.string.launch_error_detail);
		builder.setCancelable(false);
		builder.setNeutralButton(R.string.general_ok, new DialogInterface.OnClickListener() 
			{
	           public void onClick(DialogInterface dialog, int id) 
	           {
	                dialog.cancel();
	           }
	        });
		
		builder.show();
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		this.finish();
	}
}
