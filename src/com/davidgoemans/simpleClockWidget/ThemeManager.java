package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class ThemeManager 
{
	public int[] OfflineThemes = 
	{ 
			R.drawable.widget_bg, // 0
			R.drawable.widget_bg_white, // 1
			R.drawable.widget_bg_velvet, // 2
			R.drawable.widget_bg_pink, // 3
			R.drawable.widget_bg_blue, // 4
			R.drawable.widget_bg_red, // 5
			R.drawable.widget_bg_green, // 6
			R.drawable.widget_bg_ghost, // 7
			R.drawable.widget_bg_dutch, // 8
			R.drawable.widget_bg_orange, // 9
			R.drawable.blank, // 10
			R.drawable.blank, // 11
			R.drawable.widget_bg_yellow, // 12
			R.drawable.widget_bg_gold, // 13
			R.drawable.widget_bg_purple, // 14
			R.drawable.widget_solid_white, // 15
			R.drawable.widget_solid_black, // 16
			R.drawable.widget_cloud, // 17
			R.drawable.widget_cubism_white, // 18
			R.drawable.metal_pill, // 19
			R.drawable.speech, // 20
			R.drawable.chip, // 21
			R.drawable.external_digimetal, // 22
			R.drawable.external_digipool, // 23
			R.drawable.external_digisage // 24
	};

	Lock listLock;

	private static boolean currentTaskFinished = false;
	
	List<IThemeListListener> listeners;
	
	private enum Task
	{
		GetOnlineList,
		GetImage,
	};
	
	private Task currentTask;

	private Handler handler;

	// All the themes
	private List<DigiTheme> themes;
	
	private int currentThemeIndexForImageDownload = 0;
	
	private Context context;
	
	private boolean terminatePending = false;
	
	private boolean themesPopulated = false;
	
	public boolean IsPopulatingComplete()
	{
		return themesPopulated;
	}

	public ThemeManager()
	{
		themes = new ArrayList<DigiTheme>();
		listeners = new ArrayList<IThemeListListener>();
		listLock = new ReentrantLock();
		
		terminatePending = false;
	}
	
	public void terminate()
	{
		terminatePending = true;
	}
		
	public void populateThemes(Context context, IThemeListListener listener) 
	{
		themesPopulated = false;
		currentTaskFinished = false;
		this.context = context;
		listeners.add(listener);

		String[] names = context.getResources().getStringArray(R.array.colors);

		int i = 0;
		for (int themeImage: OfflineThemes) 
		{
			String creator = "David Goemans";
			if (i >= 22)
				creator = "L. Baker";

			DigiTheme theme = new DigiTheme(names[i], creator);
			theme.ImageResourceID = themeImage;

			themes.add(theme);
			i++;
			
			theme.Log();
		}
		
		notfiyListeners();
		
		currentThemeIndexForImageDownload = themes.size();
		
		Log.d("DigiClock", "Theme Index: " + currentThemeIndexForImageDownload );
		
		new ListRetrieval(themes).start();

		currentTask = Task.GetOnlineList;
		handler = new Handler();
		handler.postDelayed(timer, 100);
	}
	
	private void notfiyListeners()
	{
		List<DigiTheme> copiedList = new ArrayList<DigiTheme>();
		
		listLock.lock();
		
		for(DigiTheme theme : themes)
		{
			copiedList.add(theme.clone());
		}
		
		listLock.unlock();
		
		for(IThemeListListener listener : listeners)
		{
			listener.listChanged(copiedList);
		}
	}
	
	private void taskFinished()
	{
		
		switch(currentTask)
		{
		case GetImage:
			Log.d("DigiClock", "Image Task Done");
			
			currentThemeIndexForImageDownload++;
			
			if( currentThemeIndexForImageDownload == themes.size() )
				themesPopulated = true;
			
			notfiyListeners();
		
			if(!terminatePending)
			{
				if(!getCurrentImage())
					listeners.clear();
			}
			break;
		case GetOnlineList:
			Log.d("DigiClock", "List Task Done");
			notfiyListeners();
			currentTask = Task.GetImage;
			if(!terminatePending)
			{
				getCurrentImage();
			}
			break;
		}
	}
	
	private boolean getCurrentImage()
	{
		if( currentThemeIndexForImageDownload < themes.size() )
		{
			currentTaskFinished = false;
			
			listLock.lock();
			
			new ImageRetrieval(themes.get(currentThemeIndexForImageDownload), context).start();
			
			listLock.unlock();
			
			handler.postDelayed(timer, 100);
			return true;
		}
		else
		{
			return false;
		}
	}

	private Runnable timer = new Runnable() 
	{

		public void run() 
		{
			if (!currentTaskFinished) 
			{
				//Log.d("DigiClock", "Not done yet");
				handler.postDelayed(this, 50);
			}
			else
			{
				Log.d("DigiClock","Tick");
				taskFinished();
			}
		}
	};
	
	private class ListRetrieval extends Thread
	{
		List<DigiTheme> themes;
		
		public ListRetrieval(List<DigiTheme> themes)
		{
			this.themes = themes;
		}
		
		@Override
		public void run() 
		{
			Log.d("DigiClock", "Getting online list");
			
			List<DigiTheme> onlineThemes = UpdateFunctions.getOnlineThemes();
			
			listLock.lock();
			themes.addAll(onlineThemes);
			currentTaskFinished = true;
			listLock.unlock();
			
			Log.d("DigiClock", "List of online themes retrieved: " + onlineThemes.size());
			
			super.run();
		}
	};
	
	private class ImageRetrieval extends Thread
	{
		DigiTheme theme;
		private Context context;
		
		public ImageRetrieval(DigiTheme theme, Context context)
		{
			this.theme = theme;
			this.context = context;
		}
		
		@Override
		public void run() 
		{
			listLock.lock();
			
			UpdateFunctions.downloadThemeImage(theme, context);
			
			currentTaskFinished = true;
			
			listLock.unlock();
			
			Log.d("DigiClock", "Theme Image retrieved: " + theme.Name);
			
			super.run();
		}
	};

}
