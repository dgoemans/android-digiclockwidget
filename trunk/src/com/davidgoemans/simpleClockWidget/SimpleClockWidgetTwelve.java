package com.davidgoemans.simpleClockWidget;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SimpleClockWidgetTwelve extends AppWidgetProvider 
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		UpdateFunctions.SetTwelve(context, true);
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
 		UpdateFunctions.Invalidate(context);
 		UpdateFunctions.LaunchSettingsApp(context);
 		
 		context.startService(new Intent(context, SimpleClockUpdateService.class));
 		super.onEnabled(context);
 	}
}
