package com.davidgoemans.simpleClockWidget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
	
	private int m_textColor = -1;
	private boolean m_leadingZero = true;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.textsettings);

		SeekBar sb = (SeekBar)findViewById(R.id.sbColor);
		sb.setOnSeekBarChangeListener(m_colorPicked);
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		m_textColor = prefs.getInt("textColor", -1);
		
		if( m_textColor != -1 )
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
	}
	
	@Override
	protected void onPause() 
	{
		ToggleButton tb = (ToggleButton)findViewById(R.id.tbLeadingZero);
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		
		ed.putInt("textColor", m_textColor );
		ed.putBoolean("leadingZero", tb.isChecked());
		ed.putBoolean("invalidate", true);
		ed.commit();

		super.onPause();
	};
	
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
}
