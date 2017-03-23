package org.ozram1922.image;

import java.util.LinkedList;
import java.util.Queue;

public class CircularQueue {

	/// variables for a circular queue
	private Queue<Byte> _queue = new LinkedList<Byte>();
	
	public byte[] Pop(int maxSize)
	{
		byte[] bytes = new byte[maxSize];
		int j = 0;
		for(int i = 0; i < maxSize; ++i)
		{
			if(_queue.isEmpty())
				break;
			j++;
			bytes[i] = _queue.remove();
		}
		
		byte[] retBytes = new byte[j];
		for(int i = 0; i < j; ++i)
		{
			retBytes[i] = bytes[i];
		}
		return retBytes;
	}
	
	public void Push(byte[] bytes)
	{
		for(int i = 0; i < bytes.length; ++i)
		{
			_queue.add(bytes[i]);
		}
	}
}
