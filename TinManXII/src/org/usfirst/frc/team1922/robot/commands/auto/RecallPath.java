package org.usfirst.frc.team1922.robot.commands.auto;

import org.ozram1922.Vector2d;
import org.ozram1922.fieldsense.PathFollowerMath;
import org.usfirst.frc.team1922.robot.Robot;

/**
 * 
 */
public class RecallPath extends Vector2dPlaybackAsync {

	double dThetaDtMax = Math.PI;
	double accMax = 12; //inches/second^2 -> What should this be?
	
    public RecallPath(String filePath, int periodMS) {
    	super(filePath, periodMS);
    	requires(Robot.mDriveTrain);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute(Vector2d value) {
    	
    	Vector2d sidedVelocity = PathFollowerMath.GetSidedVelocityTarget(
    			value, 												//desired position
    			Robot.mFieldState.Velocity(),						//current absolute velocity
    			new Vector2d(										//current left/right velocities 
    					Robot.mDriveTrain.GetLeftVelocity(),  
    					Robot.mDriveTrain.GetRightPosition()), 
    			Robot.mFieldState.Position(), 						//current position 
    			_autoPlayback.GetNextTimeDuration(),				//next cycle time duration
    			20.5, 												//drive train width
    			dThetaDtMax,										//maximum rotation change per cycle
    			accMax); 											//maximum acceleration
    	double leftVoltage = Robot.mDriveTrain.VoltageLookup(sidedVelocity.x);
    	double rightVoltage = Robot.mDriveTrain.VoltageLookup(sidedVelocity.y);
    	
		double pdpVoltage = Robot.mPDP.getVoltage();
    	Robot.mDriveTrain.TankControl(leftVoltage / pdpVoltage, rightVoltage / pdpVoltage);    	
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    	super.end();
    	Robot.mDriveTrain.TankControl(0, 0);
    }
}
