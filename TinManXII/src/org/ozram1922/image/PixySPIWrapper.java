package org.ozram1922.image;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class PixySPIWrapper implements PixyCamWrapper
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
		_pixy = new SPI(port);
		//_pixy.read(true, ret, 1);
		//_netTable = NetworkTable.getTable("TestSPIBytes");
	}
	
	/*
	 * 
	 * Fields
	 * 
	 */
	private SPI _pixy;
	SPI.Port _port = SPI.Port.kOnboardCS0;
	

	CircularQueue _queue = new CircularQueue();

	//DON"T TOUCH THESE
	byte[] ret = new byte[OutBufSize];
	int retIndex = OutBufSize;
	
	public byte[] ViewBuffer()
	{
		return ret;
	}
	
	/*
	 * 
	 * Public Methods
	 * 
	 */
	NetworkTable _netTable;
	public synchronized byte GetByte()
	{
		if(retIndex > OutBufSize-1)
		{
			retIndex = 0;
			byte[] sendBytes = _queue.Pop(OutBufSize/2);
			byte[] sendBytesAll = new byte[OutBufSize];
			int i = 0;
			for(;i < sendBytes.length*2; i += 2)
			{
				sendBytesAll[i] = SyncByteData;
				sendBytesAll[i+1] = sendBytes[i/2];
			}
			for(int j = i; j < OutBufSize; j += 2)
			{
				sendBytesAll[j] = SyncByte;
				sendBytesAll[j + 1] = (byte)0;
			}
			_pixy.transaction(sendBytesAll, ret, OutBufSize);
		}
		return ret[retIndex++];
	}
	
	byte[] send2 = new byte[2];
	public synchronized byte[] Get2Bytes()
	{
		byte[] ret = new byte[2];
		/*_pixy.read(true, ret, count);
		return ret;*/
		_pixy.transaction(send2, ret, 2);
		return ret;
	}
	
	public short GetWord()
	{
		// ordering is big endian because Pixy is sending 16 bits through SPI 
		int w;
		int c;

		w = GetByte();
		
		c = GetByte(); // send out data byte
		
		w <<= 8;
		w += c;
		
		return (short)w;
	}
	public synchronized void Send(byte[] data)
	{
		_queue.Push(data);
	}
}