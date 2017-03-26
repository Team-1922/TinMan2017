package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.Robot;

public class PlayDTVoltageVelocityRecording extends LeftRightPlayback2 {
	
	public PlayDTVoltageVelocityRecording(String leftVoltagePath, String rightVoltagePath, 
			String leftVelocityPath, String rightVelocityPath) {
		super(leftVoltagePath, rightVoltagePath, leftVelocityPath, rightVelocityPath);
		
	}

    // Called repeatedly when this Command is scheduled to run
    protected void execute(double left1, double right1, double left2, double right2) {
		double pdpVoltage = Robot.mPDP.getVoltage();
		
		double leftBaseVoltage = left1;// / pdpVoltage;
		double rightBaseVoltage = right1;// / pdpVoltage;
		
		double leftVelocityDifference = Robot.mDriveTrain.GetLeftVelocity() - left2;
		double rightVelocityDifference = Robot.mDriveTrain.GetRightVelocity() - right2;
		
		double leftVoltage = leftBaseVoltage + Robot.mDriveTrain.VelocityToVoltageL(leftVelocityDifference);		
		double rightVoltage = rightBaseVoltage + Robot.mDriveTrain.VelocityToVoltageR(rightVelocityDifference);
		
    	Robot.mDriveTrain.TankControl(leftVoltage/pdpVoltage, rightVoltage/pdpVoltage);
    	
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.mDriveTrain.TankControl(0, 0);
    }

}
