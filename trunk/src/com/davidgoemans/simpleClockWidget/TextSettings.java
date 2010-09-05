package com.davidgoemans.simpleClockWidget;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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
	
	String[] typeFaces = 
	{
		"normal",
		"sans",
		"serif",
		"monospace"
	};
	
	
	private int m_textColor = 0;
	private boolean m_leadingZero = true;
	private boolean m_dateEnabled = true;
	private String m_typeface = "normal";
	
	private float m_timeSize = 52;
	private float m_dateSize = 14;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.textsettings);

		SeekBar sb = (SeekBar)findViewById(R.id.sbColor);
		sb.setOnSeekBarChangeListener(m_colorPicked);
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		m_textColor = prefs.getInt("textColor", 0);
		
		if( m_textColor != 0 )
		{
			for( int i=0; i<colArray.length; i++ )
			{
				if( colArray[i] == m_textColor )
				{
					sb.setProgress(i);
					break;
				}
			}
		}
		
		
		m_leadingZero = prefs.getBoolean("leadingZero", true);
		
		ToggleButton tb = (ToggleButton)findViewById(R.id.tbLeadingZero);
		tb.setChecked(m_leadingZero);
		
		m_dateEnabled = prefs.getBoolean("dateEnabled", true);
		
		tb = (ToggleButton)findViewById(R.id.tbDateEnabled);
		tb.setChecked(m_dateEnabled);
		
		// Typeface Spinner		
		m_typeface = prefs.getString("typeface", typeFaces[0]);
		
		Spinner typeface = (Spinner) findViewById(R.id.sTypeface);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.typefaces, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    typeface.setAdapter(adapter);
	    
	    for( int i=0; i<typeFaces.length; i++ )
		{
	    	if( typeFaces[i].equalsIgnoreCase(m_typeface) )
	    	{
	    		typeface.setSelection(i);
	    		break;
	    	}
		}
	    
	    TextView title = (TextView) findViewById(R.id.lTypeface);
		title.setText(R.string.text_typeface);
	    
	    int theme = prefs.getInt("colorId", 0);
		// For legacy themes, disable the spinner
		if( theme < 15 && theme != 11 )
		{
			title.setText(R.string.text_fonterror);
			typeface.setEnabled(false);
		}
		
		
		// Font size numerical selects
		
		m_timeSize = prefs.getFloat("textTimeSize", 52);
		m_dateSize = prefs.getFloat("textDateSize", 14);
		
		sb = (SeekBar)findViewById(R.id.sbTimeSize);
		sb.setOnSeekBarChangeListener(m_timeSizePicked);
		sb.setProgress((int)m_timeSize);
		
		sb = (SeekBar)findViewById(R.id.sbDateSize);
		sb.setOnSeekBarChangeListener(m_dateSizePicked);
		sb.setProgress((int)m_dateSize);
	}
	
	@Override
	protected void onPause() 
	{
		ToggleButton leadZero = (ToggleButton)findViewById(R.id.tbLeadingZero);
		ToggleButton dateEn = (ToggleButton)findViewById(R.id.tbDateEnabled);
		
		Spinner typeface = (Spinner)findViewById(R.id.sTypeface);
		m_typeface = typeFaces[typeface.getSelectedItemPosition()];
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		
		ed.putInt("textColor", m_textColor );
		ed.putBoolean("leadingZero", leadZero.isChecked());
		ed.putBoolean("dateEnabled", dateEn.isChecked());
		ed.putString("typeface", m_typeface);
		
		ed.putFloat("textTimeSize", m_timeSize);
		ed.putFloat("textDateSize", m_dateSize);
		
		ed.putBoolean("invalidate", true);
		
		
		ed.commit();

		this.startActivity(new Intent(this, SettingsList.class));
		
		super.onPause();
		
		this.finish();
	}
	
	OnSeekBarChangeListener m_colorPicked = new OnSeekBarChangeListener()
	{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) 
		{
			TextView tmp = (TextView)findViewById(R.id.lColor);
			tmp.setBackgroundColor(colArray[progress]);
			seekBar.setBackgroundColor(colArray[progress]);
			Log.d("DigiClock", "New Color: " + String.valueOf(progress));
			
			m_textColor = colArray[progress];
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
