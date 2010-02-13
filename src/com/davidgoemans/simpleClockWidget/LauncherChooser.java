package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LauncherChooser extends ListActivity 
{
	private List<String> menuEntries;
	private ArrayList<String> packageNames;
	
	public static ProgressDialog progressDialog = null;
	
	class PackageDesc
	{
		public PackageDesc(String name, String packageName)
		{
			this.name = name;
			this.packageName = packageName;
		}
		
		String name;
		String packageName;
	}
	
	private class FillListTask extends AsyncTask<Void, Void, Void> 
	{
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
					
			getListView().invalidate();
			onContentChanged();
			progressDialog.dismiss();
			
			setSelected();
			
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			populateList();
			return null;
		}

	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		Resources res = getResources();
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(res.getText( R.string.load_tap_text));
		progressDialog.setTitle(res.getText( R.string.load_tap_title));
		progressDialog.setProgress(0);
		progressDialog.setMax(1);
		progressDialog.show();
		
		menuEntries = new ArrayList<String>();
		packageNames = new ArrayList<String>();
		
		new FillListTask().execute();
		
		setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, menuEntries));
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		super.onCreate(savedInstanceState);
	}

	
	private void populateList()
	{
		List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
		progressDialog.setMax(packages.size());
		
		int count = 0;

		PackageInfo cur;
		for( Iterator<PackageInfo> it = packages.iterator(); it.hasNext(); )
		{
			cur = it.next();
			
			Intent intent = getPackageManager().getLaunchIntentForPackage(cur.packageName);
			
			if( intent != null )
			{
				try
				{
					menuEntries.add( getPackageManager().getApplicationLabel(cur.applicationInfo).toString() );
				}
				catch(Exception e)
				{
					menuEntries.add( cur.packageName );
				}
				packageNames.add(cur.packageName);
			}
			
			progressDialog.setProgress(count);

			count++;
		}
	}
	
	private void setSelected()
	{
		ArrayList<String> selPackageNames = new ArrayList<String>();
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		int count = prefs.getInt("launcherCount", 1);
		
		for( int i=0; i<count; i++ )
		{
			// Backward compat
			if( i==0 )
			{
				selPackageNames.add( prefs.getString("launcherPackage", "" ) );
			}
			else
			{
				selPackageNames.add( prefs.getString("launcherPackage"+i, "" ) );
			}
		}
		
		for( int i=0; i<packageNames.size(); i++ )
		{
			String curPackage = null;
			boolean found = false;
			for( Iterator<String> it = selPackageNames.iterator(); it.hasNext(); )
			{
				curPackage = it.next();

				if( curPackage.equalsIgnoreCase( packageNames.get(i) ) )
				{
					found = true;
					break;
				}
			}
			getListView().setItemChecked(i, found);
		}
	}
	
	@Override
	protected void onDestroy() 
	{
		SparseBooleanArray positions = getListView().getCheckedItemPositions();
		ArrayList<PackageDesc> selectedPackages = new ArrayList<PackageDesc>();
		
		for( int i=0; i<packageNames.size(); i++)
		{
			if( positions.get(i) )
			{
				selectedPackages.add(new PackageDesc(menuEntries.get(i), packageNames.get(i) ) );
				Log.d("DigiClockWidget", menuEntries.get(i));
			}			
		}
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putInt("launcherCount", selectedPackages.size());
		
		for( int i=0; i<selectedPackages.size(); i++ )
		{
			// Backward compat
			if( i==0 )
			{
				ed.putString("launcherPackage", selectedPackages.get(i).packageName );
				ed.putString("launcherPackageDesc", selectedPackages.get(i).name );
			}
			else
			{
				ed.putString("launcherPackage"+i, selectedPackages.get(i).packageName );
				ed.putString("launcherPackageDesc"+i, selectedPackages.get(i).name );
			}
		}
		
		ed.commit();
		
		super.onDestroy();
	}
	
	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id )
	{
		super.onListItemClick(l, v, position, id);
	}
}
