package com.davidgoemans.simpleClockWidget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DateFormatChooser extends Activity
{
	TextView m_outputDate;
	EditText m_inputDate;
	String m_format;
	
	public static final String DefaultFormat = new String("E, dd MMM yyyy"); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dateformatchooser);

		Button b = (Button)findViewById(R.id.btnReset);
		b.setOnClickListener(m_resetView);
		
		b = (Button)findViewById(R.id.btnPreview);
		b.setOnClickListener(m_previewView);
		
		m_outputDate = (TextView)findViewById(R.id.outDate);
		m_inputDate = (EditText)findViewById(R.id.inDate);
		
		//m_inputDate.setOnKeyListener(m_typeListener);
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		m_format = prefs.getString("dateFormat", DefaultFormat); 
		
		m_format = UpdateFunctions.convertToNewDateFormat(m_format);
		
		updateView(m_format);
	}
	
	/*private OnKeyListener m_typeListener = new OnKeyListener() 
	{

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) 
		{
			if( event.getAction() == KeyEvent.ACTION_UP )
			{
				if( m_inputDate.getText().toString().length() > 0 )
				{
					if( updateView(m_inputDate.getText().toString()) )
					{
						m_format = m_inputDate.getText().toString();
					}
					else
					{
						m_inputDate.setText( m_format );
					}
				}
			}

			return false;
		}
	};*/
	
	private OnClickListener m_resetView = new OnClickListener() 
	{
	    public void onClick(View v) 
	    {
	    	if( m_format != null && !m_format.equalsIgnoreCase(DefaultFormat) )
	    	{
	    		if( updateView(DefaultFormat) )
	    		{
	    			m_format = DefaultFormat;
	    		}
	    	}
	    }
	};
	
	private OnClickListener m_previewView = new OnClickListener() 
	{
	    public void onClick(View v) 
	    {
	    	if( m_inputDate.getText().toString().length() > 0 )
			{
				if( updateView(m_inputDate.getText().toString()) )
				{
					m_format = m_inputDate.getText().toString();
				}
				else
				{
					m_inputDate.setText( m_format );
				}
			}
	    }
	};
	
	@Override
	protected void onPause() 
	{
		if( updateView(m_inputDate.getText().toString()) )
		{
			m_format = m_inputDate.getText().toString();
		}
		
		if(updateView(m_format))
		{
			SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
			SharedPreferences.Editor ed = prefs.edit();
			ed.putString("dateFormat", m_format );
			ed.putBoolean("invalidate", true);
			ed.commit();
		}
		
		super.onPause();
	}

	private boolean updateView(String format)
	{
		String outString;
		try
		{
			outString = UpdateFunctions.GetDateWithFormat(format);
		}
		catch(IllegalArgumentException e)
		{
			Log.d("DigiClock","Date formatting failed");
			return false;
		}
		
    	m_outputDate.setText(outString);
    	
    	int cursorPos = m_inputDate.getSelectionStart();
    	cursorPos = Math.max(cursorPos, 0);
    	cursorPos = Math.min(cursorPos, format.length());
    	
    	m_inputDate.setText(format);
   		m_inputDate.setSelection(cursorPos);
    	
    	return true;
	}
}
