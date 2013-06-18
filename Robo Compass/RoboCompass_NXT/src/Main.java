import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.navigation.DifferentialPilot;


public class Main 
{
	public static DataInputStream dis;
	public static NXTConnection btc;
	
	public static void main(String[] args)
	{
		//handle exit button
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b)
			{
			     try 
			     {
			    	 if(dis != null)
			    		 dis.close();
			    	 Thread.sleep(100); // wait for data to drain
			     } 
			     catch (Exception e) 
			     {
			     }

				 LCD.clear();
				 LCD.drawString("Closing",0,0);
				 LCD.refresh();
				 if(btc != null)
					 btc.close();
				 LCD.clear();
				 System.exit(0);
			}
			
			public void buttonReleased(Button b)
			{
			}
		});
		
		//setup dp with large bicycle wheels
		DifferentialPilot p = new DifferentialPilot(2.25f, 2.25f, Motor.B, Motor.C);
		p.setRotateSpeed(120); //not too fast
		
		//main loop - reconnects if connection is lost   
		while (true)
		{
		    LCD.drawString("Waiting",0,0);
		    LCD.refresh();
	        btc = Bluetooth.waitForConnection(); //start BT
	        btc.setIOMode(NXTConnection.RAW);
		    LCD.clear();
		    LCD.drawString("Connected",0,0);
		    LCD.refresh();  
		    dis = btc.openDataInputStream(); //open streams

		    while(true)
		    {
		        int n = -1;

	            try 
	            {
	            	n = dis.readByte();
				} 
	            catch (IOException e) 
	            {
				}   
	            //n: -1 = left, 0 = stop, 1 = right

		        LCD.clear();
		        LCD.drawInt(n, 4, 4);

		        switch(n)
		        {
		        	case 2: p.rotateLeft(); break;
		        	case 0: p.stop(); break;
		        	case 1: p.rotateRight(); break;
		        }
		        
		    }

		}
	}
	
	public static boolean inRange(int source, int target, int upperBound, int lowerBound)
	{
		return source > target-lowerBound && source < target+upperBound;
	}
}
