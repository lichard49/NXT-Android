package test;
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Drive test
 * @author Richard
 *
 */

public class Test 
{
	public static void main(String[] args)
	{
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b)
			{
				System.exit(0);
			}
			
			public void buttonReleased(Button b)
			{
			}
		});
		
		DifferentialPilot p = new DifferentialPilot(2.25f, 2.25f, Motor.B, Motor.C);
		p.setRotateSpeed(100);
		//p.steer(-200);
		p.rotateLeft();
		while(true)
		{
			
		}
	}
}
