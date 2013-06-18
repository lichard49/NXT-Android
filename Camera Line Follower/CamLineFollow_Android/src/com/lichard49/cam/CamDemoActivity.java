package com.lichard49.cam;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

public class CamDemoActivity extends Activity 
{
	public static Display disp;
	public static Draw draw;
	public static Activity a;
	public final String nxtAddress = "00:16:53:09:AE:F0"; //my NXT's address
	public static Conn btConn; 
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //start Bluetooth things independently
        btConn = new Conn(nxtAddress);
        btConn.enableBT();
        new Thread()
        {
        	public void run()
        	{
        		btConn.connectToNXT();
        	}
        }.start();
        
        //setup camera display
        a = this;
        disp = new Display(this);
        draw = new Draw(this);
        setContentView(disp);
        addContentView(draw, new LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
    }
}