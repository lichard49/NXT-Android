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
		//makes the escape button exit the program
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
		
		//set up drive system with motors B and C
		DifferentialPilot p = new DifferentialPilot(2.1f, 4.4f, Motor.B, Motor.C);
		p.setTravelSpeed(6);
		
		//main loop - reconnects if connection is lost   
		while (true)
		{
		    LCD.drawString("Waiting",0,0);
		    LCD.refresh();
	        btc = Bluetooth.waitForConnection(); //start BT
	        btc.setIOMode(NXTConnection.RAW); //necessary for Android - NXT connections
		    LCD.clear();
		    LCD.drawString("Connected",0,0);
		    LCD.refresh();  
		    dis = btc.openDataInputStream(); //open streams

		    while(true)
		    {
		        String s = "";
	            try 
	            {
	            	//deprecated but it was the easiest way and it worked
	            	s = dis.readLine();
				} 
	            catch (IOException e) 
	            {
				}   

	            //puts received value into steering range of -100 to 100
	            int i = (int)(-1*(Integer.parseInt(s)-100));
	            p.steer(i);
		        LCD.clear();
		        LCD.drawInt(i, 4, 4);
		        
		    }

		}
	}
	
}
