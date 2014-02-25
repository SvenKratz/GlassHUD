package com.kratz.glassHUD;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class HUDView extends View {

	private static final String TAG = "HUDView";
	public static int sWidth;
	public static int sHeight; 
	
	
	private float pitch;
	private float tilt;
	private float heading;
	
	
	private ArtificialHorizon horizon;
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setTilt(float tilt) {
		this.tilt = tilt;
	}

	public void setHeading(float heading) {
		this.heading = heading;
	}

	public HUDView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HUDView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HUDView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH)
	{
		init();
	}
	
	private void init()
	{
		// TODO Auto-generated constructor stub
		sWidth = this.getWidth();
		sHeight = this.getHeight();
				
		horizon = new ArtificialHorizon(sWidth, sHeight);
				
		Log.d(TAG, "Screen Height " + sHeight + " Screen Width " + sWidth);
	}
	
	/****
	 * Updates widget orientation 
	 * @param yaw
	 * @param tilt
	 * @param roll
	 */
	public void updateOrientation(float yaw, float tilt, float roll)
	{
		if (horizon != null)
		{
			horizon.updateOrientation(yaw, tilt, roll);
		}
	}
	
	public void updateLocation(Location loc)
	{		
		String prov = loc.getProvider();
		
		if (prov.equals("network"))
		{
			double lat = GPSTracker.instance.getLatitude();
			double lon = GPSTracker.instance.getLongitude();
			horizon.updateLocationLatLon(lat, lon);
			Log.d(TAG, "updateLocation Lat: " + lat + " lon " + lon + " prov " + prov);
			
		}
		else
		{
			double alt = loc.getAltitude();
			double spd = loc.getSpeed();
			horizon.updateLocationAltSpd(alt,spd);
			Log.d(TAG, "updateLocation: spd " + spd + " prov " + prov);
		}
		
	}
	
	@Override
	protected void onDraw(Canvas c)
	{
		
		//sWidth = this.getWidth();
		//sHeight = this.getHeight();
		// Log.d(TAG, "onDraw: w " + sWidth + " h " + sHeight);
		super.onDraw(c);
		c.drawColor(Color.BLACK);
		
		horizon.draw(c);
	}

}
