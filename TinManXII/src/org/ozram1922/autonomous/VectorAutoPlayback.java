package org.ozram1922.autonomous;

import java.util.ArrayList;

import org.ozram1922.Vector2d;

public class VectorAutoPlayback extends AutoPlayback {
	
	public VectorAutoPlayback()
	{
		super(2);
	}
	
	public Vector2d GetVectorSetpoint()
	{
		ArrayList<Double> setpoint = GetSetpoint();
		return new Vector2d(setpoint.get(0), setpoint.get(1));
	}
}
