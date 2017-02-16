package org.ozram1922.autonomous;

import java.util.ArrayList;

public class AutoPlayback {
	
	private ArrayList<Long> _timeTable = new ArrayList<Long>();
	private ArrayList<Double> _distanceTable = new ArrayList<Double>();
	private long _nanoTimeOffset = 0;
	private int _nearestIndex = 0;
	
	private double LinearInterpolate(double x, double y, double a)
	{
		return x*(1-a) + y*a;
	}
	
	private double LinearInterpolate(double a, double key1, double key2, double val1, double val2)
	{
		double normA = (key1 - a) / (key2-key1);
		return LinearInterpolate(val1, val2, normA);
	}
	
	private int GetNearestIndex(long time, int guess)
	{
		for(int i = guess; i < _timeTable.size(); ++i)
		{
			if(_timeTable.get(i) < time)
				return i - 1;
		}
		return -1;
	}
	
	public void StartPlayback()
	{
		_nanoTimeOffset = System.nanoTime();
	}
	
	public boolean IsFinished()
	{
		long timeNow = System.nanoTime();
		_nearestIndex = GetNearestIndex(timeNow, _nearestIndex);
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
		long timeNow = System.nanoTime();
		_nearestIndex = GetNearestIndex(timeNow, _nearestIndex);
		if(_nearestIndex < 0)
			return Double.NaN;
		
		long prevTime = _timeTable.get(_nearestIndex);
		long nextTime = _timeTable.get(_nearestIndex + 1);
		
		double prevDistance = _distanceTable.get(_nearestIndex);
		double nextDistance = _distanceTable.get(_nearestIndex + 1);
		
		return LinearInterpolate(timeNow - _nanoTimeOffset + (long)(lookAheadTime * 1000000000.0), prevTime, nextTime, prevDistance, nextDistance);
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
