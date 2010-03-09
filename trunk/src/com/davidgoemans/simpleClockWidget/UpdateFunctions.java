package com.davidgoemans.simpleClockWidget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.res.Resources;

public class UpdateFunctions 
{

	static int GetLayoutFromColorId( int colorId )
	{
		// TODO: Read this from XML file or other data storage. Smart huh.
		
		int layout = R.layout.main;
		switch( colorId )
		{
		case 0:
			layout = R.layout.main;
			break;
		case 1:
			layout = R.layout.white;
			break;
		case 2:
			layout = R.layout.velvet;
			break;
		case 3:
			layout = R.layout.pink;
			break;
		case 4:
			layout = R.layout.blue;
			break;
		case 5:
			layout = R.layout.red;
			break;
		case 6:
			layout = R.layout.green;
			break;
		case 7:
			layout = R.layout.ghost;
			break;
		case 8:
			layout = R.layout.dutch;
			break;
		case 9:
			layout = R.layout.orange;
			break;
		case 10:
			layout = R.layout.clear_black;
			break;	
		case 11:
			layout = R.layout.clear_white;
			break;	
		case 12:
			layout = R.layout.yellow;
			break;	
		case 13:
			layout = R.layout.gold;
			break;
		}
		
		return layout;
	}
	
	static String convertToNewDateFormat( String format )
	{
		String outString = format.replaceAll("dow", "EEE");
    	outString = outString.replaceAll("mm", "MM");
    	outString = outString.replaceAll("ms", "MMM");
    	
    	return outString;
	}
	
	static String GetDateWithFormat( String format ) throws IllegalArgumentException
	{
		String outString = convertToNewDateFormat( format );
		SimpleDateFormat frmt = new SimpleDateFormat(outString);
		Date now = new Date();
		return frmt.format(now);
		
		/*
		
		CharSequence[] days = res.getTextArray( R.array.days );
		CharSequence[] months = res.getTextArray( R.array.months );

		Calendar rightNow = Calendar.getInstance();
		
		int doW = rightNow.get(Calendar.DAY_OF_WEEK) - 1;
		int doM = rightNow.get(Calendar.DAY_OF_MONTH);
		int month = rightNow.get(Calendar.MONTH);
		int year = rightNow.get(Calendar.YEAR);

		
		String outString = format.replaceAll("dow", days[doW].toString());
    	outString = outString.replaceAll("dd", String.valueOf(doM));
    	outString = outString.replaceAll("mm", String.valueOf(month+1));
    	outString = outString.replaceAll("ms", months[month].toString());
    	outString = outString.replaceAll("yyyy", String.valueOf(year));
    	outString = outString.replaceAll("yy", String.valueOf(year).substring(2));
    	
    	return outString;*/
	}
}
