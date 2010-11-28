package com.davidgoemans.simpleClockWidget;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GetMoreThemes extends ExpandableListActivity implements IThemeListListener 
{
	ThemeManager manager;
	
	Lock listLock;

	private List<DigiTheme> onlineThemes = null;
	private List<DigiTheme> offlineThemes = null;
	
	private ThemeListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		listLock = new ReentrantLock();
		
		onlineThemes = new ArrayList<DigiTheme>();
		offlineThemes = new ArrayList<DigiTheme>();

		super.onCreate(savedInstanceState);
		
        // Set up our adapter
        adapter = new ThemeListAdapter(this);
        setListAdapter(adapter);
        
        getExpandableListView().expandGroup(0);
        getExpandableListView().expandGroup(1);
        
		manager = new ThemeManager();
		manager.populateThemes(this, this);
	}
	
	List<DigiTheme> getListForGroup(int group)
	{
		switch(group)
		{
		case 0:
			return offlineThemes;
		case 1:
			return onlineThemes;
		default:
			return null;
		}
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) 
	{
		listLock.lock();
		
		List<DigiTheme> themes = getListForGroup(groupPosition);
		DigiTheme theme = themes.get(childPosition);
		
		Toast.makeText(GetMoreThemes.this, 
				"Theme selected: " + theme.Name + " by " + theme.Creator, 
				Toast.LENGTH_SHORT).show();
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		
		// Cleanup when done to save local space
		for( DigiTheme toDelete : onlineThemes )
		{
			if( toDelete.ImageLocation != theme.ImageLocation && toDelete.ImageLocation != prefs.getString("bgPath", null) )
			{
				getApplication().deleteFile(toDelete.Name + ".png");
			}
		}

		if (theme.ImageLocation != null) 
		{
			Log.d("DigiClock", "Got Image: " + theme.ImageLocation);
			ed.putInt("colorId", -1);
			ed.putString("bgPath", theme.ImageLocation);
		}
		else
		{
			Log.d("DigiClock", "Got Layout: " + theme.LayoutResourceID);
			ed.putInt("colorId", childPosition);
			ed.remove("bgPath");
		}

		ed.putBoolean("invalidate", true);

		ed.commit();

		finish();
		
		listLock.unlock();
		
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	
	@Override
	protected void onPause() 
	{
		//this.finish();
		super.onPause();
	}

	@Override
	public void listChanged(List<DigiTheme> themes) 
	{
		listLock.lock();
		
		onlineThemes.clear();
		offlineThemes.clear();
		
		int firstOnlineIndex = 0;
		for (DigiTheme theme : themes) 
		{
			if (theme.URL != null)
			{
				break;
			}
			
			firstOnlineIndex++;
		}

		
		offlineThemes.addAll( themes.subList(0, firstOnlineIndex) );
		
		if( manager.IsPopulatingComplete() )
		{
			Log.d("DigiClock","Populate complete");
			onlineThemes.addAll( themes.subList(firstOnlineIndex, themes.size()) );
		}
		
		listLock.unlock();
		
		getExpandableListView().invalidateViews();
		adapter.notifyDataSetChanged();
		
		//onContentChanged();
	}
	
    public class ThemeListAdapter extends BaseExpandableListAdapter 
    {
        private String[] groups = { "Offline Themes", "Online Themes" };
        Context context;
        
        public ThemeListAdapter(Context context) 
        {
			this.context = context;
		}

        public Object getChild(int groupPosition, int childPosition) 
        {
        	Object toReturn = null;
        	listLock.lock();
        	
        	toReturn = getListForGroup(groupPosition).get(childPosition);
        	
        	listLock.unlock();
        	
        	return toReturn;
        }

        public long getChildId(int groupPosition, int childPosition) 
        {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) 
        {
        	int size = 0;
        	listLock.lock();
        	
        	size = getListForGroup(groupPosition).size();
        	listLock.unlock();
        	
        	return size;
        }

       /*public TextView getGenericView() 
        {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);

            TextView textView = new TextView(GetMoreThemes.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(50, 0, 0, 0);
            return textView;
        }*/

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) 
        {
        	
        	View view = getLayoutInflater().inflate(R.layout.theme_list_item, null);
        	
        	
            listLock.lock();
            
            DigiTheme current = (DigiTheme)getChild(groupPosition, childPosition);
            
            TextView textView = (TextView)view.findViewById(R.id.theme_name);
            textView.setText(current.Name);
            
            textView = (TextView)view.findViewById(R.id.theme_creator);
            textView.setText("by "+ current.Creator);
            
            textView = (TextView)view.findViewById(R.id.theme_price);
            if( current.Paid() )
            {
            	NumberFormat formatter = new DecimalFormat("$0.00");
            	textView.setText(formatter.format(current.Price));
            }
            else
            {
            	textView.setText("FREE");
            }
            
            ImageView imageView = (ImageView)view.findViewById(R.id.theme_image);
            
            if (current.ImageLocation != null) 
			{
				imageView.setImageBitmap(BitmapFactory.decodeFile(current.ImageLocation));
			} 
			else if (current.ImageResourceID != -1) 
			{
				imageView.setImageResource(current.ImageResourceID);
			} 
			else if (current.LayoutResourceID != -1) 
			{
				imageView.setImageResource(R.drawable.widget_bg);
			}
            
            listLock.unlock();
            
            return view;
        }

        public Object getGroup(int groupPosition) 
        {
            return groups[groupPosition];
        }

        public int getGroupCount() 
        {
            return groups.length;
        }

        public long getGroupId(int groupPosition) 
        {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) 
        {
            TextView textView = new TextView(context);
            textView.setPadding(50,10,0,10);
            textView.setTextSize(22);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) 
        {
            return true;
        }

        public boolean hasStableIds() 
        {
            return true;
        }

    }

	/*
	public class ThemeChoiceListener implements OnItemClickListener 
	{
		List<DigiTheme> themes;

		public ThemeChoiceListener(List<DigiTheme> themes) 
		{
			super();
			this.themes = themes;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		{
			listLock.lock();
			
			DigiTheme theme = themes.get(position);

			Toast.makeText(GetMoreThemes.this, "Theme selected: " + theme.Name,
					Toast.LENGTH_SHORT).show();

			SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
			SharedPreferences.Editor ed = prefs.edit();

			if (theme.ImageLocation != null) 
			{
				Log.d("DigiClock", "Got Image: " + theme.ImageLocation);
				ed.putInt("colorId", -1);
				ed.putString("bgPath", theme.ImageLocation);
			} 
			else
			{
				Log.d("DigiClock", "Got Layout: " + theme.LayoutResourceID);
				ed.putInt("colorId", position);
				ed.remove("bgPath");
			}

			ed.putBoolean("invalidate", true);

			ed.commit();

			finish();
			
			listLock.unlock();
		}
	}

	public class ImageAdapter extends BaseAdapter 
	{
		List<DigiTheme> themes;

		public ImageAdapter(Context c, List<DigiTheme> themes) 
		{
			this.themes = themes;
			context = c;
		}

		public int getCount() 
		{
			listLock.lock();
			int size = themes.size();
			listLock.unlock();
			return size;
		}

		public Object getItem(int position) 
		{
			return position;
		}

		public long getItemId(int position) 
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			listLock.lock();
			
			ImageView imageView;
			if (convertView == null) 
			{
				imageView = new ImageView(context);

				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, 100));
				imageView.setAdjustViewBounds(false);
			}
			else 
			{
				imageView = (ImageView) convertView;
			}

			DigiTheme current = themes.get(position);

			if (current.ImageLocation != null) 
			{
				imageView.setImageBitmap(BitmapFactory.decodeFile(current.ImageLocation));
			} 
			else if (current.ImageResourceID != -1) 
			{
				imageView.setImageResource(current.ImageResourceID);
			} 
			else if (current.LayoutResourceID != -1) 
			{
				imageView.setImageResource(R.drawable.widget_bg);
			}

			listLock.unlock();
			
			return imageView;
		}

		private Context context;
	}*/

	/*
	 * static int imageWriterInstances = 0;
	 * 
	 * Lock listLock = null;
	 * 
	 * class ImageWriter extends Thread { DigiTheme theme; GetMoreThemes owner;
	 * 
	 * 
	 * public ImageWriter(String name, DigiTheme theme, GetMoreThemes owner) {
	 * super(name); this.theme = theme; this.owner = owner;
	 * imageWriterInstances++; }
	 * 
	 * public void run() {
	 * 
	 * 
	 * imageWriterInstances--;
	 * 
	 * owner.onThreadCompleted(); } }
	 * 
	 * List<Map<String,Object>> listData = null; List<DigiTheme> themes = null;
	 * List<ImageWriter> threads = null;
	 * 
	 * @Override public void onCreate(Bundle savedInstanceState) { listLock =
	 * new ReentrantLock(); // Start the lock so that no one can mess with the
	 * list from here on listLock.lock();
	 * 
	 * themes = new ArrayList<DigiTheme>();
	 * 
	 * listData = new ArrayList<Map<String,Object>>();
	 * 
	 * threads = new ArrayList<ImageWriter>();
	 * 
	 * try {
	 * 
	 * 
	 * 
	 * ImageWriter writer = new ImageWriter(theme.Name, theme, this);
	 * 
	 * threads.add(writer);
	 * 
	 * listData.add(getMapFromTheme(theme));
	 * 
	 * writer.start();
	 * 
	 * 
	 * stream.close(); conn.disconnect();
	 * 
	 * } catch(Exception e) { Log.d("DigiClock","Exception: " + e.toString()
	 * +" - " + e.getMessage()); }
	 * 
	 * super.onCreate(savedInstanceState);
	 * 
	 * Log.d("DigiClock","blip");
	 * 
	 * SimpleAdapter adapter = new SimpleAdapter(this, listData,
	 * R.layout.theme_list_item, new String[] { "Name", "Creator", "Paid",
	 * "Image" }, new int[] { R.id.theme_name, R.id.theme_creator,
	 * R.id.theme_paid, R.id.theme_image } );
	 * 
	 * 
	 * setListAdapter(adapter);
	 * 
	 * // Break the lock listLock.unlock();
	 * 
	 * handler.postDelayed(updateTimeTask, 100); }
	 * 
	 * @Override protected void onListItemClick(ListView l, View v, int
	 * position, long id) { SharedPreferences prefs =
	 * getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
	 * SharedPreferences.Editor ed = prefs.edit();
	 * 
	 * ed.putInt("colorId", -1); ed.putString("bgPath",
	 * themes.get(position).ImageLocation ); ed.putBoolean("invalidate", true);
	 * 
	 * Log.d("DigiClock", "Click item " + position + " with image: " +
	 * themes.get(position).ImageLocation);
	 * 
	 * ed.commit();
	 * 
	 * super.onListItemClick(l, v, position, id);
	 * 
	 * finish(); }
	 * 
	 * protected void onThreadCompleted() { listLock.lock();
	 * 
	 * if( imageWriterInstances == 0 ) { for(Map<String,Object> map : listData)
	 * { map.put("Image", this.getFilesDir() + "/" + map.get("Name") + ".png");
	 * Log.d("DigiClock", "Put " + map.get("Name") + ".png in map"); } }
	 * 
	 * listLock.unlock(); }
	 * 
	 * private Handler handler = new Handler();
	 * 
	 * private Runnable updateTimeTask = new Runnable() { public void run() {
	 * listLock.lock(); Log.d("DigiClock","Handler: Tick"); onContentChanged();
	 * 
	 * if( imageWriterInstances != 0 ) { handler.postDelayed(this, 100); }
	 * 
	 * listLock.unlock(); } };
	 * 
	 * 
	 * Map<String, Object> getMapFromTheme(DigiTheme theme) { Map<String,Object>
	 * map = new HashMap<String,Object>();
	 * 
	 * map.put("Name",theme.Name); map.put("Creator",theme.Creator);
	 * map.put("Paid",theme.Paid); map.put("URL",theme.URL); map.put("Image",
	 * R.drawable.blank); //this.getFilesDir() + "/" + theme.Name + ".png");
	 * 
	 * return map; }
	 */

}
