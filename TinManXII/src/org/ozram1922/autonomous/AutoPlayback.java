package org.ozram1922.autonomous;

import java.util.ArrayList;

import org.ozram1922.OzMath;

public class AutoPlayback {
	
	private ArrayList<Long> _timeTable = new ArrayList<Long>();
	private ArrayList<ArrayList<Double>> _valueTable = new ArrayList<ArrayList<Double>>();
	private long _nanoTimeOffset = 0;
	private int _nearestIndex = 0;
	private int _valueCount;
	
	public AutoPlayback(int valueCount)
	{
		_valueCount = valueCount;
	}
	
	public AutoPlayback()
	{
		this(1);
	}
	
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
	
	public ArrayList<Double> GetSetpoint()
	{
		return GetSetpoint(0);
	}
	
	//lookahead time allows to compensate for the time it takes for the setpoint to be reached
	//There is probably a better way to do this, but this may be OK
	public ArrayList<Double> GetSetpoint(double lookAheadTime)
	{
		long relativeTime = System.nanoTime() - _nanoTimeOffset;
		_nearestIndex = GetNearestIndex(relativeTime, _nearestIndex);
		if(_nearestIndex < 0)
			return new ArrayList<Double>(_valueCount);
		
		long prevTime = _timeTable.get(_nearestIndex);
		long nextTime = _timeTable.get(_nearestIndex + 1);
		
		ArrayList<Double> prevVal = _valueTable.get(_nearestIndex);
		ArrayList<Double> nextVal = _valueTable.get(_nearestIndex + 1);
		
		return OzMath.LRP(relativeTime + (long)(lookAheadTime * 1000000000.0), prevTime, nextTime, prevVal, nextVal);
	}
	
	//returns the next time duration in seconds
	public double GetNextTimeDuration()
	{
		if(_nearestIndex < 0 || _nearestIndex > _timeTable.size() - 2)
			return 0;
		//since we may be part-way through one of the points, make sure to project to the next point to ensure we don't
		//		get an issue that both the projected distance and time are very small and result in strange division
		return (double)(_timeTable.get(_nearestIndex) - _timeTable.get(_nearestIndex + 2)) / 1000000000.0;
	}
	
	public void Deserialize(String csv) throws NumberFormatException
	{
		_timeTable.clear();
		_valueTable.clear();
		String[] rows = csv.split("\n");
		for(int i = 0; i < rows.length; ++i)
		{
			String[] parts = rows[i].split(",");
			if(parts.length < _valueCount + 1)
				throw new NumberFormatException();//if there is not a correct number of elements in this playback
			
			_timeTable.add(Long.parseLong(parts[0]));
			ArrayList<Double> value = new ArrayList<Double>(_valueCount);
			for(int j = 0; j < _valueCount; ++j)
			{
				value.set(j, Double.parseDouble(parts[1 + j]));
			}
		}
	}

}
