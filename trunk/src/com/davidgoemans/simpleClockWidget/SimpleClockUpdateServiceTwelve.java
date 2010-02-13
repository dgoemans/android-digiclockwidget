package com.davidgoemans.simpleClockWidget;

import java.net.URISyntaxException;
import java.util.Calendar;

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
import android.os.IBinder;
import android.util.Log;
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
		int color = prefs.getInt("colorId", 0);
		String launcherPackage = prefs.getString("launcherPackage", "");
		
    	Calendar rightNow = Calendar.getInstance();
    	int minute = rightNow.get(Calendar.MINUTE);
    	
    	String dateFormat = prefs.getString("dateFormat", DateFormatChooser.DefaultFormat);
    	
    	if( minute == prevMinute && color == prevColor && launcherPackage == prevLauncher  && dateFormat == prevDateFormat )
    	{
    		return;
    	}
    	
    	prevMinute = minute;
    	prevColor = color;
    	prevLauncher = launcherPackage;
    	
        RemoteViews updateViews = buildUpdate(this);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        
        // Push update for all sized widgets to home screen       
        ComponentName thisWidget = new ComponentName(this, SimpleClockWidgetTwelve.class);        
        manager.updateAppWidget(thisWidget, updateViews);

    }
    
	private RemoteViews buildUpdate(Context context) 
	{
		
		Resources res = getResources();
		CharSequence[] days = res.getTextArray( R.array.days );
		CharSequence[] months = res.getTextArray( R.array.months );
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		int color = prefs.getInt("colorId", 0);

		int layout = UpdateFunctions.GetLayoutFromColorId(color);
		
		RemoteViews views = new RemoteViews(context.getPackageName(), layout);

		Calendar rightNow = Calendar.getInstance();
		
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		
		if( hour == 0 )
		{
			hour = 12;
		}
		
		if( hour > 12 )
		{
			hour -= 12;
		}
		
		int min = rightNow.get(Calendar.MINUTE);
		
		views.setTextViewText(R.id.time_left, String.format("%02d", hour ) );
		views.setTextViewText(R.id.time_right, String.format("%02d", min ) );
		
		String format = prefs.getString("dateFormat", DateFormatChooser.DefaultFormat);
		
		/*String outString = format.replaceAll("dow", days[doW].toString());
    	outString = outString.replaceAll("dd", String.valueOf(doM));
    	outString = outString.replaceAll("mm", String.valueOf(month+1));
    	outString = outString.replaceAll("ms", months[month].toString());
    	outString = outString.replaceAll("yyyy", String.valueOf(year));
    	outString = outString.replaceAll("yy", String.valueOf(year).substring(2));*/
		
		String outString = UpdateFunctions.GetDateWithFormat(res, format);
		
    	views.setTextViewText(R.id.date, outString );
		
		int launcherId = prefs.getInt("launcherId", 0);
		String launcherPackage = prefs.getString("launcherPackage", "");
		
		Intent defineIntent;
		
		if( launcherPackage.length() != 0 )
		{
			defineIntent = new Intent(this, Launcher.class);
			//defineIntent = getPackageManager().getLaunchIntentForPackage(launcherPackage);
		}
		else
		{
			defineIntent = new Intent();
			
	        switch( launcherId )
	        {
	        	case 0:
	        		defineIntent.setComponent(new ComponentName("com.android.alarmclock", "com.android.alarmclock.AlarmClock"));
	        		break;
	        	case 1:
	        		
	        		try 
	        		{
						getPackageManager().getPackageInfo("com.htc.calendar", 0);
						defineIntent.setComponent(new ComponentName("com.htc.calendar","com.htc.calendar.MonthActivity"));
					}
	        		catch (NameNotFoundException e1) 
	        		{
	        			defineIntent.setComponent(new ComponentName("com.android.calendar", "com.android.calendar.LaunchActivity"));
					}
	        		break;
	        	case 2:
	        		try 
	        		{        			
						getPackageManager().getPackageInfo("com.android.browser", 0);
						defineIntent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
					}
	        		catch (NameNotFoundException e1) 
	        		{
	        			Log.d("DigiClock","Browser not found");
					}
	        		break;
	        	case 3:
	        		defineIntent.setComponent(new ComponentName("com.davidgoemans.simpleClockWidget", "com.davidgoemans.simpleClockWidget.ThemeChooser"));
	        		break;
	        }
		}
        
        

        

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, defineIntent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        
        
		return views;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
