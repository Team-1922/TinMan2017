package org.ozram1922.image;

import edu.wpi.first.wpilibj.SPI;

public class PixySPIWrapper
{
	/*
	 * 
	 * Constants
	 * 
	 */
	public static final int OutBufSize = 64;	
	public static final byte SyncByte = 0x5a;
	public static final byte SyncByteData = 0x5b;
	
	
	/*
	 * 
	 * Constructor
	 * 
	 */
	public PixySPIWrapper(SPI.Port port)
	{
		_port = port;
		_pixy = new SPI(_port);
	}
	
	/*
	 * 
	 * Fields
	 * 
	 */
	private SPI _pixy;
	SPI.Port _port = SPI.Port.kOnboardCS0;
	
	/// variables for a circular queue
	private byte[] _outBuf = new byte[OutBufSize];
	private int _outLen = 0;
	private int _outWriteIndex = 0;
	private int _outReadIndex = 0;


	//DON"T TOUCH THESE
	byte[] ret = new byte[1];
	byte[] send = new byte[1];
	
	/*
	 * 
	 * Public Methods
	 * 
	 */
	public synchronized byte GetByte(byte out)
	{
		send[0] = out;
		_pixy.transaction(send, ret, 1);
		return ret[0];
	}
	
	public short GetWord()
	{
		// ordering is big endian because Pixy is sending 16 bits through SPI 
		short w;
		byte c, cout = 0;
		
		if (_outLen > 0)
		{
		    w = GetByte(SyncByteData);
		    //only this needs to be locked
		    synchronized(this)
		    {
			    cout = _outBuf[_outReadIndex++];
			    _outLen--;
			    if (_outReadIndex==OutBufSize)
			      _outReadIndex = 0; 
		    }
		}
		else
		    w = GetByte(SyncByte); // send out sync byte
		w <<= 8;
		c = GetByte(cout); // send out data byte
		w |= c;
		
		return w;
	}
	public synchronized int Send(byte[] data, int len)
	{
		  int i;

		  // check to see if we have enough space in our circular queue
		  if (_outLen+len>OutBufSize)
		      return -1;

		  _outLen += len;
		  for (i=0; i<len; i++)
		  {
		      _outBuf[_outWriteIndex++] = data[i];
		      if (_outWriteIndex==OutBufSize)
		    	  _outWriteIndex = 0;
		  }
		  return len;
	}
}