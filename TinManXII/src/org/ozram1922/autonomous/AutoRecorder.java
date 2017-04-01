package org.ozram1922.autonomous;

import java.io.StringWriter;
import java.util.ArrayList;

public class AutoRecorder {
	
	private ArrayList<Long> _timeTable = new ArrayList<Long>();
	private ArrayList<ArrayList<Double>> _valueTable = new ArrayList<ArrayList<Double>>();
	private long _nanoTimeOffset = 0;
	private int _valueCount = 0;
	
	public AutoRecorder(int valueCount)
	{
		_valueCount = valueCount;
	}
	
	public AutoRecorder()
	{
		this(1);
	}
	
	public void StartRecording()
	{
		_nanoTimeOffset = System.nanoTime();
		_timeTable.clear();
		_valueTable.clear();
	}
	
	public void Update(double newVal)
	{
		ArrayList<Double> val = new ArrayList<Double>(_valueCount);
		for(int i = 0; i < val.size(); ++i)
		{
			val.add(newVal);
		}
		Update(val);
	}
	
	public void Update(ArrayList<Double> newVal)
	{
		_timeTable.add(System.nanoTime() - _nanoTimeOffset);
		_valueTable.add(newVal);
	}
	
	//this helps to remove noise in the beginning and end of the data
	/*public void TrimVelocityData(double threshold)
	{
		//remove leading and trailing small values
		
		int startIndex = 0;
		for(; 
			(startIndex < _valueTable.size()) || (_valueTable.get(startIndex) < threshold); 
			++startIndex);
		
		int endIndex = _valueTable.size() - 1;
		for(;
			(endIndex > startIndex) || (_valueTable.get(endIndex) < threshold);
			--endIndex);
		
		_valueTable = (ArrayList<Double>) _valueTable.subList(startIndex, endIndex);
	}
	
	public void TrimPositionData(double deltaThreshold)
	{
		//removing leading and trailing 0 deltas
		
		int startIndex = 0;
		for(;
			(startIndex < _valueTable.size() - 1) || (Math.abs(_valueTable.get(startIndex) - _valueTable.get(startIndex + 1)) < deltaThreshold);
			++startIndex);
		
		int endIndex = _valueTable.size() - 1;
		for(;
			(endIndex > startIndex + 1) || (Math.abs(_valueTable.get(endIndex) - _valueTable.get(endIndex - 1)) < deltaThreshold);
			--endIndex);
	}*/
	
	public String Serialize()
	{
		StringWriter sw = new StringWriter();

		for(int i = 0; i < _timeTable.size(); ++i) {
			sw.write(_timeTable.get(i).toString());
			sw.write(",");
			for(int j = 0; j < _valueCount; ++j)
			{
				if(j != 0)
					sw.write(",");
				sw.write(_valueTable.get(i).get(j).toString());
			}
			sw.write("\n");
		}
		sw.flush();
		
		return sw.toString();
	}
}
