package com.kratz.glassHUD;

import com.kratz.aviator.R;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener, LocationUpdateListener {

	
	private static final String TAG = "HUDView";
	private SensorManager mSensorManager; 
	private Sensor mOrientation; 
	private Sensor mAccelerometer;
	
	
	private Handler screenUpdateHandler = new Handler();
	private static int SCREEN_UPDATE_DELAY_MS = 33; 
	
	private HUDView hudView;  
	
	// GPS
	GPSTracker gps; 
	
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSensorManager  = (SensorManager) getSystemService(SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);   
        
        hudView = (HUDView) findViewById(R.id.hudview);
              
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_FASTEST);
    	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    	
    	screenUpdateHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateScreen();
				screenUpdateHandler.postDelayed(this, SCREEN_UPDATE_DELAY_MS);	
			}
    	}
    	, SCREEN_UPDATE_DELAY_MS);
    	
    	
        // start GPS
    	gps = new GPSTracker(this);
 
    }
    
    /**
     * Updates the main screen visualization elements 
     */
    protected void updateScreen() {
		hudView.invalidate();
	}


	@Override
    protected void onPause() {
    	super.onPause();
    	mSensorManager.unregisterListener(this);
    	screenUpdateHandler.removeCallbacksAndMessages(null);
    	if (gps != null)
    		gps.stopUsingGPS();
    }


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		if ( event.sensor == mOrientation)
		{
			float yaw = event.values[0];
			float pitch = event.values[1];
			float roll = event.values[2]; 
			
			// Log.d(TAG, "Yaw " + yaw + " Pitch " + (pitch + 90) + " Roll " + roll); 
			
			hudView.updateOrientation(yaw, pitch, roll);
		}	
		
		
	}


	@Override
	public void OnLocationUpdate(Location loc) {
		// TODO Auto-generated method stub
		hudView.updateLocation(loc);
		
	} 
}
