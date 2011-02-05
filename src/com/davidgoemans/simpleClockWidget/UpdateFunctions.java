package com.davidgoemans.simpleClockWidget;

import java.io.FileOutputStream;
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
import java.util.zip.GZIPInputStream;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint.Align;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RemoteViews;

public class UpdateFunctions 
{
	static int[][] legacyThemes = 
	{
			{
				R.drawable.widget_bg, R.drawable.widget_bg_white, 
				R.drawable.widget_bg_velvet, R.drawable.widget_bg_pink,
				R.drawable.widget_bg_blue, R.drawable.widget_bg_red,
				R.drawable.widget_bg_green, R.drawable.widget_bg_ghost, 
				R.drawable.widget_bg_dutch, R.drawable.widget_bg_orange, 
				R.drawable.blank, R.drawable.blank,
				R.drawable.widget_bg_yellow, R.drawable.widget_bg_gold, 
				R.drawable.widget_bg_purple
			},
			{ 
				R.drawable.date_bg, R.drawable.date_bg_white,
				R.drawable.date_bg_velvet, R.drawable.date_bg_pink,
				R.drawable.date_bg_blue, R.drawable.date_bg_red,
				R.drawable.date_bg_green, R.drawable.date_bg_ghost,
				R.drawable.date_bg_orange, R.drawable.date_bg_orange,
				R.drawable.blank, R.drawable.blank, 
				R.drawable.date_bg_yellow, R.drawable.date_bg_gold, 
				R.drawable.date_bg_purple,
			}
			
	};
	
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
		case 11:
		case 10:
			info.backgroundImageId = R.drawable.blank;
			info.layoutId = layoutFromTypeFace(typeface);
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
	
	static String GetFormattedTime(SharedPreferences prefs)
	{
		boolean twelve = prefs.getBoolean("twelvehour", true);
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
		return frmt.format(now);
	}
	
	
	public static RemoteViews buildUpdate(Context context, int id ) 
	{
		AppWidgetProviderInfo inf = AppWidgetManager.getInstance(context).getAppWidgetInfo(id);
		
		// Double size of everything
		float scale = inf.minHeight == 219 ? 1.5f : 1f;

		float sizeMultiplier = context.getResources().getDisplayMetrics().density * scale;
		float fontSizeMultiplier = context.getResources().getDisplayMetrics().scaledDensity * scale;
		
		// Constants
		float canvasWidth = 160;
		float canvasHeight = 100;
		float dateWidth = 144;
		float dateHeight = 21;
		float padding = 5;
		
		Log.d("DigiClock", "Updating with ID: " + id);
		
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);		
		
		// Properties
		int color = prefs.getInt("colorId", 0);
		int bgAlpha = prefs.getInt("bgAlpha", 255);
		int textAlpha = prefs.getInt("textAlpha", 255);
		int textColor = prefs.getInt("textColor", Color.WHITE);
		
		String typeface = prefs.getString("typeface", "normal");
		String backgroudPath = prefs.getString("bgPath", null);

