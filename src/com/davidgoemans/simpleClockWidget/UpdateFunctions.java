package com.davidgoemans.simpleClockWidget;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.PowerManager;
import android.text.format.DateFormat;
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
			backgroundImagePath = null;
		}
		
		int layoutId;
		int backgroundImageId;
		String backgroundImagePath;
	}

	static LayoutInfo GetLayoutFromColorId( int colorId, String typeface, String backgroudPath)
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
		default:
			info.backgroundImageId = -1;
			info.backgroundImagePath = backgroudPath;
			info.layoutId = layoutFromTypeFace(typeface);
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
		//outString = 
		//SimpleDateFormat frmt = new SimpleDateFormat(outString);
		//Date now = new Date();
		
		return (String) DateFormat.format(outString, new Date());//frmt.format(now);
	}
	
	public static RemoteViews buildUpdate(Context context, boolean twelve ) 
	{
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);		
		int color = prefs.getInt("colorId", 0);
		String typeface = prefs.getString("typeface", "normal");
		String backgroudPath = prefs.getString("bgPath", null);

		LayoutInfo info = UpdateFunctions.GetLayoutFromColorId(color, typeface, backgroudPath);
		
		RemoteViews views = new RemoteViews(context.getPackageName(), info.layoutId);
		
		Log.d("DigiClock","Backgroud image: "+ info.backgroundImagePath + " and ID: " + info.backgroundImageId);
		
		if( info.backgroundImageId != -1 )
		{
			// Opens the way for real themes!
			views.setImageViewBitmap(R.id.background, BitmapFactory.decodeResource(context.getResources(), info.backgroundImageId) );
		}
		else if(info.backgroundImagePath != null)
		{
			Bitmap bmp = BitmapFactory.decodeFile(info.backgroundImagePath);
			Log.d("DigiClock","BMP: " + bmp);
			views.setImageViewBitmap(R.id.background,  bmp);
		}
		
		
		if( color > 14 || color == 11 || info.backgroundImagePath != null )
		{
			float textTimeSize = prefs.getFloat("textTimeSize", 52);
			views.setFloat(R.id.time, "setTextSize", textTimeSize);
			
			float textDateSize = prefs.getFloat("textDateSize", 14);
			views.setFloat(R.id.date, "setTextSize", textDateSize);
			
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
	
	static void LaunchSettingsApp(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		if( prefs.getBoolean("settingsShown", false) ) return;
		
 		// Every time added, launch settings
 		try 
 		{
 			PendingIntent pendingIntent = PendingIntent.getActivity(context,0, new Intent(context, SettingsList.class), 0);
			pendingIntent.send();
		} 
 		catch (CanceledException e) 
		{
			e.printStackTrace();
		}
 		
 		
 		SharedPreferences.Editor ed = prefs.edit();
 		ed.putBoolean("settingsShown", true);
		ed.commit();
	}

	public static List<DigiTheme> getOnlineThemes()
	{
		List<DigiTheme> themes = new ArrayList<DigiTheme>();
		URL site = null;
		HttpURLConnection conn = null;
		InputStream stream = null;
		
		try
		{
			site = new URL("http://davidgoemans.com/DigiClock/list_themes.php?device=1");
	    	conn = (HttpURLConnection)site.openConnection();
			conn.setDoInput(true);
	    	conn.connect();
	    	
	    	stream = conn.getInputStream();
	    	byte[] data = new byte[stream.available()];
	    	stream.read(data);
	    	
	    	String dataString = new String(data, 0, data.length);
	    		    	
	    	JSONTokener tokener = new JSONTokener(dataString);
	    	
	    	while(tokener.more())
	    	{
	    		JSONObject cur = (JSONObject)tokener.nextValue();

	    		DigiTheme theme = new DigiTheme(cur.getString("Name"), 
	    				cur.getString("Creator"), 
	    				URLDecoder.decode( cur.getString("URL") ), 
	    				(float)cur.getDouble("Price"));
	    			    		
	    		themes.add(theme);
	    	}
		}
		catch(Exception e)
		{
			Log.w("DigiClock", "Couldn't download bitmap" + e.getMessage());
		}
		finally
		{
			try
			{
				if( stream != null ) stream.close();
			}
			catch(Exception e)
			{
				Log.w("DigiClock", "Failed to close stream: " + e.getMessage());
			}
			
			if( conn != null ) conn.disconnect();
		}
    	
    	return themes;
	}

	// Returns the path where the bitmap got written to
	// Also writes that to the theme
	public static String downloadThemeImage(DigiTheme theme, Context context)
	{
		OutputStream os = null;
		
    	try 
    	{
    		Bitmap bmp = getBitmapFromURL(theme.URL);
    		
    		if( bmp == null ) return null;
    		
    		Log.d("DigiClock", "Gonna write: " + context);
			
			os = context.openFileOutput(theme.Name + ".png", Context.MODE_PRIVATE);
			
			theme.ImageLocation = context.getFilesDir() + "/" + theme.Name + ".png";
			
			Log.d("DigiClock", "Image location: " + theme.ImageLocation );
			
    		bmp.compress(CompressFormat.PNG, 5, os);
    		
    		os.close();
    	}
    	catch (Exception e) 
    	{
    		Log.w("DigiClock", "Error writing theme image to file" + e.getMessage());
    		return null;
    	}
		
    	return theme.ImageLocation;
	}
	
	private static Bitmap getBitmapFromURL(String url)
	{
		HttpURLConnection conn = null;
		InputStream stream = null;
		Bitmap bmp = null;
		
		try
		{
			URL site = new URL(url);
	    	conn = (HttpURLConnection)site.openConnection();
			conn.setDoInput(true);
	    	conn.connect();
	    	
	    	stream = conn.getInputStream();
	    	
	    	bmp = BitmapFactory.decodeStream(stream);
	    	
	    	stream.close();
		}
		catch(Exception e)
		{
			Log.w("DigiClock", "Connection error");
			
			return null;
		}
		finally
		{
			if( conn != null )
				conn.disconnect();
		}
    	
    	return bmp;
	}
	
	public static void SetTwelve(Context context, boolean twelve)
	{
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
 		SharedPreferences.Editor ed = prefs.edit();
 		ed.putBoolean("twelvehour", twelve);
		ed.commit();
	}
	
	public static void Invalidate(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
 		SharedPreferences.Editor ed = prefs.edit();		
		ed.putBoolean("invalidate", true);
		ed.commit();
	}
	
	static int prevMinute = -1;
	
	public enum UpdateType
	{
		NotRequired,
		TimeChange,
		Invalidated
	}
	
	public static boolean TimeChanged()
	{
    	Calendar rightNow = Calendar.getInstance();
    	int minute = rightNow.get(Calendar.MINUTE);
    	
    	return minute != prevMinute;
	}
	
	public static UpdateType UpdateWidget(Context context, Class<?> cls)
    {
    	SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
    	
    	Calendar rightNow = Calendar.getInstance();
    	int minute = rightNow.get(Calendar.MINUTE);
    	
     	boolean invalidated = prefs.getBoolean("invalidate", false);
    	
		if( minute == prevMinute && !invalidated )
    	{
    		return UpdateType.NotRequired;
    	}
		
 		SharedPreferences.Editor ed = prefs.edit();		
		ed.putBoolean("invalidate", false);
		ed.commit();
		
    	prevMinute = minute;
    	
    	boolean twelve = prefs.getBoolean("twelvehour", true);
        RemoteViews updateViews = UpdateFunctions.buildUpdate(context, twelve);
        
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        // Push update for all sized widgets to home screen    
        ComponentName thisWidget = new ComponentName(context, cls);
        manager.updateAppWidget(thisWidget, updateViews);
        
        return invalidated ? UpdateType.Invalidated : UpdateType.TimeChange;
    }
}
