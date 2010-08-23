package com.davidgoemans.simpleClockWidget;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class SettingsList extends ListActivity 
{
	private List<String> menuEntries;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		Resources res = getResources();
		CharSequence[] settings = res.getTextArray( R.array.settings );

		menuEntries = new ArrayList<String>();
		for( int i = 0; i< settings.length; i++ )
		{
			menuEntries.add( settings[i].toString() );
		}
		
		
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuEntries));		
	}
	
	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id )
	{
		super.onListItemClick(l, v, position, id);
		
		switch( position )
		{
		case 0:
			this.startActivity(new Intent(this, ThemeChooser.class));
			break;
		case 1:			
			this.startActivity(new Intent(this, LauncherChooser.class));
			break;
		case 2:
			this.startActivity(new Intent(this, TextSettings.class));
			break;
		case 3:
			this.startActivity(new Intent(this, DateFormatChooser.class));
			break;
		case 4:
			try 
			{
				this.startActivity( Intent.getIntent( "http://www.davidgoemans.com/mainsite/node/18" ));
			} 
			catch (URISyntaxException e) 
			{
				e.printStackTrace();
			}
			break;
		case 5:
			try 
			{
				this.startActivity( Intent.getIntent( "http://www.davidgoemans.com/mainsite/node/17" ));
			} 
			catch (URISyntaxException e) 
			{
				e.printStackTrace();
			}
			break;
		}
		
		this.finish();
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		this.finish();
	}
}
