package com.davidgoemans.simpleClockWidget;

import java.util.Calendar;

import com.davidgoemans.simpleClockWidget.UpdateFunctions.UpdateType;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

class UpdateTimer implements Runnable 
{
	private Context context;
	private Handler handler;
	
	long delayTime = 3000;
	
	public UpdateTimer(Handler handler, Context context)
	{
		this.context = context;
		this.handler = handler;
		ResetTimer();
	}
	
	public void ResetTimer()
	{
		handler.removeCallbacks(this);
		delayTime = 3000;
		handler.post(this);
	}
	
	public void run() 
	{
		//Log.d("DigiClock", "Tick");
		
		SharedPreferences prefs = context.getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		boolean twelve = prefs.getBoolean("twelvehour", true);
		UpdateFunctions.UpdateWidget(context, twelve ? SimpleClockWidgetTwelve.class : SimpleClockWidget.class);
		
		if( handler != null )
		{
			handler.postDelayed(this, delayTime);
		}
	}
}