		LayoutInfo info = UpdateFunctions.GetLayoutFromColorId(color, typeface, backgroudPath);
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.base_normal);
		
		Bitmap myBitmap = Bitmap.createBitmap((int)(canvasWidth*sizeMultiplier), 
				(int)(canvasHeight*sizeMultiplier), 
				Bitmap.Config.ARGB_8888);
		
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        
        boolean legacy = false; 
        float legacyOffset = 0;
        
        paint.setSubpixelText(true);
    	paint.setAntiAlias(true);
        
    	paint.setAlpha(bgAlpha);
    	
        if(info.backgroundImagePath != null)
        {
        	Bitmap background = BitmapFactory.decodeFile(info.backgroundImagePath);
        	myCanvas.drawBitmap(Bitmap.createScaledBitmap(background, myBitmap.getWidth(), myBitmap.getHeight(), true), 
	    			0, 
	    			0, 
	    			paint);
        	
        }
        else if(info.backgroundImageId != -1)
        {
        	// TODO: Refactor into function like:
        	// drawBackground(sourceBitmap, canvas, paint, targetBitmap );
        	// OR: Bitmap getBackgroundFromInfo( LayoutInfo info )
        	Bitmap background = BitmapFactory.decodeResource(context.getResources(), info.backgroundImageId);
        	myCanvas.drawBitmap(Bitmap.createScaledBitmap(background, myBitmap.getWidth(), myBitmap.getHeight(), false), 
	    			0, 
	    			0, 
	    			paint);	
        }
        else 
        {
        	legacy = true;
        	legacyOffset = 10;
        	
        	Bitmap background = BitmapFactory.decodeResource(context.getResources(), legacyThemes[0][color]);
        	myCanvas.drawBitmap(Bitmap.createScaledBitmap(background, myBitmap.getWidth(), myBitmap.getHeight(), false), 
	    			0, 
	    			0, 
	    			paint);
        	
        	Bitmap datebg = BitmapFactory.decodeResource(context.getResources(), legacyThemes[1][color]);
        	myCanvas.drawBitmap(Bitmap.createScaledBitmap(datebg, 
        			(int)(dateWidth*sizeMultiplier), 
        			(int)(dateHeight*sizeMultiplier), false),
        			0.5f*(canvasWidth - dateWidth)*sizeMultiplier,
					(canvasHeight - dateHeight)*sizeMultiplier,
	    			paint);
        }

        
        Typeface clock;

        if(typeface.equalsIgnoreCase("normal"))
        {
        	clock = Typeface.DEFAULT;
        }
        else if(typeface.equalsIgnoreCase("normal_bold"))
        {
        	clock = Typeface.DEFAULT_BOLD;
        }
        else if(typeface.equalsIgnoreCase("sans"))
        {
        	clock = Typeface.SANS_SERIF;
        }
        else if(typeface.equalsIgnoreCase("serif"))
        {
        	clock = Typeface.SERIF;	
        }
        else if(typeface.equalsIgnoreCase("monospace"))
        {
        	clock = Typeface.MONOSPACE;
        }
        else
        {
        	try
        	{
        		clock = Typeface.createFromAsset(context.getAssets(),typeface);
        	}
        	catch( RuntimeException e )
        	{
        		clock = Typeface.DEFAULT_BOLD;
        	}
        }
        paint.setAntiAlias(true);
        paint.setSubpixelText(false);

        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        
        paint.setColor(textColor);
        paint.setAlpha(textAlpha);
        
        paint.setTextAlign(Align.CENTER);
        
        if(  prefs.getBoolean("shadowEnabled", true) )
        {
        	int shadowColor = Color.argb(textAlpha, 0, 0, 0);
        	paint.setShadowLayer(2*sizeMultiplier, 1*sizeMultiplier, 1*sizeMultiplier, shadowColor);
        }
        
		float textTimeSize = prefs.getFloat("textTimeSize", 52);		
		paint.setTextSize(textTimeSize * fontSizeMultiplier);
		
		String timeString = GetFormattedTime(prefs);
		
		int yPosition = 65;
		
		if( legacy )
		{
			int seperatorIndex = timeString.indexOf(':');
			
			myCanvas.drawText(timeString.substring(0, seperatorIndex), 
	        		0.25f*canvasWidth*sizeMultiplier, 
	        		yPosition*sizeMultiplier, paint);
			myCanvas.drawText(timeString.substring(seperatorIndex+1),
					0.75f*canvasWidth*sizeMultiplier, 
	        		yPosition*sizeMultiplier, paint);
		}
		else
		{
			myCanvas.drawText(timeString, 
        		0.5f*canvasWidth*sizeMultiplier, 
        		yPosition*sizeMultiplier, paint);
		}
        
		boolean amPmMarkerEnabled = prefs.getBoolean("amPmMarkerEnabled", true);
		if( amPmMarkerEnabled )
		{
			float markerSize = prefs.getFloat("amPmSize", 20);		
			paint.setTextSize(markerSize * fontSizeMultiplier);
			
			
			SimpleDateFormat fmt = new SimpleDateFormat("a");
			String marker = fmt.format(new Date());
			
			float width = paint.measureText(marker);
			
			myCanvas.drawText(marker, 
					(canvasWidth - padding)*sizeMultiplier - width/2, 
					22 * sizeMultiplier, 
					paint);
		}

		boolean dateEnabled = prefs.getBoolean("dateEnabled", true);
		if( dateEnabled )
		{
			float textDateSize = prefs.getFloat("textDateSize", 14);
			paint.setTextSize(textDateSize * fontSizeMultiplier);
			
			String format = prefs.getString("dateFormat", DateFormatChooser.DefaultFormat);
			
			myCanvas.drawText(UpdateFunctions.GetDateWithFormat(format), 
					80*sizeMultiplier, 
					(85+legacyOffset)*sizeMultiplier, 
					paint);
		}
        
        views.setImageViewBitmap(R.id.background, myBitmap);
        
        views.setViewVisibility(R.id.date, View.INVISIBLE);
        views.setViewVisibility(R.id.time, View.INVISIBLE);
        
        AttachIntentToView(context, views);
        
        return views;
        
		/*
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
		
		AttachIntentToView(context, views);
		
		return views;
		
		*/
	}
	
	private static void AttachIntentToView(Context context, RemoteViews view)
	{
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
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
        view.setOnClickPendingIntent(R.id.widget, pendingIntent);
	}	
	
	static boolean LaunchSettingsApp(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		if( prefs.getBoolean("settingsShown", false) ) return false;
		
 		// Every time added, launch settings
 		try 
 		{
 			PendingIntent pendingIntent = PendingIntent.getActivity(context,0, new Intent(context, SettingsList.class), 0);
			pendingIntent.send();
		} 
 		catch (CanceledException e) 
		{
			e.printStackTrace();
			return false;
		}
 	
 		SharedPreferences.Editor ed = prefs.edit();
 		ed.putBoolean("settingsShown", true);
		ed.commit();
		
		return true;
	}
	
	public static boolean LaunchActivity(Context context, Class toLaunch)
	{
 		try 
 		{
 			PendingIntent pendingIntent = PendingIntent.getActivity(context,0, new Intent(context, toLaunch), 0);
			pendingIntent.send();
		} 
 		catch (CanceledException e) 
		{
			e.printStackTrace();
			return false;
		}
 		
 		return true;
	}
	
	/*
	public static boolean LaunchNewsApp(Context context)
	{
 		try 
 		{
 			PendingIntent pendingIntent = PendingIntent.getActivity(context,0, new Intent(context, News.class), 0);
			pendingIntent.send();
		} 
 		catch (CanceledException e) 
		{
			e.printStackTrace();
			return false;
		}
 		
 		return true;
	}
	*/
	
	public static String GetDataFromStream(InputStream stream)
	{
    	int size = 1000;
    	byte[] data = new byte[size];
    	
    	int i=0;
    	int dataValue = 0;
    	while(dataValue != -1)
    	{
    		try 
    		{
				dataValue = stream.read();
			}
    		catch (IOException e) 
    		{
    			Log.d("DigiClock", "Data stream corrupt");
				e.printStackTrace();
			}

    		data[i] = (byte)dataValue;
    		
    		i++;
    		
    		// If we've gone over the limit, expand the array
    		if( i >= size )
    		{
    			size *= 2;
    			byte[] newData = new byte[size];
    			int j = 0;
    			for(byte cur : data)
    			{
    				newData[j] = cur;
    				j++;
    			}
    			data = newData;
    		}
    	}
    	
    	
    	return new String(data, 0, data.length);
	}

	public static List<DigiTheme> getOnlineThemes()
	{
		List<DigiTheme> themes = new ArrayList<DigiTheme>();
		URL site = null;
		HttpURLConnection conn = null;
		InputStream stream = null;
		
		try
		{
			site = new URL("http://dgoemans.com/DigiClock/list_themes.php?device&compressed");
			
	    	conn = (HttpURLConnection)site.openConnection();
			conn.setDoInput(true);
	    	conn.connect();
	    	
	    	stream = conn.getInputStream();
	    	
	    	GZIPInputStream zip = new GZIPInputStream(stream);
	    	
	    	String dataString = GetDataFromStream(zip);
	    		    	
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
			Log.w("DigiClock", "Couldn't download theme " + e.getMessage());
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
		FileOutputStream os = null;
		
    	try 
    	{
    		Bitmap bmp = getBitmapFromURL(theme.URL);
    		
    		if( bmp == null ) return null;
			
    		theme.ImageLocation = context.getFilesDir() + "/" + theme.Name + ".png";
    		
    		if( context.getFileStreamPath(theme.Name + ".png").exists() )
    			return theme.ImageLocation;
    		
			os = context.openFileOutput(theme.Name + ".png", Context.MODE_PRIVATE);
			
    		bmp.compress(CompressFormat.PNG, 5, os);
    		
    		os.getFD().sync();
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
	
	public static UpdateType UpdateWidget(Context context)
    {
    	SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
    	
    	Calendar rightNow = Calendar.getInstance();
    	int minute = rightNow.get(Calendar.MINUTE);
    	
     	boolean invalidated = prefs.getBoolean("invalidate", false);
    	
		if( minute == prevMinute && !invalidated )
    	{
    		return UpdateType.NotRequired;
    	}
		
		int version = prefs.getInt("version", 0);
		int newVersion = 0;
		try 
		{
			newVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} 
		catch (NameNotFoundException e) 
		{
			e.printStackTrace();
		}
		
 		SharedPreferences.Editor ed = prefs.edit();
 		
 		if( version < newVersion )
		{
 			LaunchActivity(context, News.class);
			ed.putInt("version", newVersion);
			Log.d("DigiClock", "New Version: " + newVersion);
		}
 		
 		if( !prefs.contains("twelvehour") )
 		{
 			LaunchActivity(context, TimeSelector.class);
 		}
 		
		ed.putBoolean("invalidate", false);
		ed.commit();
		
    	prevMinute = minute;

        UpdateWidgetsOfType(context, SimpleClockWidget.class);
        UpdateWidgetsOfType(context, SimpleClockWidgetTwelve.class);
        UpdateWidgetsOfType(context, SimpleClockWidgetLarge.class);

        
        return invalidated ? UpdateType.Invalidated : UpdateType.TimeChange;
    }
	
	static void UpdateWidgetsOfType(Context context, Class<?> cls)
	{
		RemoteViews views = null;
		
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		
		ComponentName widgets = new ComponentName(context, cls);
		for( int id : manager.getAppWidgetIds(widgets) )
        {
			views = UpdateFunctions.buildUpdate(context, id);
        	manager.updateAppWidget(id, views);
        }
	}
}
