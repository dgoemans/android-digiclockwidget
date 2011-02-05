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
		
        if (theme.ImageLocation == null
        	&& theme.ImageResourceID == -1) 
		{
			Toast.makeText(GetMoreThemes.this, 
					"Please wait for the image to download", Toast.LENGTH_LONG).show();
			listLock.unlock();
			
			return super.onChildClick(parent, v, groupPosition, childPosition, id);
		}
		
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

		manager.terminate();
		
		finish();
		
		listLock.unlock();
		
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	
	@Override
	protected void onPause() 
	{
		super.onPause();
	}
	
	@Override
	public void finish() 
	{
		manager.terminate();
		super.finish();
	}

	int firstOnlineIndex = 0;
	
	@Override
	public void listChanged(List<DigiTheme> themes) 
	{
		listLock.lock();
				
		if( firstOnlineIndex == 0 )
		{
			for (DigiTheme theme : themes) 
			{
				if (theme.URL != null)
				{
					break;
				}
				
				firstOnlineIndex++;
			}
		}
		
		if( offlineThemes.size() == 0 )
		{		
			offlineThemes.addAll( themes.subList(0, firstOnlineIndex) );
		}
		
		onlineThemes.clear();
		onlineThemes.addAll( themes.subList(firstOnlineIndex, themes.size()) );
		
		listLock.unlock();
		
		getExpandableListView().invalidateViews();
		adapter.notifyDataSetChanged();
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

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) 
        {
        	View view = convertView;
        	if( convertView == null )
        	{
        		view = getLayoutInflater().inflate(R.layout.theme_list_item, null);
        	}

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
			else
			{
				imageView.setImageResource(R.drawable.blank);
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
}
