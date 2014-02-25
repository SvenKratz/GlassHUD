package com.kratz.glassHUD;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.util.Log;

public class ArtificialHorizon {

	private int width;
	private int height; 
	
	private float mYaw;
	private float mTilt;
	private float mRoll; 
	
	private static final float ANGULAR_RANGE_PITCH_VISUALIZER = 10.0f; 
	
	private static final int PITCH_VISUALIZER_SIZE = 150;
	
	private static final float ANGULAR_RANGE_PITCH = 25.0f; 
	private static final float ANGULAR_RANGE_YAW = 30.0f;
	private static final String TAG = "ArtificialHorizon";
	
	// navigation
	private double lon;
	private double lat;
	private double alt; 
	private double spd; 
	private String navProvider = "None"; 
	
	
	public ArtificialHorizon(int w, int h) {
		// TODO Auto-generated constructor stub
		width = w;
		height = h;
	}
	
	/****
	 * Updates widget orientation 
	 * @param yaw
	 * @param tilt
	 * @param roll
	 */
	public void updateOrientation(float yaw, float tilt, float roll)
	{
		mYaw = yaw; 
		mTilt = tilt + 90;
		mRoll = roll; 
	}
	
	public void draw(Canvas c)
	{
		// horizon line
		final float rollCos = (float) Math.cos(deg_to_rad(mRoll));
		final float rollSin = (float) Math.sin(deg_to_rad(mRoll));
		
		int pitchY, rollY; 
		
		pitchY = (int) (0.5 * (height - mTilt * height / ANGULAR_RANGE_PITCH));
		//endY = (int) (0.5 * (height - mTilt * height / 90));
		
//		rollY =  (int) ( mRoll * height / 90);
		rollY = (int) (rollSin * (height));
		
		//// horizon bar
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setStrokeWidth(2.0f);
		p.setTextSize(25.0f);
		p.setTextAlign(Align.CENTER);
		
		Paint whiteFill = new Paint();
		whiteFill.setColor(Color.WHITE);
		whiteFill.setStyle(Paint.Style.FILL);
		
		//// ground bar 
		Paint groundPaint = new Paint();
		groundPaint.setARGB(255, 139, 69, 19);
		groundPaint.setStyle(Paint.Style.FILL);
		
		Paint skyPaint = new Paint();
		skyPaint.setARGB(255, 30, 144, 255);
		skyPaint.setStyle(Paint.Style.FILL);
		
		int horizonStartY = pitchY - rollY;
		int horizonEndY = pitchY + rollY;
		
		// ground path
		Path gp = new Path();
		gp.reset();
		gp.moveTo(0, horizonStartY);
		gp.lineTo(width, horizonEndY);
		gp.lineTo(width, height);
		gp.lineTo(0, height);
		gp.lineTo(0,  horizonStartY);
		c.drawPath(gp, groundPaint);
		
		//sky path
		Path sp = new Path();
		sp.reset();
		sp.moveTo(0, horizonStartY);
		sp.lineTo(width, horizonEndY);
		sp.lineTo(width, 0);
		sp.lineTo(0, 0);
		sp.lineTo(0, horizonStartY);
		c.drawPath(sp, skyPaint);
		
		
		
		
		
		// horizon ribbon 
		
		int pitch = (int) mTilt;
		
		int maxPitch = pitch + (int) ANGULAR_RANGE_PITCH_VISUALIZER;
		int minPitch = pitch - (int) ANGULAR_RANGE_PITCH_VISUALIZER;
		
		final float total_pitch_range = 2 * ANGULAR_RANGE_PITCH_VISUALIZER;
		final float pitchTickCoeff = height / (2*ANGULAR_RANGE_PITCH);
		
		// draw pitch ticks
		
		
		// draw line to indicate plane
		
		c.drawLine(width/2 - 130, height /2, width/2 - 80, height / 2, p);
		c.drawLine(width/2 + 130, height /2, width/2 + 80, height / 2, p);
		
		

		p.setTextSize(20.0f);
		int ctr = 0; 
		for (int i = minPitch; i <= maxPitch; i++)
		{
			if (i % 5 == 0)
			{
				int angle = i % 90;
				final int offs = (int) (ctr * pitchTickCoeff - PITCH_VISUALIZER_SIZE/2);
				
				
				
				if (i % 10 == 0)
				{
					
					
					
					float x_offs = 40 * rollCos ;
					float y_offs_a = offs + 40 * (float) Math.sin(-deg_to_rad(mRoll));
					float y_offs_b = offs + 40 * (float) Math.sin(deg_to_rad(mRoll));
					
					// horizontal offset
					float hOff = -(offs ) * rollSin;
					
					c.drawLine(hOff + width/2 - x_offs , height/2  + y_offs_a, hOff +   width/2 + x_offs, height / 2 + y_offs_b, p);
					
					final String pitchTxt = Integer.toString(-i);
					
					c.save();
					c.rotate(mRoll, width / 2, height / 2);
					c.drawText(pitchTxt,  width/2 - 60, height/2 + offs + 10, p);
					c.drawText(pitchTxt,  width/2 + 60, height/2 + offs + 10, p);
					c.restore();
					
				}
				else
				{
					
					float x_offs = 20 * rollCos ;
					float y_offs_a = offs + 20 * (float) Math.sin(-deg_to_rad(mRoll));
					float y_offs_b = offs + 20 * (float) Math.sin(deg_to_rad(mRoll));
					
					// horizontal offset
					float hOff = -(offs ) * rollSin;
					
					c.drawLine(hOff + width/2 - x_offs , height/2  + y_offs_a, hOff +   width/2 + x_offs, height / 2 + y_offs_b, p);

				}
				
				
			}
			ctr++; 
		}
		
		
		
		// compass ribbon
		int heading = (int) mYaw;
		
		int maxHeading = heading + (int) ANGULAR_RANGE_YAW;
		int minHeading = heading - (int) ANGULAR_RANGE_YAW;
		
		final float total_angular_range  = 2 * ANGULAR_RANGE_YAW;
		
		final float tickMarkCoeff = width / total_angular_range;
		
		//ArrayList<Integer> tickmarks = new ArrayList<Integer>(10);
		
		// search for angular increments to use --> could be done when a yaw update is given  
		 ctr = 0; 
		for (int i = minHeading; i <= maxHeading; i++)
		{
			if (i % 5 == 0)
			{
				
				int angle = i % 360; 
				
				// add a tick mark
				// offset in pixels
				final int offs  = (int) (ctr * tickMarkCoeff);
				//tickmarks.add(offs);
//				Log.d(TAG, "offs " + offs);
				if (i % 10 == 0)
				{
					p.setTextSize(20.0f);
					// todo: "North" display
					c.drawText(Integer.toString(angle/10), offs, 325, p);
					c.drawLine(offs, 360, offs, 330, p);
				}
				else 
				{
					
					c.drawLine(offs, 360, offs, 340, p);
				}
			}
			ctr++;
		}
		
		// draw the pfd notch
		Path hnp = new Path();
		int notchY = 310;
		int mid = width/2;
		hnp.moveTo(mid, notchY );
		hnp.lineTo(mid + 15, notchY - 10);
		hnp.lineTo(mid - 15, notchY - 10);
		hnp.lineTo(mid, notchY);
		c.drawPath(hnp, whiteFill);
		
		
		p.setTextSize(25.0f);
		// speed display
		c.drawText(Integer.toString((int) spd), 10, height/2, p);
		
		// alt display
		c.drawText(Integer.toString((int) alt), width - 40, height/2, p);
		
		// source display
		// c.drawText(navProvider, width-100, 30, p);
		
		if (!navProvider.equals("none"))
		{
			String locationString = ""; 
//			locationString += Character.toTitleCase(navProvider.charAt(0));
			locationString = String.format("%3.3f %3.3f", lat, lon);
			c.drawText(locationString, width-100, 30, p);
			
		}
		
		// ribbon for compass heading
		
		String yawString = String.format("%3.0f", mYaw);
		
		c.drawText(yawString, width/2, 295 , p);
		
		
		c.drawLine(0, horizonStartY, width, horizonEndY, p);
		
		
		
		
	}

	private double deg_to_rad(float d) {
		// TODO Auto-generated method stub
		return 2*Math.PI * d / 360.0; 
	}

	public void updateLocation(double aLat, double aLon, double aAlt, double aSpd,
			String aProv) 
	{
		lat = aLat; 
		lon = aLon;
		alt = aAlt; 
		spd= aSpd; 
		navProvider = aProv;
	}

	public void updateLocationLatLon(double aLat, double aLon) {
		lat = aLat; 
		lon = aLon;
		navProvider = "network";
	}

	public void updateLocationAltSpd(double aAlt, double aSpd) {
		alt = aAlt; 
		spd = aSpd;
		navProvider = "GPS";
	}

}
