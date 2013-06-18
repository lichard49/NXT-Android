package com.lichard49.robocompass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.TextView;

public class RoboCompass extends Activity implements SensorEventListener
{
	public float[] magneticValues = null;
	public float[] accelValues = null;
	public Sensor magneticSensor;
	public Sensor accelSensor;
	public SensorManager sensorManager;
	public TextView text;
	public final String nxtAddress = "00:16:53:09:AE:F0";
	public Conn btConn;
	public PowerManager pm;
	PowerManager.WakeLock wl;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    
        //make sure the program stays awake so it doesn't die on NXT
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        
        //handle BT things
        btConn = new Conn(nxtAddress);
        btConn.enableBT();
        new Thread()
        {
        	public void run()
        	{
        		btConn.connectToNXT();
        	}
        }.start();
        
        //GUI setup
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.compassHeading);

        //sensor setup
    	sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    	accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }
    
	public void onAccuracyChanged(Sensor s, int a)
	{
		
	}
	
	public void onSensorChanged(SensorEvent e)
	{
		switch(e.sensor.getType())
		{
			case Sensor.TYPE_MAGNETIC_FIELD: magneticValues = e.values.clone(); break;
			case Sensor.TYPE_ACCELEROMETER: accelValues = e.values.clone(); break;
		}
		
		//combining magnetic field sensor and accelerometer in order to determine compass heading
		if(magneticValues != null && accelValues != null)
		{
			if (accelValues != null && magneticValues != null) 
			{
		        float R[] = new float[9];
		        float I[] = new float[9];
		        boolean success = SensorManager.getRotationMatrix(R, I, accelValues, magneticValues);
		        
		        if (success) 
		        {
		          float orientation[] = new float[3];
		          SensorManager.getOrientation(R, orientation);
		          int a = ((int)(-orientation[0]*36/(2*3.14159f)))*10;
		          text.setText(a+""); //update GUI about it
						
		          //if BT is open, send appropriate message
		          if(btConn.connected)
		          {
		        	  if(inRange(a, 50, 20, 20))
		        	  {
		        		  btConn.writeMessage((byte)0);
		        		  text.append(" stop");
		        	  }
		        	  else if(a < 50)
		        	  {
		        		  btConn.writeMessage((byte)2);
		        		  text.append(" left");
		        	  }
		        	  else if(a > 50)
		        	  {
		        		  btConn.writeMessage((byte)1);
		        		  text.append(" right");
		        	  }
		          }    				
		        }
		      }
		}
	}
    
    public void onResume()
    {
    	super.onResume();
    	
    	//restart resources
    	sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
    	sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
    	
        wl.acquire();
    }
    
    public void onPause()
    {
    	super.onPause();
    	
    	//manage resources before closing
    	sensorManager.unregisterListener(this);
    	
    	wl.release();
    }
    
    public boolean inRange(double source, double target, double lowerBound, double upperBound)
    {
    	//this gives the NXT some slack due to imprecise values
    	return source > target - lowerBound && source < target + upperBound;
    }
}