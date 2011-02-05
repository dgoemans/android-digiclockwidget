package com.davidgoemans.simpleClockWidget;

public class DigiTheme
{
	public DigiTheme(String Name, String Creator)
	{
		this.Name = Name;
		this.Creator = Creator;		

		this.URL = null;
		this.Price = 0;
		
		this.ImageResourceID = -1;
		this.ImageLocation = null;
	}
	
	public DigiTheme(String Name, String Creator, String URL, float Price)
	{
		this(Name, Creator);
		this.URL = URL;
		this.Price = Price;
	}
	
	public DigiTheme clone()
	{
		DigiTheme clone = new DigiTheme(Name,Creator,URL,Price);
		clone.ImageResourceID = ImageResourceID;
		clone.ImageLocation = ImageLocation;
		
		return clone;
	}
	
	public void Log()
	{
		android.util.Log.d("DigiClock", "Theme: " + Name 
				+ " by: " + Creator );
	}
	
	public String Name;
	public String Creator;
	public String URL;
	public float Price;
	
	public boolean Paid()
	{ 
		return Price > 0; 
	}
	
	public String ImageLocation;
	public int LayoutResourceID;
	public int ImageResourceID;
}
