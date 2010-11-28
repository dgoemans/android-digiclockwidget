package com.davidgoemans.simpleClockWidget;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SimpleClockUpdateService extends Service 
{	
	Handler handler;
	private UpdateTimer timer = null;
	
	@Override
    public void onStart(Intent intent, int startId) 
    {
    	Log.d("DigiClock", "StartID: " + startId + " Intent: " + intent);
    	if( timer != null )
    	{
    		Log.d("DigiClock", "Killing timer");
    		//handler.removeCallbacks(timer);
    		timer.ResetTimer();
    		//handler.post(timer); 
    	}
    	else
    	{
    		handler = new Handler();
    		timer = new UpdateTimer(handler, getApplicationContext());
        	
        	//handler.post(timer);    	
        }

    }
        
    @Override
    public void onDestroy() 
    {
    	handler.removeCallbacks(timer);
    	//stopService(new Intent(this, SimpleClockUpdateService.class));
    	startService(new Intent(this, SimpleClockUpdateService.class));
    }

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
}