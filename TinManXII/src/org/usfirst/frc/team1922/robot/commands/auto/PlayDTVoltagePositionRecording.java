package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.Robot;

public class PlayDTVoltagePositionRecording extends LeftRightPlayback2 {
	
	public PlayDTVoltagePositionRecording(String leftVoltagePath, String rightVoltagePath, 
			String leftPositionPath, String rightPositionPath) {
		super(leftVoltagePath, rightVoltagePath, leftPositionPath, rightPositionPath);
		
	}
	
	protected void initializeInternal()
	{
		Robot.mDriveTrain.ResetEncoderPositions();
	}

    // Called repeatedly when this Command is scheduled to run
    protected void execute(double left1, double right1, double left2, double right2) {
		double pdpVoltage = Robot.mPDP.getVoltage();
		
		double leftBaseVoltage = left1;// / pdpVoltage;
		double rightBaseVoltage = right1;// / pdpVoltage;
		
		double leftPositionDifference = Robot.mDriveTrain.GetLeftPosition() - left2;
		double rightPositionDifference = Robot.mDriveTrain.GetRightPosition() - right2;
		
		double leftVoltage = leftBaseVoltage + Robot.mDriveTrain.dLToVoltage(leftPositionDifference);		
		double rightVoltage = rightBaseVoltage + Robot.mDriveTrain.dRToVoltage(rightPositionDifference);
		
    	Robot.mDriveTrain.TankControl(leftVoltage/pdpVoltage, rightVoltage/pdpVoltage);
    	
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.mDriveTrain.TankControl(0, 0);
    }

}
