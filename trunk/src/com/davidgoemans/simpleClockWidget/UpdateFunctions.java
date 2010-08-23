package com.davidgoemans.simpleClockWidget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class UpdateFunctions 
{
	
	public static class LayoutInfo
	{
		public LayoutInfo()
		{
			layoutId = R.layout.main;
			backgroundImageId = -1;
		}
		
		int layoutId;
		int backgroundImageId;
	}

	static LayoutInfo GetLayoutFromColorId( int colorId, String typeface )
	{
		LayoutInfo info = new LayoutInfo();
		
		// TODO: Read this from XML file or other data storage. Smart huh.
		
		info.layoutId = R.layout.main;
		switch( colorId )
		{
		case 0:
			info.layoutId = R.layout.main;
			break;
		case 1:
			info.layoutId = R.layout.white;
			break;
		case 2:
			info.layoutId = R.layout.velvet;
			break;
		case 3:
			info.layoutId = R.layout.pink;
			break;
		case 4:
			info.layoutId = R.layout.blue;
			break;
		case 5:
			info.layoutId = R.layout.red;
			break;
		case 6:
			info.layoutId = R.layout.green;
			break;
		case 7:
			info.layoutId = R.layout.ghost;
			break;
		case 8:
			info.layoutId = R.layout.dutch;
			break;
		case 9:
			info.layoutId = R.layout.orange;
			break;
		case 10:
			info.layoutId = R.layout.clear_black;
			break;	
		case 11:
			info.backgroundImageId = R.drawable.blank;
			info.layoutId = layoutFromTypeFace(typeface);
			break;	
		case 12:
			info.layoutId = R.layout.yellow;
			break;	
		case 13:
			info.layoutId = R.layout.gold;
			break;
		case 14:
			info.layoutId = R.layout.purple;
			break;
		case 15:
			info.backgroundImageId = R.drawable.widget_solid_white;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		case 16:
			info.backgroundImageId = R.drawable.widget_solid_black;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		case 17:
			info.backgroundImageId = R.drawable.widget_cloud;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		case 18:
			info.backgroundImageId = R.drawable.widget_cubism_white;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		case 19:
			info.backgroundImageId = R.drawable.metal_pill;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		case 20:
			info.backgroundImageId = R.drawable.speech;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		case 21:
			info.backgroundImageId = R.drawable.chip;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		case 22:
			info.backgroundImageId = R.drawable.external_digimetal;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		case 23:
			info.backgroundImageId = R.drawable.external_digipool;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		case 24:
			info.backgroundImageId = R.drawable.external_digisage;
			info.layoutId = layoutFromTypeFace(typeface);
			break;
		}
		
		return info;
	}
	
	static int layoutFromTypeFace(String typeface)
	{
		if( typeface.equalsIgnoreCase("sans") )
		{
			return R.layout.base_sans;
		}
		else if( typeface.equalsIgnoreCase("serif") )
		{
			return R.layout.base_serif;
		}
		else if( typeface.equalsIgnoreCase("monospace") )
		{
			return R.layout.base_monospace;
		}
		else
		{
			return R.layout.base_normal;
		}
	}
	
	static String convertToNewDateFormat( String format )
	{
		String outString = format.replaceAll("dow", "EEE");
    	outString = outString.replaceAll("mm", "MM");
    	outString = outString.replaceAll("ms", "MMM");
    	
    	return outString;
	}
	
	static String GetDateWithFormat( String format ) throws IllegalArgumentException
	{
		String outString = convertToNewDateFormat( format );
		SimpleDateFormat frmt = new SimpleDateFormat(outString);
		Date now = new Date();
		return frmt.format(now);
	}
	
	public static RemoteViews buildUpdate(Context context, boolean twelve ) 
	{
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);		
		int color = prefs.getInt("colorId", 0);
		String typeface = prefs.getString("typeface", "normal");
		
		LayoutInfo info = UpdateFunctions.GetLayoutFromColorId(color, typeface);
		
		RemoteViews views = new RemoteViews(context.getPackageName(), info.layoutId);
		
		if( info.backgroundImageId != -1 )
		{
			// Opens the way for real themes!
			views.setImageViewBitmap(R.id.background, BitmapFactory.decodeResource(context.getResources(), info.backgroundImageId) );
		}
		
		if( color > 14 || color == 11 )
		{
			int textColor = prefs.getInt("textColor", 0);
			if( textColor != 0 )
			{
				views.setTextColor(R.id.date, textColor);
				views.setTextColor(R.id.time, textColor);
			}
			
			String timeFormat = "HH:mm";
			if( twelve )
			{
				
				if( !prefs.getBoolean("leadingZero", true) )
				{
					timeFormat = "h:mm";
				}
				else
				{
					timeFormat = "hh:mm";
				}
			}
			else
			{
				if( !prefs.getBoolean("leadingZero", true) )
				{
					timeFormat = "H:mm";
				}
				else
				{
					timeFormat = "HH:mm";
				}	
			}
			
			
			SimpleDateFormat frmt = new SimpleDateFormat(timeFormat);
			Date now = new Date();
			String time = frmt.format(now);
			views.setTextViewText(R.id.time, time);
			
			String format = prefs.getString("dateFormat", DateFormatChooser.DefaultFormat);
			
			String outString = UpdateFunctions.GetDateWithFormat(format);

			boolean dateEnabled = prefs.getBoolean("dateEnabled", true);
			if( dateEnabled )
			{
				views.setViewVisibility(R.id.date, View.VISIBLE);
			}
			else
			{
				views.setViewVisibility(R.id.date, View.INVISIBLE);
			}
			
	    	views.setTextViewText(R.id.date, outString );	
		}
		else
		{
			int textColor = prefs.getInt("textColor", 0);
			if( textColor != 0 )
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
			
			if( twelve )
			{
				if( hour == 0 )
				{
					hour = 12;
				}
				
				if( hour > 12 )
				{
					hour -= 12;
				}
			}
			
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

			boolean dateEnabled = prefs.getBoolean("dateEnabled", true);
			if( dateEnabled )
			{
				views.setViewVisibility(R.id.date, View.VISIBLE);
			}
			else
			{
				views.setViewVisibility(R.id.date, View.INVISIBLE);
			}
			
	    	views.setTextViewText(R.id.date, outString );
	    	
		}

		
		int launcherId = prefs.getInt("launcherId", 0);
		String launcherPackage = prefs.getString("launcherPackage", "");
		
		Intent defineIntent;
		
		if( launcherPackage.length() != 0 )
		{
			defineIntent = new Intent(context, Launcher.class);
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
	        			context.getPackageManager().getPackageInfo("com.htc.calendar", 0);
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
	        			context.getPackageManager().getPackageInfo("com.android.browser", 0);
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
}
