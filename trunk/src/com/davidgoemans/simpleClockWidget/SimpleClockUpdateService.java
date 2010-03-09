package com.davidgoemans.simpleClockWidget;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
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
    	
        RemoteViews updateViews = buildUpdate(this);
        
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        
        // Push update for all sized widgets to home screen       
        ComponentName thisWidget = new ComponentName(this, SimpleClockWidget.class);
        manager.updateAppWidget(thisWidget, updateViews);
    }
    
	private RemoteViews buildUpdate(Context context) 
	{
		Resources res = getResources();


		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		int color = prefs.getInt("colorId", 0);

		int layout = UpdateFunctions.GetLayoutFromColorId(color);
		
		RemoteViews views = new RemoteViews(context.getPackageName(), layout);
		
		int textColor = prefs.getInt("textColor", -1);
		if( textColor != -1 )
		{
			views.setTextColor(R.id.date, textColor);
			views.setTextColor(R.id.time_left, textColor);
			views.setTextColor(R.id.time_right, textColor);
			
			// The ones with the Colon
			if( color == 10 || color == 11 )
			{
				views.setTextColor(R.id.time_sep, textColor);
			}
		}
		
		Calendar rightNow = Calendar.getInstance();
		
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		int min = rightNow.get(Calendar.MINUTE);
		
		if(prefs.getBoolean("leadingZero", true))
		{
			views.setTextViewText(R.id.time_left, String.format("%02d", hour ) );
		}
		else
		{
			views.setTextViewText(R.id.time_left, String.format("%d", hour ) );
		}
		
		views.setTextViewText(R.id.time_right, String.format("%02d", min ) );
		
		String format = prefs.getString("dateFormat", DateFormatChooser.DefaultFormat);

		String outString = UpdateFunctions.GetDateWithFormat(format);
 
    	views.setTextViewText(R.id.date, outString );
		
		int launcherId = prefs.getInt("launcherId", 0);
		String launcherPackage = prefs.getString("launcherPackage", "");
		
		Intent defineIntent;
		
		if( launcherPackage.length() != 0 )
		{
			defineIntent = new Intent(this, Launcher.class);
			//getPackageManager().getLaunchIntentForPackage(launcherPackage);
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