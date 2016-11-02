package com.ahmetkizilay.controls.osc;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import android.view.View.OnClickListener;
import com.ahmetkizilay.controls.osc.R;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class SecondActivity extends Activity implements SensorEventListener {
	

	
	
	
	Intent intent;
	public static String ACTION_START = "com.ahmetkizilay.controls.osc.startsecondactivity";
	
	private static final int REQUEST_ENABLE_BT = 1;
	
	Uri sharePath;
	File sampleDir;
	
	private ImageView image;
	
	Uri newUri;
	
	private float currentDegree = 0f;
	float degree;
	
	TextView myTextView;
	TextView accelVals;
	TextView accelVals2;
	
	String tom="hello";
	Object args[] = new Object [4];
	public float locked=0;
	
	
	 BluetoothAdapter bluetoothAdapter;

	MediaRecorder recorder;
    MediaPlayer player = new MediaPlayer();

    File audiofile = null;
    private static final String TAG = "SoundRecordingActivity";
    private View startButton;
    private View stopButton;
	
 
    
    
	 private SensorManager mSensorManager;
	 
	 RelativeLayout mScreen;
	 
	 String serialnum;
	 
	int lee=0;
	
	String col;
	
	public static float x = 0;
	public static  float y = 0;
	public static float z = 0;
	
	int r;
	int g;
	int b;
	
	ImageView up;
	ImageView down;
	ImageView left;
	ImageView right;

    private final static int NETWORK_DIALOG = 2;
    private final static int WIFI_ALERT_DIALOG = 3;
    
    private final static String NETWORK_SETTINGS_FILE = "qosc_network.cfg";
    private final static String OSC_SETTINGS_FILE = "qosc_osc.cfg";
            
    

    private Hashtable<String, String> oscSettingsHashtable = new Hashtable<String, String>();
    
    private boolean editMode = false;

    private String ipAddress = "10.10.50.70";
    private int port = 8000;
    private OSCPortOut oscPortOut = null;    
    
	TextView debugTextView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        
        try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
		serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
			
			

		} catch (Exception ignored) {

		}
        
        //mScreen.setBackgroundColor(Color.parseColor("#FF8300"));
        
        accelVals = (TextView)findViewById(R.id.accelVal);   
        accelVals.setTextColor(Color.BLACK);
        
        accelVals2 = (TextView)findViewById(R.id.accelVals2);   
        accelVals2.setTextColor(Color.WHITE);
        
        Button button = (Button) findViewById(R.id.button1);
        

        image = (ImageView) findViewById(R.id.imageViewCompass);

        
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        col = serialnum.substring(0,6);
        
        int color = Integer.parseInt(col, 16);
         r = (color >> 16) & 0xFF;
         g = (color >> 8) & 0xFF;
         b = (color >> 0) & 0xFF;
         
         mScreen = (RelativeLayout) findViewById(R.id.myScreen);
         //mScreen.setBackgroundColor(Color.parseColor("#"+col));
         mScreen.setBackgroundColor(Color.parseColor("#FF8300"));
       
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
		restoreNetworkSettingsFromFile();
		restoreOSCSettingsFromFile();
        initializeOSC();
        

        checkWifiState();
        

        
        
        button.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    
                	locked=1;
                    mScreen.setBackgroundColor(Color.parseColor("#000000"));
            
                  // v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                   accelVals2.setText("LOCKED"); 
                   accelVals.setTextColor(Color.parseColor("#FF8300"));
                 
                 //v.vibrate(200);
                	
                	
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    
                	locked=0;
  	              //mScreen.setBackgroundColor(Color.parseColor("#"+col));
  	              mScreen.setBackgroundColor(Color.parseColor("#FF8300"));
  	             accelVals.setTextColor(Color.BLACK);
  	              accelVals2.setText(""+Math.round(x)); 
                	
                }
                return true;
            }
        });

    }
    
    
    
    @Override 
	   public boolean onKeyDown(int keyCode, KeyEvent event) {

	       switch (keyCode) {
	           case KeyEvent.KEYCODE_ENTER:
	           {
	              locked=1;
	              mScreen.setBackgroundColor(Color.parseColor("#000000"));
	      
	            // v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	             accelVals2.setText("LOCKED"); 
	             accelVals.setTextColor(Color.parseColor("#FF8300"));
	           
	           //v.vibrate(200);
	               return true;
	           }
	           
	           
	       }
	       return super.onKeyDown(keyCode, event);
	       
	       
	   }
	   
	   @Override 
	   public boolean onKeyUp(int keyCode, KeyEvent event) {

	       switch (keyCode) {
	           case KeyEvent.KEYCODE_ENTER:
	           {
	              locked=0;
	              //mScreen.setBackgroundColor(Color.parseColor("#"+col));
	              mScreen.setBackgroundColor(Color.parseColor("#FF8300"));
	             accelVals.setTextColor(Color.BLACK);
	              accelVals2.setText(""+Math.round(x)); 
	  
	             
	               return true;
	           }
	       }
	       return super.onKeyDown(keyCode, event);
	   }

    
    
    
	@Override
	protected void onResume() {
		super.onResume();

		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_UI);
		
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
		


	
	}
	
	
    

    
    private void initializeOSC() {
    	try {
    		
    		if(oscPortOut != null) {
    			oscPortOut.close();
    		}
    		
    		oscPortOut = new OSCPortOut(InetAddress.getByName(MainActivity.ipAddress), MainActivity.port);
       	}
    	catch(Exception exp) {
    		Toast.makeText(this, "Error Initializing OSC", Toast.LENGTH_SHORT).show();
    		oscPortOut = null;
    	}
    }
    
    
    
    
    
