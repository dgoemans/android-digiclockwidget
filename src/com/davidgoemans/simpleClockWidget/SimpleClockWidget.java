package com.davidgoemans.simpleClockWidget;

import java.util.Timer;
import java.util.TimerTask;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SimpleClockWidget extends AppWidgetProvider 
{
    class RunUpdateService extends TimerTask 
    {
    	public Context context = null;    	
    	
    	public void run() 
    	{
   			context.startService(new Intent(context, SimpleClockUpdateService.class));
    	}
    }
    
    static RunUpdateService m_serviceTask = null;
    static Timer m_serviceTimer = null;

    
 	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
 	{		
 		if( m_serviceTask == null )
 		{
 			m_serviceTask = new RunUpdateService();
 	 		m_serviceTask.context = context;
 		}
 		
 		if( m_serviceTimer == null )
 		{
 			m_serviceTimer = new Timer();
 			m_serviceTimer.schedule(m_serviceTask, 2000, 2000);
 		}
    }
 	
 	@Override
 	public void onDisabled(Context context) {
 		// TODO Auto-generated method stub
 		super.onDisabled(context);
 		
 		if ( m_serviceTimer != null )
 		{
 			m_serviceTimer.cancel();
 		}
 	}
 	
 	@Override
 	public void onEnabled(Context context) 
 	{
 		super.onEnabled(context);
 		
 		if ( m_serviceTimer != null )
 		{
 			m_serviceTimer.cancel();
 		}
 		
 		m_serviceTask = new RunUpdateService();
 		m_serviceTask.context = context;
 		m_serviceTimer = new Timer();
 		m_serviceTimer.schedule(m_serviceTask, 2000, 2000);
 	}
}
