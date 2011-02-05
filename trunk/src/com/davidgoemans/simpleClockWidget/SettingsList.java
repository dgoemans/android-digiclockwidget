package com.davidgoemans.simpleClockWidget;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.davidgoemans.simpleClockWidget.LauncherConfig.AppInfo;

import android.R.bool;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsList extends ListActivity 
{
	public int[] MenuIcons =
	{
		R.drawable.menu_themes,
		R.drawable.menu_tap,
		R.drawable.menu_time,
		R.drawable.menu_date,
		R.drawable.menu_donate,
		R.drawable.menu_help,
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		Resources res = getResources();
		CharSequence[] settings = res.getTextArray( R.array.settings );
		CharSequence[] settingsDesc = res.getTextArray( R.array.settings_desc );
		
		super.onCreate(savedInstanceState);
		//setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuEntries));
		
		setListAdapter(new MenuItemAdapter(this, R.layout.launcher_row, settings, settingsDesc));
	}
	
	private class MenuItemAdapter extends ArrayAdapter<CharSequence> 
	{

		CharSequence[] items;
		CharSequence[] descriptions;
		
		public MenuItemAdapter(Context context, int textViewResourceId, CharSequence[] items, CharSequence[] descriptions) 
		{
			super(context, textViewResourceId, items);
			this.items = items;
			this.descriptions = descriptions;
		}

		@Override
		public View getView(int position, View convertView, android.view.ViewGroup parent) 
		{
			View v = convertView;
			if (v == null) 
			{
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.launcher_row, null);
			}
			
			if( position < items.length )
			{
				ImageView image = (ImageView) v.findViewById(R.id.icon);
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				
				if(image != null)
				{
					image.setImageResource(MenuIcons[position]);
				}
				
				if (tt != null) 
				{
					tt.setText(items[position]);                            
				}
				if(bt != null)
				{
					bt.setText(descriptions[position]);
				}
			}
			return v;
		};
	}
	
	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id )
	{
		super.onListItemClick(l, v, position, id);
		
		switch( position )
		{
		case 0:
			this.startActivity(new Intent(this, GetMoreThemes.class));
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
				this.startActivity( Intent.getIntent( "market://search?q=pname:com.davidgoemans.simpleClockWidgetDonate" ));
			} 
			catch (URISyntaxException e) 
			{
				e.printStackTrace();
			}
			break;
		case 5:
			UpdateFunctions.LaunchActivity(this, News.class);
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
