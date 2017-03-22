package org.ozram1922.image;

import java.util.ArrayList;

public class PixyCamFrame
{
	public PixyCamFrame(int frameId, ArrayList<PixyCamBlock> list)
	{
		_frameId = frameId;
		List = list;
	}
	public PixyCamFrame(int frameId)
	{
		_frameId = frameId;
		List = new ArrayList<PixyCamBlock>();
	}
	
	private int _frameId = 0;
	public boolean IsDifferent(PixyCamFrame other)
	{
		return _frameId == other._frameId;
	}
	public boolean IsDifferent(int frameId)
	{
		return _frameId == frameId;
	}
	
	public int GetFrameID()
	{
		return _frameId;
	}
	
	public ArrayList<PixyCamBlock> List;
}
