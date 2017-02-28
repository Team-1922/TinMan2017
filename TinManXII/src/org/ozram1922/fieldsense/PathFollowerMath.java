package org.ozram1922.fieldsense;

import org.ozram1922.OzMath;
import org.ozram1922.Vector2d;

//note that this does NOT account for acceleration limitations that may cause sliding
public class PathFollowerMath {
	
	//sided Velocity is defined as a Vector2d where "x" = left side and "y" = right side
	public static Vector2d GetSidedVelocityTarget(Vector2d Pd1, Vector2d Vc, Vector2d sidedVc, Vector2d Pc, double dt, double d, double dThetaMax)
	{
		dThetaMax = Math.abs(dThetaMax);
		
		//see math sheet for proof
		Vector2d dPc = Vc.ScalarMultiply(dt);
		Vector2d dPm = Pd1.SubtractFromThis(Pc);
		
		double k = dPm.Magnitude()/dPc.Magnitude();
		double dTheta = Math.acos(
				dPc.Dot(dPm)/
				(dPc.Magnitude() * dPm.Magnitude()));
		
		dTheta = OzMath.Clamp(dTheta, -dThetaMax, dThetaMax);
		
		double velocityBias = (dTheta * d)/(2*dt);

		return sidedVc.ScalarMultiply(k).AddToThis(new Vector2d(-velocityBias, velocityBias));
	}

}
