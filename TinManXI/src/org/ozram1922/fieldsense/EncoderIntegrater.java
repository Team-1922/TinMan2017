package org.ozram1922.fieldsense;

public class EncoderIntegrater {
	
	private double _distance;
	private double _direction;
	private Vector2d _position;
	
	private double prevL = 0;
	private double prevR = 0;
	
	public EncoderIntegrater(double distance, double initialPosition)
	{
		_distance = distance;
		VelocityIntegrater ints[] = new VelocityIntegrater[2];
		ints[0] = new VelocityIntegrater(initialPosition);
		ints[1] = new VelocityIntegrater(initialPosition);
		
		_positionIntegrater = new VectorIntegrater<VelocityIntegrater>(ints);
	}
	
	public void Cycle(double L, double R)
	{
		double dL = L - prevL;
		double dR = R - prevR;
		
		if(dL == 0 && dR == 0)
			return;
		
		//the change in left and right are actually the arc lengths of two concentric circles since encoder units are 
		//	essential continuous
		
		//See the math note sheet for proof
		double bigChange;
		double smallChange;
		if(dL > dR)
		{
			bigChange = dL;
			smallChange = dR;
		}
		else
		{
			bigChange = dR;
			smallChange = dL;
		}
		
		double deltaDirection = (bigChange - smallChange) / _distance;
		double deltaPositionMagnitude = 2.0 * (d / 2.0 + smallChange / deltaDirection) * Math.Sin(deltaDirection / 2.0);
		
		_direction += deltaDirection;
		_position.x += deltaPositionMagnitude * Math.Cos(deltaDirection);
		_position.y += deltaPositionMagnitude * Math.Sin(deltaDirection);
	}
	
	/**
	 * @return The distance between drive train wheels
	 */
	public double Distance()
	{
		return _distance;
	}
	
	public Vector<Double> Position()
	{
		return _positionIntegrater.
	}
}
