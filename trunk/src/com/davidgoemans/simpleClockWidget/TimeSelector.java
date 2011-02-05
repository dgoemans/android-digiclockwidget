package com.davidgoemans.simpleClockWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class TimeSelector extends Activity implements OnCheckedChangeListener
{
	List<RadioButton> buttons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		setContentView(R.layout.toggle_menu);
		RadioGroup menu = (RadioGroup) findViewById(R.id.radio_group);
		menu.setOnCheckedChangeListener(this);
		
		CharSequence[] choices = getResources().getTextArray(R.array.time_options);
		LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
		
		buttons = new ArrayList<RadioButton>();
		
		for(CharSequence choice : choices)
		{
			RadioButton button = new RadioButton(this);
			button.setText(choice);
			buttons.add(button);
			menu.addView(button, layoutParams);
		}
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) 
	{
		SharedPreferences prefs = getSharedPreferences(SimpleClockWidget.PREFS_NAME, 0);
		Editor ed = prefs.edit();
		
		int selected = -1;
		for( int i=0; i<group.getChildCount(); i++ )
		{
			if( group.getChildAt(i).getId() == checkedId )
			{
				selected = i;
				break;
			}
		}
		
		switch(selected)
		{
		case 0:
			ed.putBoolean("twelvehour", true);
			ed.putBoolean("leadingZero", false);
			break;
		case 1:
			ed.putBoolean("twelvehour", false);
			ed.putBoolean("leadingZero", true);
			break;
		default:
			Log.d("DigiClock", "Invalid selection, defaulting");
		}

		ed.putBoolean("invalidate", true);
		ed.commit();

		this.finish();
	}

}
