package com.lichard49.cam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class Draw extends View 
{	
	public Draw(Context c)
	{
		super(c);
	}
	
	public void onDraw(Canvas c)
	{
		Paint p = new Paint();
		p.setColor(Color.RED);
		
		//draw the camera's contents
		if(CamDemoActivity.disp.bitmap!= null)
			c.drawBitmap(CamDemoActivity.disp.bitmap, 0, 0, p);

		//draw the pointer of the darkest spot
		int hU = getHeight()/3;
		int index = CamDemoActivity.disp.minIndex;
		p.setColor(Color.YELLOW);
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(5);
		c.drawRect(index, hU-90, index+1, hU+10, p);
	}
}
