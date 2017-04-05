package org.usfirst.frc.team1922.robot.commands;

import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class BareTeleDrive extends Command {

    public BareTeleDrive() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.mDriveTrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
		double leftVal = Robot.oi.GetLeftJoystick().getY();
		double rightVal = Robot.oi.GetRightJoystick().getY();
    	if(Robot.mDriverCamera.IsReversed())
    	{
    		double tmp = rightVal;
    		rightVal = -leftVal;
    		leftVal = -tmp;
    	}
    	else
    	{
    		//TODO: don't account for rotation just yet!!
    		//only account for pixy cam if NOT reversed
    		double rotationalBias = Robot.mPixyCam.GetDTTwist();
    		leftVal += rotationalBias / 2;
    		rightVal -= rotationalBias / 2;
    	}
    	Robot.mDriveTrain.TankControl(leftVal, rightVal);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.mDriveTrain.TankControl(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
