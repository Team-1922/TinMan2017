package org.ozram1922.autonomous;

import java.util.ArrayList;

import org.ozram1922.OzMath;

public class VelocityControl {

	ArrayList<Double> _cruiseVelocity = new ArrayList<Double>();
	ArrayList<Double> _voltage = new ArrayList<Double>();
	
	
	private int GetNearestIndex(double velocity)
	{
		for(int i = 0; i < _cruiseVelocity.size(); i++)
		{
			if(_cruiseVelocity.get(i) > velocity)
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
		return -1;
	}
	
	public void Deserialize(String csv) throws NumberFormatException
	{
		String[] rows = csv.split("\n");
		for(int i = 0; i < rows.length; ++i)
		{
			String[] parts = rows[i].split(",");
			_cruiseVelocity.add(Double.parseDouble(parts[0]));
			_voltage.add(Double.parseDouble(parts[1]));
		}
	}
	
	public double GetVoltage(double velocity)
	{
		int nearestIndex = GetNearestIndex(velocity);
		if(nearestIndex == -1)
			return _voltage.get(_voltage.size() - 1);
		
		return OzMath.LRP(velocity, _cruiseVelocity.get(nearestIndex), _cruiseVelocity.get(nearestIndex + 1), _voltage.get(nearestIndex), _voltage.get(nearestIndex + 1));
	}
}
