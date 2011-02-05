package com.davidgoemans.simpleClockWidget;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class News extends Activity 
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{	    
	    super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_PROGRESS);
	    
	    setContentView(R.layout.simple_webview);

	    WebView webview = (WebView) findViewById(R.id.webView);
	    
	    webview.setWebChromeClient(new WebChromeClient() 
	    {
    	   public void onProgressChanged(WebView view, int progress) 
    	   {
    	     // Activities and WebViews measure progress with different scales.
    	     // The progress meter will automatically disappear when we reach 100%
    	     News.this.setProgress(progress * 1000);
    	   }
    	 });
	    
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.loadUrl("http://www.davidgoemans.com/DigiClock/upgrade.html");
	}
	
	@Override
	protected void onPause() 
	{
		this.startActivity(new Intent(this, SettingsList.class));
		
		super.onPause();
		
		this.finish();
	}
}
