package org.ozram1922.fieldsense;

public class Vector2d implements Vector<double> {
	
	private double _x = 0;
	private double _y = 0;
	
	@Override
	public double ValueAt(int index) throws Exception
	{
		if(index == 0)
			return _x;
		else if(index == 1)
			return _y;
		else
			throw new Exception("Index Out of Range");
	}
	
	@Override
	public int GetCount()
	{
		return 2;
	}
}