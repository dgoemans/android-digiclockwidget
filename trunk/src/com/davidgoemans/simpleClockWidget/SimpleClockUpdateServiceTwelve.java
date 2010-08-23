package com.davidgoemans.simpleClockWidget;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class SimpleClockUpdateServiceTwelve extends Service 
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
    	
        RemoteViews updateViews = UpdateFunctions.buildUpdate(this, true);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        
        // Push update for all sized widgets to home screen       
        ComponentName thisWidget = new ComponentName(this, SimpleClockWidgetTwelve.class);        
        manager.updateAppWidget(thisWidget, updateViews);

    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
