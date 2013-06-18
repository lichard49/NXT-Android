package com.lichard49.cam;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class Conn
{
	public static BluetoothAdapter bt;
	public BluetoothSocket socket;
	public String nxtAddress;
	public boolean connected = false;
	
	//this class facilitates all things Bluetooth
	public Conn(String a)
	{
		nxtAddress = a;
	}
	
	//Enable Bluetooth adapter
	public void enableBT()
	{
		bt = BluetoothAdapter.getDefaultAdapter();
		
        if(bt.isEnabled()==false)
        {
            bt.enable();
            while(!(bt.isEnabled())) //wait for Bluetooth
            {}						// to finish enabling
        }
	}
	
	//connect to given NXT
	public boolean connectToNXT()
	{
		BluetoothDevice nxtt = bt.getRemoteDevice(nxtAddress);
		
		try 
		{
		    socket = nxtt.createRfcommSocketToServiceRecord(UUID
		            .fromString("00001101-0000-1000-8000-00805F9B34FB"));
		    socket.connect();
		
		    connected = true;
		} 
		catch (IOException e) 
		{
		    connected = false;
		}
			
		return connected;
    }
	
	//writing multiple bytes
	public void writeMessage(byte[] msg)
	{
		if(socket!=null)
    	{
        	try 
        	{
        		OutputStream out = socket.getOutputStream();
        		out.write(msg);
        		out.flush();
        	} 
        	catch (IOException e) 
        	{
        	}
    	}
		else
    	{
    	}
	}
	
	//write a single byte
	public void writeMessage(byte msg)
	{
		if(socket!=null)
		{
            try 
            {
                OutputStream out = socket.getOutputStream();
                out.write(msg);
                out.flush();
            } 
            catch (IOException e) 
            {
            }
        }
        else
        {
        }
	}
	 
	//read messages from the NXT
	public int readMessage()
	{
		int msg;

		if(socket!=null)
		{
			try 
			{
				InputStreamReader in = new InputStreamReader(socket.getInputStream());
				msg = in.read();

				return msg;
			} 	
			catch (IOException e) 
			{
				return -1;
			}	
		}
		else
		{
			return -1;
		}
	}
}
