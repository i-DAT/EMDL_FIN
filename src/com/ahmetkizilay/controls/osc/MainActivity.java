package com.ahmetkizilay.controls.osc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;



import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TextView textView;
	TextView textView1;
	
    public static String ipAddress = "10.10.50.70";
    public static int port = 8000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.port);
		textView1 = (TextView) findViewById(R.id.ip);
		
		
		
	}


	
	public void sendIntent(View v){
		
		
  
		ipAddress = textView1.getText().toString();
		port = Integer.parseInt(textView.getText().toString());
		
		Intent intent = new Intent();
		intent.setAction(SecondActivity.ACTION_START);
		startActivity(intent);
	}
	
	

}
