package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.List;

import com.davidgoemans.simpleClockWidget.SimpleClockWidget;
import com.davidgoemans.simpleClockWidget.SimpleClockWidgetTwelve;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class ThemeChooser extends ListActivity 
{
	private List<String> menuEntries;
	private int page;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		Resources res = getResources();
		CharSequence[] colors = res.getTextArray( R.array.colors );

		menuEntries = new ArrayList<String>();
		for( int i = 0; i< colors.length; i++ )
		{
			menuEntries.add( colors[i].toString() );
		}
		
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, menuEntries));		
	}
	
	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id )
	{
		Resources res = getResources();
		CharSequence[] colors = res.getTextArray( R.array.colors );

		super.onListItemClick(l, v, position, id);
		
		if( position < colors.length )
		{
			SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
			SharedPreferences.Editor ed = prefs.edit();
			ed.putInt("colorId", position );
			ed.commit();
		}

		Log.d("DigiClockWidget", String.valueOf( position ) );

		this.finish();
	}
}
