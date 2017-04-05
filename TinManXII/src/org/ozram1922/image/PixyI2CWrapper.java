package org.ozram1922.image;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * The wrapper for accessing the PixyCam over the I2C Interface
 * @author Kevin Mackenzie
 *
 */
public class PixyI2CWrapper implements PixyCamWrapper
{
	
	/*
	 * 
	 * Constructor
	 * 
	 */
	public PixyI2CWrapper(int bufferSize)
	{
		_port = I2C.Port.kOnboard;
		_pixy = new I2C(_port, 0x54);
		BufferSize = bufferSize;
		_ret = new byte[BufferSize];
	}
	
	public PixyI2CWrapper()
	{
		this(100);
	}
	
	/*
	 * 
	 * Fields
	 * 
	 */
	private I2C _pixy;
	I2C.Port _port = I2C.Port.kOnboard;
	public final int BufferSize;
	

	//CircularQueue _queue = new CircularQueue();

	//DON"T TOUCH THESE
	byte[] _ret;
	int _retIndex;
	
	public byte[] ViewBuffer()
	{
		return _ret;
	}
	
	/*
	 * 
	 * Public Methods
	 * 
	 */
	
	public synchronized void RefreshBuffer()
	{
		_retIndex = 0;
		_pixy.readOnly(_ret, BufferSize);
	}
	
	NetworkTable _netTable;
	public synchronized byte GetByte()
	{
		if(_retIndex > BufferSize-1)
		{
			return 0;//we don't want to get more info, because that should be for the next frame
		}
		return _ret[_retIndex++];
	}
	
	//byte[] send2 = new byte[2];
	//public synchronized byte[] Get2Bytes()
	//{
	//	byte[] ret = new byte[2];
	//	/*_pixy.read(true, ret, count);
	//	return ret;*/
	//	_pixy.transaction(send2, ret, 2);
	//	return ret;
	//}
	
	public short GetWord()
	{
		// ordering is little endian because Pixy is sending 16 bits through I2C
		int w; 
		int c;
		c = GetByte();
		w = GetByte();
		w <<= 8;
		w |= (c & 0xFF); //well this works, but I don't like it :(
		return (short)w;
	}
	public synchronized void Send(byte[] data)
	{
		_pixy.transaction(data, data.length, null, 0);
	}
}