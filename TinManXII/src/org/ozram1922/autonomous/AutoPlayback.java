package org.ozram1922.autonomous;

import java.util.ArrayList;

import org.ozram1922.OzMath;

public class AutoPlayback {
	
	private ArrayList<Long> _timeTable = new ArrayList<Long>();
	private ArrayList<Double> _distanceTable = new ArrayList<Double>();
	private long _nanoTimeOffset = 0;
	private int _nearestIndex = 0;
	
	private int GetNearestIndex(long time, int guess)
	{
		try
		{
			for(int i = guess; i < _timeTable.size(); i++)
			{
				if(_timeTable.get(i) > time)
				{
					if(i == 0)
					{
						return 0;
					}
					else
					{
						return i - 1;
					}
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
		}
		return -1;
	}
	
	public void StartPlayback()
	{
		_nanoTimeOffset = System.nanoTime();
		_nearestIndex = 0;
	}
	
	public boolean IsFinished()
	{
		long relativeTime = System.nanoTime() - _nanoTimeOffset;
		_nearestIndex = GetNearestIndex(relativeTime, _nearestIndex);
		if(_nearestIndex < 0)
			return true;
		return false;
	}
	
	public double GetSetpoint()
	{
		return GetSetpoint(0);
	}
	
	//lookahead time allows to compensate for the time it takes for the setpoint to be reached
	//There is probably a better way to do this, but this may be OK
	public double GetSetpoint(double lookAheadTime)
	{
		long relativeTime = System.nanoTime() - _nanoTimeOffset;
		_nearestIndex = GetNearestIndex(relativeTime, _nearestIndex);
		if(_nearestIndex < 0)
			return Double.NaN;
		
		long prevTime = _timeTable.get(_nearestIndex);
		long nextTime = _timeTable.get(_nearestIndex + 1);
		
		double prevDistance = _distanceTable.get(_nearestIndex);
		double nextDistance = _distanceTable.get(_nearestIndex + 1);
		
		return OzMath.LRP(relativeTime + (long)(lookAheadTime * 1000000000.0), prevTime, nextTime, prevDistance, nextDistance);
	}
	
	public void Deserialize(String csv) throws NumberFormatException
	{
		String[] rows = csv.split("\n");
		for(int i = 0; i < rows.length; ++i)
		{
			String[] parts = rows[i].split(",");
			_timeTable.add(Long.parseLong(parts[0]));
			_distanceTable.add(Double.parseDouble(parts[1]));
		}
	}

}
