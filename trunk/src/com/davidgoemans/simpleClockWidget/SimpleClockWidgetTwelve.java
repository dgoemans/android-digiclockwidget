package com.davidgoemans.simpleClockWidget;

import java.util.Timer;
import java.util.TimerTask;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SimpleClockWidgetTwelve extends AppWidgetProvider 
{	
    class RunUpdateService extends TimerTask 
    {
    	public Context context = null;    	
    	
    	public void run() 
    	{
    		SimpleClockWidgetTwelve.this.fixServices(context);
   			context.startService(new Intent(context, SimpleClockUpdateServiceTwelve.class));
    	}
    }
    
    static RunUpdateService m_serviceTask = null;
    static Timer m_serviceTimer = null;

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
 			m_serviceTimer.schedule(m_serviceTask, SimpleClockWidget.delayTime, SimpleClockWidget.tickTime);
 		}
    }
    
    @Override
	public void onReceive(Context context, Intent intent) 
	{
    	Log.d("DigiClock","Receive");
		fixServices(context);
		super.onReceive(context, intent);
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
 		context.stopService(new Intent(context, SimpleClockUpdateServiceTwelve.class));
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
		m_serviceTimer.schedule(m_serviceTask, SimpleClockWidget.delayTime, SimpleClockWidget.tickTime);
		
		super.onEnabled(context);
 	}
}
