package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.List;

import com.davidgoemans.simpleClockWidget.Launcher.AppAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class LauncherConfig 
{
	String[] menuEntries;
	String[] packageNames;
	Drawable[] packageIcons;
	String[] descriptions;
	
	AppInfo[] apps;
	
	int count;
	
	
	public class AppInfo
	{
		public String DisplayName = null;
		public String PackageName = null;
		public Drawable Icon = null;
		public String Description = null;
		
		Launcher.AppAdapter toNotify;
		Handler handler;
		
		public void getIcon(AppAdapter toNotify)
		{
			this.toNotify = toNotify;
			handler = new Handler();
			Log.d("DigiClock", "Getting icon");
			handler.postDelayed(timer, 500);
		}
		
		private Runnable timer = new Runnable() 
		{ 
			public void run() 
			{
				try 
				{
					Icon = toNotify.getContext().getPackageManager().getApplicationIcon(PackageName);
				} 
				catch (NameNotFoundException e) 
				{
					Icon = toNotify.getContext().getResources().getDrawable(R.drawable.icon);
				}
				
				toNotify.NotifyIconDone();
			}
		};
	}
	
	public LauncherConfig(Context context)
	{		
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		count = prefs.getInt("launcherCount", 0);
		
		menuEntries = new String[count];	
		packageNames = new String[count];
		packageIcons = new Drawable[count];
		
		apps = new AppInfo[count];
		
		populateArrays(context, prefs);
	}
	
	void populateArrays(Context context, SharedPreferences prefs)
	{
		for( int i=0; i<count; i++ )
		{
			// Backward compat
			if( i==0 )
			{
				packageNames[i] = prefs.getString("launcherPackage", "" );
				menuEntries[i] = prefs.getString("launcherPackageDesc", "" );
			}
			else
			{
				packageNames[i] = prefs.getString("launcherPackage"+i, "" );
				menuEntries[i] = prefs.getString("launcherPackageDesc"+i, "" );
			}
			
			apps[i] = new AppInfo();
			apps[i].PackageName = packageNames[i];
			apps[i].DisplayName = menuEntries[i];
		}
	}
	
	public int getAppCount()
	{
		return count;
	}
	
	public String getPackageName(int index)
	{
		return packageNames[index];
	}
	
	public String getDisplayName(int index)
	{
		return menuEntries[index];
	}
	
	public String[] getDisplayNames()
	{
		return menuEntries;
	}
	
	public AppInfo getApp(int index)
	{
		return apps[index];
	}
	
	public AppInfo[] getApps()
	{
		return apps;
	}
}
