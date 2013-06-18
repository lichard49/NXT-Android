package com.lichard49.cam;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class Display extends SurfaceView implements Callback 
{
	public SurfaceHolder sh;
	public Camera cam;
	public int width;
	public int height;
	public Bitmap bitmap;
	int minIndex = -1;
	boolean lightOn = false;
	
	public Display(Context context) 
	{
		super(context);
		
		sh = getHolder();
		sh.addCallback(this);
		sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) 
	{
		if(cam == null)
			cam = Camera.open();
		
		setFlashlight(true);
		try
		{
			cam.setPreviewCallback(new PreviewCallback()
			{
				public void onPreviewFrame(byte[] data, Camera c) 
				{
					final byte[] d = data;
					if(cam != null)
					{
						//changing the given data to a bitmap image
						Size previewSize = cam.getParameters().getPreviewSize();
						YuvImage yu = new YuvImage(d, ImageFormat.NV21, 
								previewSize.width, previewSize.height, null);
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						yu.compressToJpeg(new Rect(0, 0, previewSize.width, 
								previewSize.height), 0, out);
						Bitmap b = BitmapFactory.decodeByteArray(out.toByteArray(),
								0, out.toByteArray().length);
						//rotate the image because the camera returns the image off by 90 degrees
						Matrix m = new Matrix();
						m.postRotate(90);
						b = Bitmap.createBitmap(b, 0, 0, previewSize.width, 
								previewSize.height, m, true);
						
						//finding the "darkest" spot using the red component 
						int hU = b.getHeight()/3;
						int min = 256;
						for(int x = 0; x < b.getWidth(); x++)
						{
							int r = Color.red(b.getPixel(x, hU));
							if(r < min)
							{
								min = r;
								minIndex = x;
							}
						}
						
						//scaling the position of the darkest value for use by the NXT 
						int result = (int)((minIndex/480.0)*200);
						if(CamDemoActivity.btConn.connected)
						{
							//send on over
							CamDemoActivity.btConn.writeMessage((result+"\n").getBytes());
						}
						
						//show the picture on the screen
						bitmap = b;
						CamDemoActivity.draw.invalidate();
					}
				}
			});
		}
		catch (Exception e)
		{
		}
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int w,
			int h) 
	{
		cam.setDisplayOrientation(90);
		
		cam.startPreview();
		width = w;
		height = h;
	}

	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		cam.setPreviewCallback(null);
		cam.stopPreview();
		cam.release();
		cam = null;
	}
	
	//http://stackoverflow.com/questions/3878294/camera-parameters-flash-mode-torch-replacement-for-android-2-1
	public boolean setFlashlight(boolean isOn)
	{
	    if (cam == null)
	    {
	        return false;
	    }
	    Camera.Parameters params = cam.getParameters();
	    String value;
	    if (isOn) // we are being ask to turn it on
	    {
	        value = Camera.Parameters.FLASH_MODE_TORCH;
	    }
	    else  // we are being asked to turn it off
	    {
	        value =  Camera.Parameters.FLASH_MODE_AUTO;
	    }

	    try{    
	        params.setFlashMode(value);
	        cam.setParameters(params);

	        String nowMode = cam.getParameters().getFlashMode();

	        if (isOn && nowMode.equals(Camera.Parameters.FLASH_MODE_TORCH))
	        {
	            return true;
	        }
	        if (! isOn && nowMode.equals(Camera.Parameters.FLASH_MODE_AUTO))
	        {
	            return true;
	        }
	        return false;
	    }
	    catch (Exception ex)
	    {
	    }
	    return false;
	}
	
	public byte[] intToByteArray(int value) 
	{ 
		return new byte[] 
		{ 
			(byte)(value >>> 24), 
			(byte)(value >>> 16), 
			(byte)(value >>> 8), 
			(byte) value}; 
		}
}
