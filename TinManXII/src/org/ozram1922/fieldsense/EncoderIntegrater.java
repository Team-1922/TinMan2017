package org.ozram1922.fieldsense;

public class EncoderIntegrater {
	
	private double _wheelSpacing;
	private double _direction;
	private Vector2d _position;
	
	private double _prevL = Double.NaN;
	private double _prevR = Double.NaN;
	
	public EncoderIntegrater(double wheelSpacing, Vector2d initialPosition)
	{
		_wheelSpacing = wheelSpacing;
		_position = initialPosition;
	}
	
	public void Cycle(double L, double R)
	{
		//acount for the first cycle
		if(_prevL == Double.NaN)
		{
			_prevL = L;
			_prevR = R;
			return;
		}
		double dL = L - _prevL;
		double dR = R - _prevR;
		
		if(dL == 0 && dR == 0)
			return;
		
		//the change in left and right are actually the arc lengths of two concentric circles since encoder units are 
		//	essential continuous
		
		//See the math note sheet for proof
		double bigChange = dR;
		double smallChange = dL;
		/*if(dL > dR)
		{
			bigChange = dL;
			smallChange = dR;
		}
		else
		{
			bigChange = dR;
			smallChange = dL;
		}*/
		
		double deltaDirection = (bigChange - smallChange) / _wheelSpacing;
		double deltaPositionMagnitude = 2.0 * (_wheelSpacing / 2.0 + smallChange / deltaDirection) * Math.sin(deltaDirection / 2.0);
		
		_direction += deltaDirection;
		_position.x += deltaPositionMagnitude * Math.cos(deltaDirection);
		_position.y += deltaPositionMagnitude * Math.sin(deltaDirection);
		
		//set the previous to the current
		_prevR = R;
		_prevL = L;
	}
	
	/**
	 * @return The distance between drive train wheels
	 */
	public double WheelSpacing()
	{
		return _wheelSpacing;
	}
	
	public Vector2d Position()
	{
		return _position;
	}
	
	public double Direction()
	{
		return _direction;
	}
}