///////////////////////////////////
    
    public void sendOSC(String message) {    	
    	
    	try {    		
	    	new AsyncSendOSCTask(this, this.oscPortOut).execute(new OSCMessage(message));	    	
	    	

    	
    	}
    	catch(Exception exp) {
    		Toast.makeText(this, "Error Sending Message", Toast.LENGTH_SHORT).show();
    	}
    }
    
   
  /* public void sendOSC(String address, Object[] arguments[]) {   
    	try {    		
	    	new AsyncSendOSCTask(this, this.oscPortOut).execute(new OSCMessage(address, arguments));	    	
    	}
    	catch(Exception exp) {
    		Toast.makeText(this, "Error Sending Message", Toast.LENGTH_SHORT).show();
    	}
    }*/
    
    public void bob(){
    	


    	//OSCMessage msg = new OSCMessage(serialnum, args);
    	
    	
    		//new AsyncSendOSCTask(this, this.oscPortOut).execute(msg);
 
    	
    	
    	
    }
    
    
////////////////////////////////////
    
    
    
    
    
    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
	        getAccelerometer(event);
	        
	        
/* degree = Math.round(event.values[0]);
	        
	        RotateAnimation ra = new RotateAnimation(
					currentDegree, 
					-degree,
					Animation.RELATIVE_TO_SELF, 0.5f, 
					Animation.RELATIVE_TO_SELF,
					0.5f);
			
			// how long the animation will take place
			ra.setDuration(210);

			// set the animation after the end of the reservation status
			ra.setFillAfter(true);

			// Start the animation
			image.startAnimation(ra);
			currentDegree = -degree;

	        */
	      }  
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	        getAccelerometer2(event);
	        
	        
/* degree = Math.round(event.values[0]);
	        
	        RotateAnimation ra = new RotateAnimation(
					currentDegree, 
					-degree,
					Animation.RELATIVE_TO_SELF, 0.5f, 
					Animation.RELATIVE_TO_SELF,
					0.5f);
			
			// how long the animation will take place
			ra.setDuration(210);

			// set the animation after the end of the reservation status
			ra.setFillAfter(true);

			// Start the animation
			image.startAnimation(ra);
			currentDegree = -degree;

	        */
	      }  
	        
	        
	        
	      }
		
		
		
		
		
	
	
	
	 

	
	
	
	private void getAccelerometer(SensorEvent event) {
	    float[] values = event.values;
	    // Movement
	    
	    x = event.values[0];
	    //y = event.values[1];
	   // z = event.values[2];
	    

	    
	  
    	args[0]= x;
    	args[1]= y;
    	args[2]= z;
    	args[3] =locked;
    	//new AsyncSendOSCTask(this, this.oscPortOut).execute(new OSCMessage("hello"));
          
	    

    	//new AsyncSendOSCTask(this, this.oscPortOut).execute(new OSCMessage());
    		
    		
	    	new AsyncSendOSCTask(this, this.oscPortOut).execute(new OSCMessage("/orientation",args));	    	
    		

	    
	}
    

	private void getAccelerometer2(SensorEvent event) {
	    float[] values = event.values;
	    // Movement
	    y = event.values[1];
	    //y = event.values[1];
	   // z = event.values[2];
	    

	    
	  
    	args[0]= x;
    	args[1]= y;
    	args[2]= z;
    	args[3] =locked;
    	//new AsyncSendOSCTask(this, this.oscPortOut).execute(new OSCMessage("hello"));
          
	    

    	//new AsyncSendOSCTask(this, this.oscPortOut).execute(new OSCMessage());
    		
    		
	    	new AsyncSendOSCTask(this, this.oscPortOut).execute(new OSCMessage("/orientation",args));	    	
    		
	    	accelVals.setText("x=: "+x*10+"   y=: "+y*10 + "   lock="+locked);

			
			
			  if (accelVals2.getText() != "LOCKED"){
			  accelVals2.setText(""+Math.round(x*10));
			    }
	    
	}
    







	/*private Object[] xValue(float x2) {
		// TODO Auto-generated method stub
		 x = (float) (((values[0]+10.0)/20.0)*(2.0*Math.PI));
		return null;
	}*/
	

	private void restoreNetworkSettingsFromFile() {
    	try {	
    		FileInputStream fis = openFileInput(NETWORK_SETTINGS_FILE);
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		byte[] buffer = new byte[512];
    		int bytes_read;
    		while((bytes_read = fis.read(buffer)) != -1) {
    			baos.write(buffer, 0, bytes_read);
    		}
    		
    		String data = new String(baos.toByteArray());
    		String[] pieces = data.split("#");
    		
    		ipAddress = pieces[0];
    		port = Integer.parseInt(pieces[1]);
    		
    	}
    	catch(FileNotFoundException fnfe) {}
    	catch(Exception exp) {
    		Toast.makeText(this, "Could Not Read SCAuth File", Toast.LENGTH_SHORT).show();
    		ipAddress = "127.0.0.1"; port = 8000;
    	}
    }
    
    private void restoreOSCSettingsFromFile() {
    	try {
    		oscSettingsHashtable.clear();
    		
    		FileInputStream fis = openFileInput(OSC_SETTINGS_FILE);
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		byte[] buffer = new byte[512];
    		int bytes_read;
    		while((bytes_read = fis.read(buffer)) != -1) {
    			baos.write(buffer, 0, bytes_read);
    		}
    		
    		String data = new String(baos.toByteArray());
    		String[] pieces = data.split("#x#x#");
    		
    		for(int i = 0; i < pieces.length; i+=2) {
    			oscSettingsHashtable.put(pieces[i], pieces[i+1]);
    		}
    	}
    	catch(FileNotFoundException fnfe) {}
    	catch(Exception exp) {
    		Toast.makeText(this, "Could Not Read OSC Settings File", Toast.LENGTH_SHORT).show();
    		oscSettingsHashtable.clear();
       	}
    }
    

    
        	

    
    
    
    private void checkWifiState() {
    	WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	if(!wifiManager.isWifiEnabled()) {
    		showDialog(WIFI_ALERT_DIALOG);
    	}
    }


    
    
    
    
 
    

    
    
    
    
}