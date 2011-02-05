package com.davidgoemans.simpleClockWidget;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SimpleClockWidget extends AppWidgetProvider 
{
	
	public static final int tickTime = 5000;
	public static final int delayTime = 0;
	
	public static String PREFS_NAME = "digiClockPrefs";
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		UpdateFunctions.Invalidate(context);

		context.startService(new Intent(context, SimpleClockUpdateService.class));
		
		super.onReceive(context, intent);
	}

    
 	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
 	{	
 		UpdateFunctions.Invalidate(context);
 		context.startService(new Intent(context, SimpleClockUpdateService.class));
    }
 	
 	@Override
 	public void onDisabled(Context context) 
 	{
 		context.stopService(new Intent(context, SimpleClockUpdateService.class));

 		super.onDisabled(context);
 	}
 	
 	@Override
 	public void onEnabled(Context context) 
 	{
 		UpdateFunctions.SetTwelve(context, false);
 		UpdateFunctions.Invalidate(context);
 		UpdateFunctions.LaunchSettingsApp(context);

 		context.startService(new Intent(context, SimpleClockUpdateService.class));
 		super.onEnabled(context);
 	}
}
