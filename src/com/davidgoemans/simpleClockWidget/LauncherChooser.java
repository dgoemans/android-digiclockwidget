package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class LauncherChooser extends ListActivity 
{
	private List<String> menuEntries;
	private ArrayList<String> packageNames;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{

		menuEntries = new ArrayList<String>();		
		packageNames = new ArrayList<String>();
		
		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		
		PackageInfo cur;
		for( Iterator<PackageInfo> it = packages.iterator(); it.hasNext(); )
		{
			cur = it.next();
			
			Intent intent = getPackageManager().getLaunchIntentForPackage(cur.packageName);
			
			if( intent != null )
			{
				menuEntries.add( getPackageManager().getApplicationLabel(cur.applicationInfo).toString() );
				packageNames.add(cur.packageName);
			}
		}
		
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, menuEntries));		
	}
	
	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id )
	{
		super.onListItemClick(l, v, position, id);

		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putInt("launcherId", position );
		ed.putString("launcherPackage", packageNames.get(position) );
		ed.commit();
		
		Log.d("DigiClockWidget", String.valueOf( position ) );

		this.finish();
	}
}
