package org.ozram1922.fieldsense;

public class EncoderIntegrater {
	
	private double _distance;
	private VectorIntegrater<VelocityIntegrater> _positionIntegrater;
	private IntegrationTargetDouble _direction;
	
	public EncoderIntegrater(double distance, double initialPosition)
	{
		_distance = distance;
		VelocityIntegrater ints[] = new VelocityIntegrater[2];
		ints[0] = new VelocityIntegrater(initialPosition);
		ints[1] = new VelocityIntegrater(initialPosition);
		
		_positionIntegrater = new VectorIntegrater<VelocityIntegrater>(ints);
		_direction = new IntegrationTargetDouble(0);
	}
	
	public void Cycle(double dLdt, double dRdt)
	{
		
	}
	
	public double Distance()
	{
		return _distance;
	}
	
	public Vector<Double> Position()
	{
		return _positionIntegrater.
	}
}
