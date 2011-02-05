package com.davidgoemans.simpleClockWidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class SimpleClockWidgetLarge extends AppWidgetProvider 
{
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
 		UpdateFunctions.SetTwelve(context, true);
 		UpdateFunctions.Invalidate(context);
 		UpdateFunctions.LaunchSettingsApp(context);
 		
 		context.startService(new Intent(context, SimpleClockUpdateService.class));
 		super.onEnabled(context);
 	}
}
