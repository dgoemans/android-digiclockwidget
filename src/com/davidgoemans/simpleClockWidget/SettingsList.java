package com.davidgoemans.simpleClockWidget;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
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
		boolean finish = true;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("This option is not done yet, but it's coming soon!\n- The Dev")
		       .setCancelable(false)
		       .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		
		switch( position )
		{
		case 0:
			this.startActivity(new Intent(this, ThemeChooser.class));
			break;
		case 1:
			this.startActivity(new Intent(this, LauncherChooser.class));
			break;
		case 2:
			//this.startActivity(new Intent(this, LauncherChooser.class));
			builder.show();
			finish = false;
			break;
		case 3:
			try 
			{
				this.startActivity( Intent.getIntent( "http://www.davidgoemans.com" ));
			} 
			catch (URISyntaxException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

		if( finish )
			this.finish();
	}
}
