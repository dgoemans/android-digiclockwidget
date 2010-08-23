package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LauncherChooser extends ListActivity 
{
	private List<String> menuEntries;
	private ArrayList<String> packageNames;
	
	private TreeMap<String, String> m_packages;
	
	public static ProgressDialog progressDialog = null;
	
	private int progressTicker;
	
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
			
			Object[] keys = m_packages.keySet().toArray();
			
			getListView().invalidate();
			setListAdapter(new ArrayAdapter<Object>(LauncherChooser.this,android.R.layout.simple_list_item_multiple_choice, keys));
			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			
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
		
		m_packages = new TreeMap<String, String>();
		
		new FillListTask().execute();
		
		
		
		super.onCreate(savedInstanceState);
	}

	
	private void populateList()
	{
		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		progressDialog.setMax(packages.size());
		
		int count = 0;

		PackageInfo cur;
		for( Iterator<PackageInfo> it = packages.iterator(); it.hasNext(); )
		{
			cur = it.next();
			
			Intent intent = getPackageManager().getLaunchIntentForPackage(cur.packageName);
			
			if( intent != null )
			{
				String curName;
				try
				{
					curName = getPackageManager().getApplicationLabel(cur.applicationInfo).toString();
					menuEntries.add( curName );
				}
				catch(Exception e)
				{
					curName = cur.packageName;
					menuEntries.add( curName );
				}
				packageNames.add(cur.packageName);
				
				m_packages.put(curName, cur.packageName);
			}
			
			progressTicker = count;
			progressDialog.setProgress(progressTicker);

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
		
		Object[] keys = m_packages.keySet().toArray();
		for( int i=0; i<keys.length; i++ )
		{
			if( selPackageNames.contains( m_packages.get(keys[i])) )
			{
				getListView().setItemChecked(i, true);
			}
		}
	}
	
	@Override
	protected void onPause() 
	{
		SparseBooleanArray positions = getListView().getCheckedItemPositions();
		ArrayList<PackageDesc> selectedPackages = new ArrayList<PackageDesc>();
		
		Object[] keys = m_packages.keySet().toArray();
		
		// NOTE keys IS JUST FOR THE CORRECT SIZE, NOT ORDER DEPENDENT
		for( int i=0; i<keys.length; i++ )
		{
			if( positions.get(i) )
			{
				String key = (String) getListView().getItemAtPosition( i );
				selectedPackages.add(new PackageDesc( key, m_packages.get(key) ) );
				Log.d("DigiClockWidget", key);
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
		
		ed.putBoolean("invalidate", true);
		ed.commit();
		
		this.startActivity(new Intent(this, SettingsList.class));
		
		super.onPause();
		
		this.finish();
	}
	
	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id )
	{
		super.onListItemClick(l, v, position, id);
	}
}
