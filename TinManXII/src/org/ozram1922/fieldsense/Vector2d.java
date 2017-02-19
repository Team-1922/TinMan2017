package org.ozram1922.fieldsense;

public class Vector2d implements Vector<double> {
	
	public double x = 0;
	public double y = 0;
	
	@Override
	public double ValueAt(int index) throws Exception
	{
		if(index == 0)
			return x;
		else if(index == 1)
			return y;
		else
			throw new Exception("Index Out of Range");
	}
	
	@Override
	public int GetCount()
	{
		return 2;
	}
}