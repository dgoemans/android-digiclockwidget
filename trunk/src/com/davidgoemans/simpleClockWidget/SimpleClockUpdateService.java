package com.davidgoemans.simpleClockWidget;

import java.util.Calendar;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.RemoteViews;

public class SimpleClockUpdateService extends Service 
{   
	int prevMinute = -1;
	int prevColor = -1;
	String prevLauncher = "";
	String prevDateFormat = "";
	
    @Override
    public void onStart(Intent intent, int startId) 
    {
    	SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		
    	Calendar rightNow = Calendar.getInstance();
    	int minute = rightNow.get(Calendar.MINUTE);
    	
     	boolean invalidated = prefs.getBoolean("invalidate", false);
    	
		if( minute == prevMinute && !invalidated )
    	{
    		return;
    	}
		
		SharedPreferences.Editor ed = prefs.edit();		
		ed.putBoolean("invalidate", false);
		ed.commit();
		
    	prevMinute = minute;
    	
        RemoteViews updateViews = UpdateFunctions.buildUpdate(this, false);
        
        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        // Push update for all sized widgets to home screen       
        ComponentName thisWidget = new ComponentName(this, SimpleClockWidget.class);
        manager.updateAppWidget(thisWidget, updateViews);
    }

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
}