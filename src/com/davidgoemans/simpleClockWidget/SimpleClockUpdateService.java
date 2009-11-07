package com.davidgoemans.simpleClockWidget;

import java.util.Calendar;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.widget.RemoteViews;

public class SimpleClockUpdateService extends Service 
{   
    @Override
    public void onStart(Intent intent, int startId) 
    {		
        RemoteViews updateViews = buildUpdate(this);
        
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        
        // Push update for all sized widgets to home screen       
        ComponentName thisWidget = new ComponentName(this, SimpleClockWidget.class);
        manager.updateAppWidget(thisWidget, updateViews);
    }
    
	private RemoteViews buildUpdate(Context context) 
	{
		Resources res = getResources();
		CharSequence[] days = res.getTextArray( R.array.days );
		CharSequence[] months = res.getTextArray( R.array.months );

		
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);

		Calendar rightNow = Calendar.getInstance();
		
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		
		int min = rightNow.get(Calendar.MINUTE);
		
		int doW = rightNow.get(Calendar.DAY_OF_WEEK) - 1;
		int doM = rightNow.get(Calendar.DAY_OF_MONTH);
		int month = rightNow.get(Calendar.MONTH);
		int year = rightNow.get(Calendar.YEAR);

		views.setTextViewText(R.id.time_left, String.format("%02d", hour ) );
		views.setTextViewText(R.id.time_right, String.format("%02d", min ) );
		views.setTextViewText(R.id.date, String.format("%s, %d %s %d", days[doW], doM, months[month], year) );
		
        Intent defineIntent = new Intent();
        defineIntent.setComponent(new ComponentName("com.android.alarmclock", "com.android.alarmclock.AlarmClock"));
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