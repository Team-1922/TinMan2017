package org.ozram1922.fieldsense;

import java.math.BigDecimal;

import org.ozram1922.OzMath;
import org.ozram1922.OzUtils;
import org.ozram1922.Vector2d;

public class EncoderIntegrater {
	
	private BigDecimal _wheelSpacingB;
	private double _wheelSpacing;
	private double _direction = 0;
	private double _time;
	private Vector2d _position = new Vector2d();
	

	private double _prevTime = 0;
	private Vector2d _prevPosition = new Vector2d();
	
	private double _prevL = 0;
	private double _prevR = 0;
	
	private boolean _isFirstTime = true;
	
	public EncoderIntegrater(double wheelSpacing, Vector2d initialPosition, double initialDirection)
	{
		_wheelSpacingB = BigDecimal.valueOf(wheelSpacing);
		_wheelSpacing = wheelSpacing;
		_position.Set(initialPosition);
		_direction = initialDirection;
	}
	
	public EncoderIntegrater(double wheelSpacing, Vector2d initialPosition)
	{
		this(wheelSpacing, initialPosition, 0);
	}
	
	public void Cycle(double L, double R)
	{
		//acount for the first cycle
		if(_isFirstTime)
		{
			_isFirstTime = false;
			_prevL = L;
			_prevR = R;
			_prevTime = OzUtils.GetTime();
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
		
		double deltaDirection = BigDecimal.valueOf(bigChange - smallChange).divide(_wheelSpacingB).doubleValue();
		//if deltaDirection = 0, we get an indeterminite form (really big number * tiny number)
		double deltaPositionMagnitude = 0;
		if(deltaDirection == 0)//essentially: if(bigChange == smallChange) then we don't change direction and we our magnitude is equal to either side
		{
			deltaPositionMagnitude = bigChange;
		}
		else
		{
			//deltaPositionMagnitude = 2.0 * (_wheelSpacing / 2.0 + smallChange / deltaDirection) * Math.sin(deltaDirection / 2.0);
			//try using BigDecimals when multiplying huge numbers by tiny numbers and also get a more precise sine calculation (about 20 digits)
			deltaPositionMagnitude = 2.0 * 
					BigDecimal.valueOf(Math.sin(deltaDirection/2.0))
					.multiply(
							_wheelSpacingB.divide(OzMath.TWO, OzMath.HighPMathContext)
							.add(BigDecimal.valueOf(smallChange)
									.divide(BigDecimal.valueOf(deltaDirection), OzMath.HighPMathContext), OzMath.HighPMathContext), OzMath.HighPMathContext).doubleValue();
		}
		
		//update the previous position
		_prevPosition.Set(_position);
		_prevTime = _time;
		
		_direction += deltaDirection;
		_position.x += deltaPositionMagnitude * Math.cos(_direction);
		_position.y += deltaPositionMagnitude * Math.sin(_direction);
		_time = OzUtils.GetTime();
		
		
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
	
	public Vector2d Velocity()
	{
		return _position.SubtractFromThis(_prevPosition).ScalarDivide(_time - _prevTime);
	}
	
	public double Direction()
	{
		return _direction;
	}
}
