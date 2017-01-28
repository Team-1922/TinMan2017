package org.ozram1922.autonomous;

import java.io.StringWriter;
import java.util.ArrayList;

public class AutoRecorder {
	
	private ArrayList<Long> _timeTable = new ArrayList<Long>();
	private ArrayList<Double> _distanceTable = new ArrayList<Double>();
	private long _nanoTimeOffset = 0;
	
	public void StartRecording()
	{
		_nanoTimeOffset = System.nanoTime();
	}
	
	public void Update(double newVal)
	{
		_timeTable.add(System.nanoTime() - _nanoTimeOffset);
		_distanceTable.add(newVal);
	}
	
	public String Serialize()
	{
		StringWriter sw = new StringWriter();

		for(int i = 0; i < _timeTable.size(); ++i) {
			sw.write(_timeTable.get(i).toString());
			sw.write(",");
			sw.write(_distanceTable.get(i).toString());
			sw.write("\n");
		}
		sw.flush();
		
		return sw.toString();
	}
}
