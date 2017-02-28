package org.ozram1922.fieldsense;

import org.ozram1922.OzMath;
import org.ozram1922.Vector2d;
import org.ozram1922.autonomous.VectorAutoPlayback;

public class PathFollower {
	
	private VectorAutoPlayback _positionPlayback;
	private double _d;
	private double _dThetaMax;
	
	public PathFollower(VectorAutoPlayback positionPlayback, double dtWidth, double maxCycleDirectionChange)
	{
		_positionPlayback = positionPlayback;
		_d = dtWidth;
		_dThetaMax = Math.abs(maxCycleDirectionChange);
	}
	
	public void StartPlayback()
	{
		_positionPlayback.StartPlayback();
	}
	
	public boolean IsFinished()
	{
		return _positionPlayback.IsFinished();
	}
	
	/*public Vector2d GetVelocityTarget(Vector2d currentVelocity, Vector2d Pc, double dt)
	{
		
		return new Vector2d();
	}*/
	
	//sided Velocity is defined as a Vector2d where "x" = left side and "y" = right side
	public Vector2d GetSidedVelocityTarget(Vector2d Vc, Vector2d sidedVc, Vector2d Pc, double dt)
	{
		//see math sheet for proof
		Vector2d dPc = Vc.ScalarMultiply(dt);
		Vector2d Pd = _positionPlayback.GetSetpoint();
		Vector2d dPm = Pd.SubtractFromThis(Pc);
		
		double k = dPm.Magnitude()/dPc.Magnitude();
		double dTheta = Math.acos(
				dPc.Dot(dPm)/
				(dPc.Magnitude() * dPm.Magnitude()));
		
		dTheta = OzMath.Clamp(dTheta, -_dThetaMax, _dThetaMax);
		
		double velocityBias = (dTheta * _d)/(2*dt);

		return sidedVc.ScalarMultiply(k).AddToThis(new Vector2d(-velocityBias, velocityBias));
	}

}
