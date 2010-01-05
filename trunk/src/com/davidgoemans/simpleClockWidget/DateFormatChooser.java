package com.davidgoemans.simpleClockWidget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DateFormatChooser extends Activity
{
	TextView m_outputDate;
	EditText m_inputDate;
	String m_format;
	
	public static final String DefaultFormat = new String("dow, dd ms yyyy"); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dateformatchooser);

		// Mini Button Panel!
		Button b = (Button)findViewById(R.id.bUpdate);
		b.setOnClickListener(m_updateView);
		
		b = (Button)findViewById(R.id.bSave);
		b.setOnClickListener(m_saveView);
		
		b = (Button)findViewById(R.id.bReset);
		b.setOnClickListener(m_resetView);
		
		// Helper Buttons
		b = (Button)findViewById(R.id.bDOW);
		b.setOnClickListener(m_helperKey);
		b = (Button)findViewById(R.id.bDD);
		b.setOnClickListener(m_helperKey);
		
		b = (Button)findViewById(R.id.bMS);
		b.setOnClickListener(m_helperKey);
		b = (Button)findViewById(R.id.bMM);
		b.setOnClickListener(m_helperKey);
		
		b = (Button)findViewById(R.id.bYY);
		b.setOnClickListener(m_helperKey);
		b = (Button)findViewById(R.id.bYYYY);
		b.setOnClickListener(m_helperKey);
		
		b = (Button)findViewById(R.id.bSpace);
		b.setOnClickListener(m_helperKey);
		b = (Button)findViewById(R.id.bMinus);
		b.setOnClickListener(m_helperKey);
		b = (Button)findViewById(R.id.bSlash);
		b.setOnClickListener(m_helperKey);
		b = (Button)findViewById(R.id.bComma);
		b.setOnClickListener(m_helperKey);
		
		m_outputDate = (TextView)findViewById(R.id.DateFormatOut);
		
		m_inputDate = (EditText)findViewById(R.id.DateFormatIn);
		m_inputDate.setOnKeyListener(m_typeListener);
		
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		m_format = prefs.getString("dateFormat", DefaultFormat); 
		
		updateView(m_format);
	}
	
	private OnKeyListener m_typeListener = new OnKeyListener() 
	{

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) 
		{
			if( event.getAction() == KeyEvent.ACTION_UP )
			{
				m_format = cleanupInput(m_inputDate.getText().toString());
				updateView(m_format);
			}

			return false;
		}
	};
	
	private OnClickListener m_updateView = new OnClickListener() 
	{
	    public void onClick(View v) 
	    {
	    	m_format = cleanupInput(m_inputDate.getText().toString());
	    	updateView(m_format);
	    }
	};
	
	private OnClickListener m_resetView = new OnClickListener() 
	{
	    public void onClick(View v) 
	    {
	    	m_format = DefaultFormat;
	    	updateView(m_format);
	    }
	};
	
	private OnClickListener m_saveView = new OnClickListener() 
	{
	    public void onClick(View v) 
	    {
	    	m_format = cleanupInput(m_inputDate.getText().toString());
	    	updateView(m_format);
	    	
	    	SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
			SharedPreferences.Editor ed = prefs.edit();
			ed.putString("dateFormat", m_format );
			ed.commit();
	    }
	};
	
	private OnClickListener m_helperKey = new OnClickListener() 
	{	
		@Override
		public void onClick(View v) 
		{
			m_format = cleanupInput(m_inputDate.getText().toString());

			switch( v.getId() )
			{
			case R.id.bDOW:
				m_format = m_format.concat("dow");
				break;
			case R.id.bDD:
				m_format = m_format.concat("dd");
				break;
			case R.id.bMS:
				m_format = m_format.concat("ms");
				break;
			case R.id.bMM:
				m_format = m_format.concat("mm");
				break;
			case R.id.bYY:
				m_format = m_format.concat("yy");
				break;
			case R.id.bYYYY:
				m_format = m_format.concat("yyyy");
				break;
			case R.id.bSpace:
				m_format = m_format.concat(" ");
				break;
			case R.id.bMinus:
				m_format = m_format.concat("-");
				break;
			case R.id.bSlash:
				m_format = m_format.concat("/");
				break;
			case R.id.bComma:
				m_format = m_format.concat(",");
				break;
			}
			
			updateView(m_format);
			
		}
	};
	
	private String generateSampleOutputFromFormat(String format)
	{
		String outString = format.replaceAll("dow", "Sat");
    	outString = outString.replaceAll("dd", "31");
    	outString = outString.replaceAll("mm", "12");
    	outString = outString.replaceAll("ms", "Dec");
    	outString = outString.replaceAll("yyyy", "2009");
    	outString = outString.replaceAll("yy", "09");
		
		return outString;
	}
	
	private String cleanupInput(String input)
	{
		String format = input.replaceAll("MM", "mm");
		
		format = format.replaceAll("DOW", "dow");
		format = format.replaceAll("YY", "yy");
		format = format.replaceAll("MS", "ms");
		format = format.replaceAll("DD", "dd");
		
		format = format.replaceAll("Dow", "dow");
		format = format.replaceAll("DoW", "dow");
		format = format.replaceAll("Yy", "yy");
		format = format.replaceAll("Ms", "ms");
		format = format.replaceAll("Dd", "dd");
		
		return format;
	}
	
	private void updateView(String format)
	{
    	String outString = generateSampleOutputFromFormat(format);
    	m_outputDate.setText(outString);
    	
    	int cursorPos = m_inputDate.getSelectionStart();
    	m_inputDate.setText(format);
    	m_inputDate.setSelection(cursorPos);
	}
}
