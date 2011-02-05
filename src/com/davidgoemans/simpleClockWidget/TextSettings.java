package com.davidgoemans.simpleClockWidget;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.*;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class TextSettings extends Activity 
{
	int[] colArray = 
	{
		Color.BLACK,
		Color.WHITE,
		Color.BLUE,
		Color.CYAN,
		
		Color.DKGRAY,
		Color.GRAY,
		Color.GREEN,
		Color.LTGRAY,
		
		Color.MAGENTA,
		Color.RED,
		Color.YELLOW
	};

	public static class FontInfo
	{
		public String Name;
		public String Type;
		public String Location;

		public static List<FontInfo> FromJsonFile(String data)
		{
			List<FontInfo> infoList = new ArrayList<FontInfo>();
			
			try 
			{
				JSONTokener tokener = new JSONTokener(data);
				
				while(tokener.more())
		    	{
		    		JSONObject cur;
					
					cur = (JSONObject)tokener.nextValue();
	
		    		FontInfo font = new FontInfo();
		    		font.Name = cur.getString("name");
		    		font.Type = cur.getString("type");
		    		font.Location = cur.getString("location");
	
		    		infoList.add(font);
		    	}
			}
			catch(Exception e)
			{
				Log.d("DigiClock", "Couldn't load fonts: " + e.getMessage());
			}
			
			return infoList;
			
		}
	}
	
	private int m_textColor = 0;
	private String m_typeface = "normal";
	
	private float m_timeSize = 52;
	private float m_dateSize = 14;

	List<FontInfo> m_fontInfoList = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.textsettings);
		
		try
		{
			String data = UpdateFunctions.GetDataFromStream(getAssets().open("fonts.json"));
			m_fontInfoList = FontInfo.FromJsonFile(data);
		}
		catch(IOException e)
		{
			Log.d("DigiClock","IO Exception reading fonts: " + e.getLocalizedMessage());
		}

		SeekBar sb;
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		m_textColor = prefs.getInt("textColor", Color.WHITE);
		
		Button button = (Button)findViewById(R.id.bColor);
		button.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) 
		    {
		      showTextColorPicker(v);
		    }
		  });
		
		updateTextColor();
		
		ToggleButton tb = (ToggleButton)findViewById(R.id.tbLeadingZero);
		tb.setChecked(prefs.getBoolean("leadingZero", true));
		
		tb = (ToggleButton)findViewById(R.id.tbDateEnabled);
		tb.setChecked(prefs.getBoolean("dateEnabled", true));
		
		tb = (ToggleButton)findViewById(R.id.tb24Hour);
		tb.setChecked(!prefs.getBoolean("twelvehour", true));	
		
		tb = (ToggleButton)findViewById(R.id.tbShadowEnabled);
		tb.setChecked(prefs.getBoolean("shadowEnabled", true));
		
		tb = (ToggleButton)findViewById(R.id.tbMarkerEnabled);
		tb.setChecked(prefs.getBoolean("amPmMarkerEnabled", true));
		
		// Typeface Spinner		
		m_typeface = prefs.getString("typeface", m_fontInfoList.get(0).Location);
		
		String[] fontNames = new String[m_fontInfoList.size()];
		
		for(int i=0; i<m_fontInfoList.size(); i++)
		{
			fontNames[i] = m_fontInfoList.get(i).Name;
		}
		
		ArrayAdapter<CharSequence> adapter = 
			new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, fontNames);
		
		Spinner typeface = (Spinner) findViewById(R.id.sTypeface);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    typeface.setAdapter(adapter);
	    
	    for( int i=0; i<m_fontInfoList.size(); i++ )
		{
	    	if( m_fontInfoList.get(i).Location.equalsIgnoreCase(m_typeface) )
	    	{
	    		typeface.setSelection(i);
	    		break;
	    	}
		}
	    
	    TextView title = (TextView) findViewById(R.id.lTypeface);
		title.setText(R.string.text_typeface);
	    
		// Font size numerical selects
		m_timeSize = prefs.getFloat("textTimeSize", 52);
		m_dateSize = prefs.getFloat("textDateSize", 14);
		
		sb = (SeekBar)findViewById(R.id.sbTimeSize);
		sb.setOnSeekBarChangeListener(m_timeSizePicked);
		sb.setProgress((int)m_timeSize);
		
		sb = (SeekBar)findViewById(R.id.sbDateSize);
		sb.setOnSeekBarChangeListener(m_dateSizePicked);
		sb.setProgress((int)m_dateSize);
		
		sb = (SeekBar)findViewById(R.id.sbBgAlpha);
		sb.setProgress(prefs.getInt("bgAlpha", 255));
		
		sb = (SeekBar)findViewById(R.id.sbTextAlpha);
		sb.setProgress(prefs.getInt("textAlpha", 255));
	}
	
	void updateTextColor()
	{
		Button toChange = (Button)findViewById(R.id.bColor);
		toChange.setTextColor(m_textColor);
	}
	
	public void showTextColorPicker(View view)
	{
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, m_textColor, new OnAmbilWarnaListener() {
	        @Override
	        public void onOk(AmbilWarnaDialog dialog, int color) 
	        {	        	
	    		m_textColor = color;
	    		updateTextColor();
	        }
	                
	        @Override
	        public void onCancel(AmbilWarnaDialog dialog) 
	        {
	        }
		});

		dialog.show();
		
	}

	@Override
	protected void onPause() 
	{
		ToggleButton leadZero = (ToggleButton)findViewById(R.id.tbLeadingZero);
		ToggleButton dateEn = (ToggleButton)findViewById(R.id.tbDateEnabled);
		ToggleButton shadowEn = (ToggleButton)findViewById(R.id.tbShadowEnabled);
		ToggleButton twentyFourEn = (ToggleButton)findViewById(R.id.tb24Hour);
		ToggleButton marker = (ToggleButton)findViewById(R.id.tbMarkerEnabled);
		SeekBar bgAlpha = (SeekBar)findViewById(R.id.sbBgAlpha);
		SeekBar textAlpha = (SeekBar)findViewById(R.id.sbTextAlpha);
		
		Spinner typeface = (Spinner)findViewById(R.id.sTypeface);
		m_typeface = m_fontInfoList.get(typeface.getSelectedItemPosition()).Location;
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		
		ed.putInt("textColor", m_textColor );
		
		ed.putBoolean("leadingZero", leadZero.isChecked());
		ed.putBoolean("dateEnabled", dateEn.isChecked());
		ed.putBoolean("twelvehour", !twentyFourEn.isChecked());
		ed.putBoolean("shadowEnabled", shadowEn.isChecked());
		ed.putBoolean("amPmMarkerEnabled", marker.isChecked());
		
		ed.putString("typeface", m_typeface);
		
		ed.putFloat("textTimeSize", m_timeSize);
		ed.putFloat("textDateSize", m_dateSize);
		
		ed.putInt("bgAlpha", bgAlpha.getProgress());
		ed.putInt("textAlpha", textAlpha.getProgress());
		
		ed.putBoolean("invalidate", true);
		
		
		ed.commit();

		this.startActivity(new Intent(this, SettingsList.class));
		
		super.onPause();
		
		this.finish();
	}
	
	static final float MAX_TEXT_SIZE = 80;
	
	OnSeekBarChangeListener m_timeSizePicked = new OnSeekBarChangeListener()
	{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) 
		{
			Log.d("DigiClock", "Time size: " + String.valueOf(progress));
			m_timeSize = progress;
			
			if( m_dateSize + m_timeSize > MAX_TEXT_SIZE )
			{
				SeekBar sb = (SeekBar)findViewById(R.id.sbDateSize);
				sb.setProgress((int) (MAX_TEXT_SIZE - m_timeSize));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) 
		{
			// DO NOTHING
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) 
		{
			// DO NOTHING
		}
	};
	
	OnSeekBarChangeListener m_dateSizePicked = new OnSeekBarChangeListener()
	{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) 
		{
			Log.d("DigiClock", "Date size: " + String.valueOf(progress));
			m_dateSize = progress;
			
			if( m_dateSize + m_timeSize > MAX_TEXT_SIZE )
			{
				SeekBar sb = (SeekBar)findViewById(R.id.sbTimeSize);
				sb.setProgress((int) (MAX_TEXT_SIZE - m_dateSize));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) 
		{
			// DO NOTHING
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) 
		{
			// DO NOTHING
		}
	};
}
