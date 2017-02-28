package org.ozram1922.fieldsense;

import org.ozram1922.OzMath;
import org.ozram1922.Vector2d;

//note that this does NOT account for acceleration limitations that may cause sliding
public class PathFollowerMath {
	
	//sided Velocity is defined as a Vector2d where "x" = left side and "y" = right side
	public static Vector2d GetSidedVelocityTarget(Vector2d Pd1, Vector2d Vc, Vector2d sidedVc, Vector2d Pc, double dt, double d, double dThetaDtMax, double accMax)
	{
		//we are given the maximum rates of change for the two pieces of information, so we must 
		//	multiply by the time period to ensure we are only changing by the maximum amount
		double dThetaMax = Math.abs(dThetaDtMax) * dt;
		double dVcMax = Math.abs(accMax) * dt;
		
		//see math sheet for proof
		
		//projected change in position
		Vector2d dPc = Vc.ScalarMultiply(dt);
		
		//required change in position to get back on path
		Vector2d dPm = Pd1.SubtractFromThis(Pc);
		
		//don't use 'k', because this multiplicative constant will take existing rotation and magnify it.
		//	using acceleration as an additive constant will not change the rate of rotation.  In addition,
		//	if Vc is very small, k becomes very large and multiplying really big and really small numbers 
		//	can cause issues.
		//double k = dPm.Magnitude()/dPc.Magnitude();
		
		//acceleration is equal to ((dPm/dt)-Vc)/dt , but we don't care about acceleration, we care about change in velocity.
		//	By changing to acceleration, we are no longer multiplying the sidedVc by anything, but rather we are 
		//	adding to it
		double dVc = OzMath.Clamp(dPm.ScalarDivide(dt).SubtractFromThis(Vc).Magnitude(), -dVcMax, dVcMax);
		
		//angle between two vectors
		double dTheta = Math.acos(
				dPc.Dot(dPm)/
				(dPc.Magnitude() * dPm.Magnitude()));
		
		dTheta = OzMath.Clamp(dTheta, -dThetaMax, dThetaMax);
		
		//rotational velocity bias
		double velocityBias = (dTheta * d)/(2*dt);

		return sidedVc.AddToThis(new Vector2d(dVc,dVc)).AddToThis(new Vector2d(-velocityBias, velocityBias));
		/*
		 * This works as an adaptive system, because the acceleration is added to the current velocity of each side, but the 
		 * 		vector velocity is used to calculate the acceleration required.  This means the target velocities on each side
		 * 		will be less than or more than the target vector velocity reactively.  To prevent this reactive system to oscillate
		 * 		or other wise be unstable, a maximum acceleration and rotational velocity change is employed.
		 */
	}

}
