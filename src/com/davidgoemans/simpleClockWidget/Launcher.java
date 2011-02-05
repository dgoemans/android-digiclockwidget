package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.davidgoemans.simpleClockWidget.LauncherConfig.AppInfo;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Launcher extends ListActivity 
{

	LauncherConfig config;
		
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		config = new LauncherConfig(this);
		
		UpdateFunctions.Invalidate(getApplicationContext());
		startService(new Intent(this, SimpleClockUpdateService.class));
		

		int count = config.getAppCount();
		
		if( count == 0 )
		{
			this.finish();
		}
		else if( count == 1 )
		{
			Intent defineIntent = getPackageManager().getLaunchIntentForPackage(config.getPackageName(0));

			this.startActivity(defineIntent);
			this.finish();
		}
		
		super.onCreate(savedInstanceState);
		
		setListAdapter(new AppAdapter(this, R.layout.launcher_row, config.getApps()));
	}

	public class AppAdapter extends ArrayAdapter<AppInfo> 
	{

		Lock lock;
		private AppInfo[] items;

		public AppAdapter(Context context, int textViewResourceId, AppInfo[] items) 
		{
			super(context, textViewResourceId, items);
			lock = new ReentrantLock();
			this.items = items;
			
			// Start a bunch of new threads to get the icons
			for(AppInfo info : this.items)
			{
				info.getIcon(this);
			}
		}

		@Override
		public View getView(int position, View convertView, android.view.ViewGroup parent) 
		{
			lock.lock();
			View v = convertView;
			if (v == null) 
			{
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.launcher_row, null);
			}
			
			AppInfo info = items[position];
			
			if (info != null) 
			{
				ImageView image = (ImageView) v.findViewById(R.id.icon);
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				
				if( info.Icon != null && image != null)
				{
					image.setImageDrawable(info.Icon);
				}
				
				if (tt != null) 
				{
					tt.setText(info.DisplayName);                            
				}
				if(bt != null)
				{
					bt.setText(info.PackageName);
				}
			}
			
			lock.unlock();
			return v;
		}
		
		// When an icon is retrieved, notify that content is different
		void NotifyIconDone()
		{
			Launcher.this.onContentChanged();
		}
	}

	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id )
	{
		super.onListItemClick(l, v, position, id);

		try
		{
			Intent defineIntent;
			defineIntent = 
				getPackageManager().getLaunchIntentForPackage(config.getPackageName(position));
			
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
