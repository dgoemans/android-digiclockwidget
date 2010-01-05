package com.davidgoemans.simpleClockWidget;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SimpleClockWidget extends AppWidgetProvider 
{
	
	public static final int tickTime = 5000;
	public static final int delayTime = 0;
	
	public static final String PREFS_NAME = "digiClockPrefs";
	
    class RunUpdateService extends TimerTask 
    {
    	public Context context = null;    	
    	
    	public void run() 
    	{
    		SimpleClockWidget.this.fixServices(context);
   			context.startService(new Intent(context, SimpleClockUpdateService.class));
    	}
    }

    static RunUpdateService m_serviceTask = null;
	static Timer m_serviceTimer = null;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.d("DigiClock","Receive");
		fixServices(context);
		super.onReceive(context, intent);
	}

    public void fixServices(Context context)
    {
 		if( m_serviceTask == null )
 		{
 			m_serviceTask = new RunUpdateService();
 	 		m_serviceTask.context = context;
 		}
 		
 		if( m_serviceTimer == null )
 		{
 			m_serviceTimer = new Timer();
 			m_serviceTimer.schedule(m_serviceTask, delayTime, tickTime);
 		}
    }
    
 	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
 	{	
 		fixServices(context);
    }
 	
 	@Override
 	public void onDisabled(Context context) 
 	{
 		Log.d("DigiClock","Disabled");
 		if ( m_serviceTimer != null )
 		{
 			m_serviceTimer.cancel();
 			m_serviceTimer.purge();
 			
 			m_serviceTimer = null;
 			m_serviceTask = null;
 		}
 		
 		super.onDisabled(context);
 	}
 	
 	@Override
 	public void onEnabled(Context context) 
 	{
 		Log.d("DigiClock","Enabled");
 		if ( m_serviceTimer != null )
 		{
 			m_serviceTimer.cancel();
 		}
 		
 		m_serviceTask = new RunUpdateService();
 		m_serviceTask.context = context;
		
		m_serviceTimer = new Timer();
		m_serviceTimer.schedule(m_serviceTask, delayTime, tickTime);
 		
 		super.onEnabled(context);
 	}
}
